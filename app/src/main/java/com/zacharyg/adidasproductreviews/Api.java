package com.zacharyg.adidasproductreviews;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;

import java.util.List;

public class Api {
    private static final String TAG = "Api";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String URL_BASE_PRODUCTS = "http://10.0.2.2:3001";
    private static final String URL_BASE_REVIEWS  = "http://10.0.2.2:3002";

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
//                .load(GET, URL_BASE_REVIEWS + "/reviews/" + productID)
                .load(GET, URL_BASE_REVIEWS + "/reviews/" + productID)
                .as(new TypeToken<List<Review>>(){})
                .setCallback((e, reviews) -> {
                    if (reviews != null) {
                        Log.d(TAG, "Reviews not null: " + reviews.size());
                        callback.onSuccess(reviews);
                        return;
                    }
                    Log.d(TAG, "Review fetch failed: " + e.toString());
                    callback.onFailure(e.getMessage());
                });
    }

    private void postReview(final Context context,
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
