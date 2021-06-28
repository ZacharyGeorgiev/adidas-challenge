package com.zacharyg.adidasproductreviews;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Api {
    private static final String TAG = "Api";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String URL_BASE_PRODUCTS = "http://10.0.2.2:3001";
    private static final String URL_BASE_REVIEWS  = "http://10.0.2.2:3002";
    private static final String URL_RANDOM_USER_API  = "https://randomuser.me/api";

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
                                getRandomUsernameAndCountry(context, (username, country) -> {
                                    review.setUsername(username);
                                    review.setCountry(country);

                                    additionalDataSetCount.addAndGet(1);

                                    if (additionalDataSetCount.get() == reviews.size()) {
                                        callback.onSuccess(reviews);
                                    }
                                });
                            }
                        } else {
                            callback.onSuccess(reviews);
                        }
                        return;
                    }
                    Log.d(TAG, "Review fetch failed: " + e.toString());
                    callback.onFailure(e.getMessage());
                });
    }

    private static void getRandomUsernameAndCountry(Context context,
                                                    Callbacks.GetRandomUsernameAndCountry callback) {
        Ion.with(context)
                .load(GET, URL_RANDOM_USER_API)
                .asJsonObject()
                .withResponse()
                .setCallback((e1, result) -> {
                    if (result != null) {
                        JsonObject results = result.getResult().get("results").getAsJsonArray().get(0).getAsJsonObject();
                        JsonObject location = results.get("location").getAsJsonObject();
                        JsonObject login = results.get("login").getAsJsonObject();

                        String username = login.get("username").getAsString();
                        String country = location.get("country").getAsString();

                        callback.onSuccess(username, country);
                    } else {
                        String[] fallbackUsernames = { "Omenforcer", "Flamingoat", "Vulturret", "Tangoddess", "AmusedPorcupine", "NaiveFry", "HilariousBison", "FrightenedFury", "FalseLeaf", "MorningCub" };
                        String[] fallbackCountries = { "Russia", "Ukraine", "France", "Spain", "Sweden", "Norway", "Germany", "Finland", "Netherlands", "United Kingdom" };

                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        callback.onSuccess(fallbackUsernames[random.nextInt(0, fallbackUsernames.length)],
                                           fallbackCountries[random.nextInt(0, fallbackCountries.length)]);
                    }
                });
    }

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
//                    Log.d(TAG, "Result: " + result.toString());
                });
    }
}
