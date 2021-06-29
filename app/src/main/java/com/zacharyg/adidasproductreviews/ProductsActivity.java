package com.zacharyg.adidasproductreviews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ProductsActivity extends AppCompatActivity {
    private RelativeLayout rlTopBar;

    private FrameLayout flProducts;

    private ImageView ivSearch;
    private EditText etSearch;

    private AnimatedVectorDrawable avdIconToBar;
    private AnimatedVectorDrawable avdBarToIcon;

    private Interpolator interpolator;

    private ProductsFragment productsFragment;

    private final int duration = 800;

    private boolean searchBarExpanded = false;

    private boolean topBarAnimationInProgress = false;
    private boolean productsAnimationInProgress = false;
    private boolean appJustLaunched = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        setupViews();
        initVariables();
        setupListeners();
        loadInitialSearchBarAnimation();
        loadProductsFragment();
    }

    private void setupViews() {
        rlTopBar = findViewById(R.id.rl_top_bar);

        flProducts = findViewById(R.id.fl_products);

        ivSearch = findViewById(R.id.iv_search);
        etSearch = findViewById(R.id.et_search);

        etSearch.setEnabled(false);
    }

    private void initVariables() {
        avdIconToBar = (AnimatedVectorDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.anim_icon_to_bar, null);
        avdBarToIcon = (AnimatedVectorDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.anim_bar_to_icon, null);

        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
    }

    private void loadProductsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        productsFragment = new ProductsFragment();
        fragmentTransaction.add(R.id.fl_products, productsFragment);
        fragmentTransaction.commit();
    }

    public void animateTopBar(boolean show) {
        if (topBarAnimationInProgress) { return; }

        topBarAnimationInProgress = true;

        if (show) {
            rlTopBar.setVisibility(View.VISIBLE);
        }
        rlTopBar.animate()
                .translationY(show ? 0 : -rlTopBar.getHeight())
                .alpha(show ? 1f : 0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!show) {
                            rlTopBar.setVisibility(View.GONE);
                        }
                        topBarAnimationInProgress = false;
                    }
                });
    }

    public void moveProducts(boolean down) {
        if (appJustLaunched && down) {
            flProducts.setTranslationY(rlTopBar.getHeight());
            appJustLaunched = false;
            return;
        }
        if (productsAnimationInProgress) { return; }

        productsAnimationInProgress = true;

        flProducts.animate()
                .translationY(down ? rlTopBar.getHeight() : 0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        productsAnimationInProgress = false;
                    }
                });
    }

    private void setupListeners() {
        rlTopBar.setOnClickListener(v -> onTopBarClick());

        ivSearch.setOnClickListener(v -> onSearchClick());

        etSearch.setOnFocusChangeListener(this::onFocusChange);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productsFragment.filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (etSearch.getText().length() == 0) {
                    hideSearchBar();
                }
            }
            return false;
        });
    }

    private void onTopBarClick() {
        if (etSearch.getText().length() == 0) {
            hideSearchBar();
        }
        etSearch.clearFocus();
        ivSearch.requestFocus();
    }

    private void onSearchClick() {
        if (!searchBarExpanded) {
            showSearchBar();
        } else {
            etSearch.requestFocus();
        }
    }

    private void onFocusChange(View view, boolean hasFocus) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (hasFocus) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
        else {
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    private void loadInitialSearchBarAnimation() {
        ivSearch.setImageDrawable(avdBarToIcon);
        avdBarToIcon.start();
        ivSearch.animate().setDuration(duration - 400).setInterpolator(interpolator);
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