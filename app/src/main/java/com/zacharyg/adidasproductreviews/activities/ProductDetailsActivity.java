package com.zacharyg.adidasproductreviews.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zacharyg.adidasproductreviews.dialogs.AddReviewBottomSheet;
import com.zacharyg.adidasproductreviews.models.Product;
import com.zacharyg.adidasproductreviews.R;
import com.zacharyg.adidasproductreviews.fragments.ReviewsFragment;

import java.io.Serializable;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageButton ibBack;

    private Button btnAddReview;

    private Product product;

    private ReviewsFragment reviewsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Serializable serializable = getIntent().getSerializableExtra("product");
        if (serializable != null) {
            this.product = (Product) serializable;
        }

        setupViews();
        setupListeners();
        updateStatusBarColor();
        loadReviewsFragment();
    }

    private void setupViews() {
        ibBack                 = findViewById(R.id.ib_back);

        btnAddReview           = findViewById(R.id.btn_add_review);

        ImageView ivProduct    = findViewById(R.id.iv_product);

        TextView tvPrice       = findViewById(R.id.tv_price);
        TextView tvName        = findViewById(R.id.tv_name);
        TextView tvDescription = findViewById(R.id.tv_description);

        Glide.with(this)
                .load(product.getImageUrl())
                .into(ivProduct);

        tvPrice.setText(String.format(Locale.ENGLISH,"%s%d.00", product.getCurrency(), product.getPrice()));
        tvName.setText(product.getName());
        tvDescription.setText(product.getDescription());
    }

    private void setupListeners() {
        ibBack.setOnClickListener(v -> finish());

        btnAddReview.setOnClickListener(v -> {
            AddReviewBottomSheet addReviewBottomSheet = AddReviewBottomSheet.newInstance(product.getId());
            if (getFragmentManager() != null) {
                addReviewBottomSheet.show(getSupportFragmentManager(), "addReviewBottomSheet");
            }
        });
    }

    private void loadReviewsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        reviewsFragment = ReviewsFragment.newInstance(product.getId());

        fragmentTransaction.add(R.id.fl_reviews, reviewsFragment);
        fragmentTransaction.commit();
    }

    public void refreshReviews() {
        reviewsFragment.refreshReviews();
    }

    private void updateStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.product_background));
    }
}