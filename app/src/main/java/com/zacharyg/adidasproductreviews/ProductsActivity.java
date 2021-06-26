package com.zacharyg.adidasproductreviews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ProductsActivity extends AppCompatActivity {
    private LinearLayout llRoot;

    private ImageView ivSearch;
    private EditText etSearch;

    private AnimatedVectorDrawable avdIconToBar;
    private AnimatedVectorDrawable avdBarToIcon;

    private Interpolator interpolator;

    private final int duration = 800;

    private boolean searchBarExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llRoot   = findViewById(R.id.ll_root);
        ivSearch = findViewById(R.id.iv_search);
        etSearch = findViewById(R.id.et_search);

        avdIconToBar = (AnimatedVectorDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.anim_icon_to_bar, null);
        avdBarToIcon = (AnimatedVectorDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.anim_bar_to_icon, null);

        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);

        etSearch.setEnabled(false);

        setupListeners();

        loadInitialSearchBarAnimation();
        loadProductsFragment();
    }

    private void loadProductsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ProductsFragment productsFragment = new ProductsFragment();
        fragmentTransaction.add(R.id.fl_products, productsFragment);
        fragmentTransaction.commit();
    }

    private void setupListeners() {
        llRoot.setOnClickListener(v -> {
            if (etSearch.getText().length() == 0) {
                hideSearchBar();
            }
            etSearch.clearFocus();
            ivSearch.requestFocus();
        });

        ivSearch.setOnClickListener(v -> {
            if (!searchBarExpanded) {
                showSearchBar();
            } else {
                etSearch.requestFocus();
            }
        });

        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (hasFocus) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }
            else {
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
            }
        });
    }

    private void loadInitialSearchBarAnimation() {
        ivSearch.setImageDrawable(avdBarToIcon);
        avdBarToIcon.start();
        ivSearch.animate().setDuration(duration-400).setInterpolator(interpolator);
        etSearch.setAlpha(0f);
    }

    public void showSearchBar() {
        ivSearch.setImageDrawable(avdIconToBar);
        avdIconToBar.start();
        ivSearch.animate().setDuration(duration).setInterpolator(interpolator);
        etSearch.animate().alpha(1f).setStartDelay(duration - 100).setDuration(100).setInterpolator(interpolator);
        etSearch.setEnabled(true);
        etSearch.requestFocus();
        searchBarExpanded = true;
    }

    public void hideSearchBar() {
        if (searchBarExpanded) {
            ivSearch.setImageDrawable(avdBarToIcon);
            avdBarToIcon.start();
            ivSearch.animate().setDuration(duration).setInterpolator(interpolator);
            etSearch.setAlpha(0f);
            etSearch.setEnabled(false);
            searchBarExpanded = false;
        }
    }
}