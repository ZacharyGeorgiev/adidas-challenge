package com.zacharyg.adidasproductreviews;

import java.util.List;

public class Callbacks {
    private interface NetworkRequestErrorCallback {
        void onFailure(int errorCode, String errorMessage);
    }

    private interface RequestErrorCallback {
        void onFailure(String errorMessage);
    }

    public interface GetProductsComplete extends RequestErrorCallback {
        void onSuccess(List<Product> products);
    }
}