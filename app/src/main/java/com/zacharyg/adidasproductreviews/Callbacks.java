package com.zacharyg.adidasproductreviews;

import java.util.List;

public class Callbacks {
    private interface NetworkRequestErrorCallback {
        void onFailure(int errorCode, String errorMessage);
    }

    public interface GetProductsComplete extends NetworkRequestErrorCallback {
        void onSuccess(List<Product> products);
    }
}
