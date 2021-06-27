package com.zacharyg.adidasproductreviews;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Arrays;
import java.util.List;

public class ProductsRemoteRepo {
    private static final String TAG = "ProductsRemoteRepo";
    private static final String GET = "GET";
    private static final String URL_BASE = "http://10.0.2.2:3001";

    public static void getProducts(final Context context,
                                   final Callbacks.GetProductsComplete callback) {
        Log.d(TAG, "getProducts: " + URL_BASE + "/product");

        // Perform a GET request to fetch products from the server
        Ion.with(context)
                .load(GET, URL_BASE + "/product")
                .as(new TypeToken<List<Product>>(){})
                .setCallback((e, products) -> {
                    if (products != null) {
                        callback.onSuccess(products);
                        return;
                    }
                    Log.d(TAG, "Product fetch failed: " + e.toString());
                    callback.onFailure(-1, e.getMessage());
                });
    }
}
