package com.zacharyg.adidasproductreviews.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zacharyg.adidasproductreviews.Callbacks;
import com.zacharyg.adidasproductreviews.adapters.ProductsAdapter;
import com.zacharyg.adidasproductreviews.R;
import com.zacharyg.adidasproductreviews.activities.ProductDetailsActivity;
import com.zacharyg.adidasproductreviews.activities.ProductsActivity;
import com.zacharyg.adidasproductreviews.helpers.Api;
import com.zacharyg.adidasproductreviews.helpers.Utils;
import com.zacharyg.adidasproductreviews.models.Product;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Fragment used to display all available products
 */
public class ProductsFragment extends Fragment implements ProductsAdapter.OnProductClickListener, ServerIssueFragment.OnReloadListener {
    private static final String TAG = "ProductsFragment";

    private Context context;

    private RecyclerView rvProducts;

    private RelativeLayout rlServerIssue;

    private LinearLayout llNoResults;

    private FrameLayout flLoading;

    private TextView tvNoResults;

    private LinearLayoutManager layoutManager;

    private ProductsAdapter productsAdapter;

    private List<Product> products;

    private boolean justLaunched = true;

    public ProductsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        setupViews(view);
        setupListeners();
        loadFragments();
        fetchProducts();

        return view;
    }

    private void setupViews(View view) {
        // Create the layout manager for the recycler view
        layoutManager = new LinearLayoutManager(requireContext());

        rlServerIssue = view.findViewById(R.id.rl_server_issue);

        llNoResults   = view.findViewById(R.id.ll_no_results);

        flLoading     = view.findViewById(R.id.fl_loading);

        tvNoResults   = view.findViewById(R.id.tv_no_results);

        rvProducts    = view.findViewById(R.id.rv_products);

        // Configure the recycler view
        rvProducts.setLayoutManager(layoutManager);
    }

    /**
     * Setup the onScroll listener for the recycler view
     */
    private void setupListeners() {
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (getActivity() != null) {
                    try {
                        ProductsActivity productsActivity = (ProductsActivity) getActivity();

                        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                            if (justLaunched) {
                                /* When the app is launched the onScroll is triggered and since the first item is visible
                                 * this clause will be entered. Since we don't want to move the products layout before the user
                                 * has scrolled we don't do anything the first time this clause is entered.
                                 */
                                justLaunched = false;
                                return;
                            }
                            // If we are at the top of the page we have to move the product layout below the top bar to prevent hidden products
                            productsActivity.moveProducts(true);
                            return;
                        }

                        if (dy > 0) {
                            // The user scrolled down -> hide the top bar and move the product layout up to take up the full screen
                            productsActivity.animateTopBar(false);
                            productsActivity.moveProducts(false);
                        } else if (dy < 0) {
                            // The user scrolled up -> show the top bar
                            productsActivity.animateTopBar(true);
                        }
                    } catch (Exception ex) {
                        Log.d(TAG, "Exception: " + ex.getMessage());
                    }
                }

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onProductClick(Product product) {
        // Check if internet is available before showing the product details activity
        if (Utils.internetIsUnavailable(context)) {
            Utils.showNoInternetToast(getActivity());
            return;
        }
        Intent productDetailsIntent = new Intent(context, ProductDetailsActivity.class);
        // Pass the product which was clicked to the product details activity
        productDetailsIntent.putExtra("product", product);
        // Start the product details activity
        startActivity(productDetailsIntent);
    }

    @Override
    public void onReload() {
        fetchProducts();
    }

    /**
     * Load the loading indicator and server issue fragments
     */
    private void loadFragments() {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            LoadingIndicatorFragment loadingIndicatorFragment = new LoadingIndicatorFragment();
            ServerIssueFragment serverIssueFragment = ServerIssueFragment.newInstance(getString(R.string.problem_loading_section));
            serverIssueFragment.setOnReloadListener(this);

            fragmentTransaction.add(R.id.fl_loading, loadingIndicatorFragment);
            fragmentTransaction.add(R.id.fl_server_issue, serverIssueFragment);
            fragmentTransaction.commit();
        }
    }

    /**
     * Filter the product list based on a criteria
     * @param criteria - the search query of the user
     */
    public void filterProducts(String criteria) {
        List<Product> filteredProducts = products.stream()
                .filter(product -> product.getName().toLowerCase().contains(criteria) || product.getDescription().toLowerCase().contains(criteria))
                .collect(Collectors.toList());
        productsAdapter.updateProducts(filteredProducts);

        if (filteredProducts.size() == 0) {
            // No products were found matching the criteria -> show the 'no results' view
            tvNoResults.setText(String.format(Locale.ENGLISH, "%s '%s'.", getString(R.string.no_results), criteria));

            llNoResults.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
        } else {
            // Products that match the search criteria exist -> hide the 'no results' view
            llNoResults.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Shows the view used when an issue has occurred (no internet/failure to connect to the server)
     */
    private void showServerIssueView() {
        flLoading.setVisibility(View.GONE);
        rvProducts.setVisibility(View.INVISIBLE);
        rlServerIssue.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the loading indicator fragment
     */
    private void showLoadingIndicator() {
        flLoading.setVisibility(View.VISIBLE);
        rvProducts.setVisibility(View.INVISIBLE);
        rlServerIssue.setVisibility(View.GONE);
    }

    /**
     * Hides the loading indicator fragment
     */
    private void hideLoadingIndicator() {
        flLoading.setVisibility(View.GONE);
        rvProducts.setVisibility(View.VISIBLE);
        rlServerIssue.setVisibility(View.GONE);
    }

    /**
     * Fetches the product list from the server
     */
    private void fetchProducts() {
        showLoadingIndicator();
        // Check if internet is available
        if (Utils.internetIsUnavailable(context)) {
            Utils.showNoInternetToast(getActivity());
            showServerIssueView();
            return;
        }
        Api.getProducts(context, new Callbacks.GetProductsComplete() {
            @Override
            public void onSuccess(List<Product> products) {
                ProductsFragment.this.products = products;

                // Show the product list
                loadProducts(products);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d(TAG, "fetchProducts: onFailure - " + errorMessage);
                showServerIssueView();
            }
        });
    }

    /**
     * Displays a list of products in the recycler view
     * @param products - the list of products to display
     */
    private void loadProducts(List<Product> products) {
        productsAdapter = new ProductsAdapter(requireContext(), products);
        productsAdapter.setOnProductClickListener(this);

        rvProducts.setAdapter(productsAdapter);

        // Hide the loading indicator once the products have been loaded
        hideLoadingIndicator();
    }
}