package com.zacharyg.adidasproductreviews;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review implements Serializable {
    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("rating")
    @Expose
    private int rating;
    @SerializedName("text")
    @Expose
    private String text;

    /**
     * No args constructor for use in serialization
     */
    public Review() {
    }

    public Review(String productId, String locale, int rating, String text) {
        super();
        this.productId = productId;
        this.locale = locale;
        this.rating = rating;
        this.text = text;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}