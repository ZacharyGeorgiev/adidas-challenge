package com.zacharyg.adidasproductreviews.helpers;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.zacharyg.adidasproductreviews.Callbacks;
import com.zacharyg.adidasproductreviews.models.Product;
import com.zacharyg.adidasproductreviews.models.Review;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Api {
    private static final String TAG = "Api";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String IP_ADDRESS = "10.0.2.2";
//    private static final String IP_ADDRESS = "192.168.1.126";
    private static final String URL_BASE_PRODUCTS = "http://" + IP_ADDRESS + ":3001";
    private static final String URL_BASE_REVIEWS  = "http://" + IP_ADDRESS + ":3002";
    private static final String URL_RANDOM_USER_API  = "https://randomuser.me/api";

    private static final String[] FALLBACK_USERNAMES = { "Omenforcer", "Flamingoat", "Vulturret", "Tangoddess", "AmusedPorcupine", "NaiveFry", "HilariousBison", "FrightenedFury", "FalseLeaf", "MorningCub" };
    private static final String[] FALLBACK_COUNTRIES = { "Russia", "Ukraine", "France", "Spain", "Sweden", "Norway", "Germany", "Finland", "Netherlands", "United Kingdom" };

    /**
     * Performs a GET request to the server to fetch the product list
     * @param context - the context to be used for the request
     * @param callback - the callback where the response will be handled
     */
    public static void getProducts(final Context context,
                                   final Callbacks.GetProductsComplete callback) {
        Log.d(TAG, "getProducts: " + URL_BASE_PRODUCTS + "/product");

        // Perform a GET request to fetch products from the server
        Ion.with(context)
                .load(GET, URL_BASE_PRODUCTS + "/product")
                .as(new TypeToken<List<Product>>(){})
                .setCallback((e, products) -> {
                    if (products != null) {
                        callback.onSuccess(products);
                        return;
                    }
                    Log.d(TAG, "Product fetch failed: " + e.toString());
                    callback.onFailure(e.getMessage());
                });
    }

    /**
     * Performs a GET request to the server to fetch the list of reviews for a product
     * @param context - the context to be used for the request
     * @param callback - the callback where the response will be handled
     * @param productID - the ID of the product for which the reviews should be fetched
     */
    public static void getReviews(final Context context,
                                  final Callbacks.GetReviewsComplete callback,
                                  String productID) {
        Log.d(TAG, "getReviews: " + URL_BASE_REVIEWS + "/reviews/" + productID);

        // Perform a GET request to fetch reviews from the server
        Ion.with(context)
                .load(GET, URL_BASE_REVIEWS + "/reviews/" + productID)
                .as(new TypeToken<List<Review>>(){})
                .setCallback((e, reviews) -> {
                    if (reviews != null) {
                        if (reviews.size() >= 1) {
                            AtomicInteger additionalDataSetCount = new AtomicInteger();
                            for (Review review: reviews) {
                                // Obtain a random username and country for each review to make it look more personal
                                getRandomUsernameAndCountry(context, (username, country) -> {
                                    review.setUsername(username);
                                    review.setCountry(country);

                                    additionalDataSetCount.addAndGet(1);

                                    if (additionalDataSetCount.get() == reviews.size()) {
                                        // Pass the reviews back to the callback once the additional data has been set for all of them
                                        callback.onSuccess(reviews);
                                    }
                                });
                            }
                        } else {
                            // The review list is empty so there is no need to obtain additional data
                            callback.onSuccess(reviews);
                        }
                        return;
                    }
                    Log.d(TAG, "Review fetch failed: " + e.toString());
                    callback.onFailure(e.getMessage());
                });
    }

    /**
     * Makes a GET request to a random user API and handles the JSON response to get a username and a country
     * @param context - the context to be used for the request
     * @param callback - the callback where the username and country are passed
     */
    private static void getRandomUsernameAndCountry(Context context,
                                                    Callbacks.GetRandomUsernameAndCountry callback) {
        Ion.with(context)
                .load(GET, URL_RANDOM_USER_API)
                .asJsonObject()
                .withResponse()
                .setCallback((e1, result) -> {
                    if (result != null) {
                        try {
                            JsonObject results = result.getResult().get("results").getAsJsonArray().get(0).getAsJsonObject();
                            JsonObject location = results.get("location").getAsJsonObject();
                            JsonObject login = results.get("login").getAsJsonObject();

                            String username = login.get("username").getAsString();
                            String country = location.get("country").getAsString();

                            callback.onSuccess(username, country);
                        } catch (Exception ex) {
                            callback.onSuccess(getRandomUsername(), getRandomCountry());
                        }
                    } else {
                        // In case the call fails we get a random username and country from a hardcoded list
                        callback.onSuccess(getRandomUsername(), getRandomCountry());
                    }
                });
    }

    /**
     * Selects a random username from the fallback usernames list and returns it
     * @return - a random username
     */
    private static String getRandomUsername() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return FALLBACK_USERNAMES[random.nextInt(0, FALLBACK_USERNAMES.length)];
    }

    /**
     * Selects a random country from the fallback countries list and returns it
     * @return - a random country
     */
    private static String getRandomCountry() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return FALLBACK_COUNTRIES[random.nextInt(0, FALLBACK_COUNTRIES.length)];
    }

    /**
     * Performs a POST request to the server to post a review of a product
     * @param context - the context to be used for the request
     * @param callback - the callback where the response will be handled
     * @param productID - the product for which to post a review
     * @param rating - the star rating (1-5) for the product
     * @param review - the text of the review
     */
    public static void postReview(final Context context,
                                  final Callbacks.PostReviewComplete callback,
                                  String productID,
                                  int rating,
                                  String review) {
        JsonObject reviewBody = new JsonObject();

        reviewBody.addProperty("productId", productID);
        reviewBody.addProperty("locale", "en-US,en;q=0.9");
        reviewBody.addProperty("rating", rating);
        reviewBody.addProperty("text", review);

        Ion.with(context)
                .load(POST, URL_BASE_REVIEWS + "/reviews/" + productID)
                .setJsonObjectBody(reviewBody)
                .asJsonObject()
                .withResponse()
                .setCallback((e, result) -> {
                    if (e == null) {
                        callback.onSuccess();
                        return;
                    }
                    callback.onFailure(e.getMessage());
                });
    }
}
