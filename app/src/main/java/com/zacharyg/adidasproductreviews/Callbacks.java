package com.zacharyg.adidasproductreviews;

import com.zacharyg.adidasproductreviews.models.Product;
import com.zacharyg.adidasproductreviews.models.Review;

import java.util.List;

public class Callbacks {
    private interface RequestErrorCallback {
        void onFailure(String errorMessage);
    }

    public interface GetProductsComplete extends RequestErrorCallback {
        void onSuccess(List<Product> products);
    }

    public interface GetReviewsComplete extends RequestErrorCallback {
        void onSuccess(List<Review> reviews);
    }

    public interface PostReviewComplete extends RequestErrorCallback {
        void onSuccess();
    }

    public interface GetRandomUsernameAndCountry {
        void onSuccess(String username, String country);
    }
}
