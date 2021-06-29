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

    private String username;
    private String country;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}