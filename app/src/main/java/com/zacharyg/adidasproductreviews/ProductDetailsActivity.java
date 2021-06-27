package com.zacharyg.adidasproductreviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageButton ibBack;

    private ImageView ivProduct;

    private TextView tvPrice;
    private TextView tvName;
    private TextView tvDescription;

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ibBack        = findViewById(R.id.ib_back);

        ivProduct     = findViewById(R.id.iv_product);

        tvPrice       = findViewById(R.id.tv_price);
        tvName        = findViewById(R.id.tv_name);
        tvDescription = findViewById(R.id.tv_description);

        Serializable serializable = getIntent().getSerializableExtra("product");
        if (serializable != null) {
            this.product = (Product) serializable;

            Glide.with(this)
                    .load(product.getImageUrl())
                    .into(ivProduct);

            tvPrice.setText(String.format(Locale.ENGLISH,"%s%d.00", product.getCurrency(), product.getPrice()));
            tvName.setText(product.getName());
            tvDescription.setText(product.getDescription());
        }

        ibBack.setOnClickListener(v -> finish());

        updateStatusBarColor();
    }

    private void updateStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.product_background));
    }
}