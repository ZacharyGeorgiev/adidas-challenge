package com.zacharyg.adidasproductreviews;

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

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProductsFragment extends Fragment implements ProductsAdapter.OnProductClickListener, ServerIssueFragment.OnReloadListener {
    private static final String TAG = "ProductsFragment";

    private Context context;

    private RecyclerView rvProducts;

    private RelativeLayout rlNoInternet;

    private LinearLayout llNoResults;

    private FrameLayout flLoading;

    private TextView tvNoResults;

    private ProductsAdapter productsAdapter;

    private List<Product> products;

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

        rlNoInternet = view.findViewById(R.id.rl_no_internet);
        llNoResults = view.findViewById(R.id.ll_no_results);

        flLoading = view.findViewById(R.id.fl_loading);

        tvNoResults = view.findViewById(R.id.tv_no_results);

        rvProducts = view.findViewById(R.id.rv_products);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());

        // Configure the recycler view
        rvProducts.setLayoutManager(layoutManager);

        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (getActivity() != null) {
                    try {
                        ProductsActivity productsActivity = (ProductsActivity) getActivity();

                        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                            productsActivity.moveProducts(true);
                            return;
                        }

                        if (dy > 0) {
                            productsActivity.animateTopBar(false);
                            productsActivity.moveProducts(false);
                        } else if (dy < 0) {
                            productsActivity.animateTopBar(true);
                        }
                    } catch (Exception ex) {
                        Log.d(TAG, "Exception: " + ex.getMessage());
                    }
                }

            }
        });

        loadFragments();
        fetchProducts();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onProductClick(Product product) {
        if (!Utils.deviceIsConnectedToInternet(context)) {
            Utils.showNoInternetToast(getActivity());
            return;
        }
        Intent productDetailsIntent = new Intent(context, ProductDetailsActivity.class);
        productDetailsIntent.putExtra("product", product);
        startActivity(productDetailsIntent);
    }

    @Override
    public void onReload() {
        fetchProducts();
    }

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

    public void filterProducts(String criteria) {
        List<Product> filteredProducts = products.stream()
                .filter(product -> product.getName().toLowerCase().contains(criteria) || product.getDescription().toLowerCase().contains(criteria))
                .collect(Collectors.toList());
        productsAdapter.updateProducts(filteredProducts);

        if (filteredProducts.size() == 0) {
            tvNoResults.setText(String.format(Locale.ENGLISH, "%s '%s'.", getString(R.string.no_results), criteria));

            llNoResults.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
        } else {
            llNoResults.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
        }
    }

    private void showNoInternetView() {
        flLoading.setVisibility(View.GONE);
        rvProducts.setVisibility(View.INVISIBLE);
        rlNoInternet.setVisibility(View.VISIBLE);
    }

    private void showLoadingIndicator() {
        flLoading.setVisibility(View.VISIBLE);
        rvProducts.setVisibility(View.INVISIBLE);
        rlNoInternet.setVisibility(View.GONE);
    }

    private void hideLoadingIndicator() {
        flLoading.setVisibility(View.GONE);
        rvProducts.setVisibility(View.VISIBLE);
        rlNoInternet.setVisibility(View.GONE);
    }

    private void fetchProducts() {
        showLoadingIndicator();
        if (!Utils.deviceIsConnectedToInternet(context)) {
            Utils.showNoInternetToast(getActivity());
            showNoInternetView();
            return;
        }
        Api.getProducts(context, new Callbacks.GetProductsComplete() {
            @Override
            public void onSuccess(List<Product> products) {
                ProductsFragment.this.products = products;

                loadProducts(products);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d(TAG, "fetchProducts: onFailure - " + errorMessage);
                showNoInternetView();
            }
        });
    }

    private void loadProducts(List<Product> products) {
        productsAdapter = new ProductsAdapter(requireContext(), products);
        productsAdapter.setOnProductClickListener(this);

        rvProducts.setAdapter(productsAdapter);

        hideLoadingIndicator();
    }
}