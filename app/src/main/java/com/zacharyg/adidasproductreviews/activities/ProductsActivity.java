package com.zacharyg.adidasproductreviews.activities;

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

import com.zacharyg.adidasproductreviews.fragments.ProductsFragment;
import com.zacharyg.adidasproductreviews.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        setupViews();
        initAnimationVariables();
        setupListeners();
        loadInitialSearchBarAnimation();
        loadProductsFragment();
    }

    private void setupViews() {
        rlTopBar   = findViewById(R.id.rl_top_bar);

        flProducts = findViewById(R.id.fl_products);

        ivSearch   = findViewById(R.id.iv_search);
        etSearch   = findViewById(R.id.et_search);

        etSearch.setEnabled(false);
    }

    /**
     * Initializes variables needed for the animations
     */
    private void initAnimationVariables() {
        avdIconToBar = (AnimatedVectorDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.anim_icon_to_bar, null);
        avdBarToIcon = (AnimatedVectorDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.anim_bar_to_icon, null);

        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
    }

    /**
     * Loads the product fragment into its designated container
     */
    private void loadProductsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        productsFragment = new ProductsFragment();
        fragmentTransaction.add(R.id.fl_products, productsFragment);
        fragmentTransaction.commit();
    }

    /**
     * Shows/hides the top bar with an animation
     * @param show - specifies whether the top bar should be shown or hidden
     */
    public void animateTopBar(boolean show) {
        // Only perform one animation at a time
        if (topBarAnimationInProgress) { return; }

        topBarAnimationInProgress = true;

        if (show) {
            // The top bar is currently hidden so we want to show it before performing the animation
            rlTopBar.setVisibility(View.VISIBLE);
        }

        // Animate the showing/hiding of the top bar
        rlTopBar.animate()
                .translationY(show ? 0 : -rlTopBar.getHeight())
                .alpha(show ? 1f : 0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!show) {
                            // We just animated the top bar out of the view -> hide it entirely
                            rlTopBar.setVisibility(View.GONE);
                        }
                        topBarAnimationInProgress = false;
                    }
                });
    }

    /**
     * Moves the products layout up/down
     * @param down - specifies whether the layout should be moved up or down
     */
    public void moveProducts(boolean down) {
        // Only perform one animation at a time
        if (productsAnimationInProgress) { return; }

        productsAnimationInProgress = true;

        if (!down) {
            // The products layout initially has a margin of 70dp in order to be positioned below the top bar
            // Remove the margin
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)flProducts.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            flProducts.setLayoutParams(params);
        }

        // Move the products layout up/down
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
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (etSearch.getText().length() == 0) {
                    // The user has not typed a search query -> we can hide the search bar
                    hideSearchBar();
                }
            }
            return false;
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the list of products every time the user types a character
                productsFragment.filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    /**
     * Triggered when the user clicks on the top bar of the activity (with the logo and search bar)
     */
    private void onTopBarClick() {
        if (etSearch.getText().length() == 0) {
            // The user has not typed a search query -> we can hide the search bar
            hideSearchBar();
        }
        // Clear the focus of the search bar
        etSearch.clearFocus();
        ivSearch.requestFocus();
    }

    /**
     * Triggered when the user clicks on the search bar/icon
     */
    private void onSearchClick() {
        if (!searchBarExpanded) {
            // Only perform the transform animation when necessary
            showSearchBar();
        } else {
            // The search bar is expanded and the user clicked on it
            // Request the edit text to be focused
            etSearch.requestFocus();
        }
    }

    /**
     * Triggered when the search bar gets focused/unfocused
     * Handles showing and hiding of the keyboard
     */
    private void onFocusChange(View view, boolean hasFocus) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (hasFocus) {
            // Show the keyboard
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        } else {
            // Hide the keyboard when the search bar is no longer focused
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    /**
     * Performs the initial search bar animation
     */
    private void loadInitialSearchBarAnimation() {
        ivSearch.setImageDrawable(avdBarToIcon);
        avdBarToIcon.start();
        ivSearch.animate().setDuration(duration - 400).setInterpolator(interpolator);
        etSearch.setAlpha(0f);
    }

    /**
     * Performs the animation that transforms the search icon into a bar
     */
    public void showSearchBar() {
        ivSearch.setImageDrawable(avdIconToBar);
        avdIconToBar.start();
        ivSearch.animate().setDuration(duration).setInterpolator(interpolator);
        etSearch.animate().alpha(1f).setStartDelay(duration - 100).setDuration(100).setInterpolator(interpolator);
        etSearch.setEnabled(true);
        etSearch.requestFocus();
        searchBarExpanded = true;
    }

    /**
     * Performs the animation that transforms the search bar into an icon
     */
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