package com.zacharyg.adidasproductreviews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProductsFragment extends Fragment implements ProductsAdapter.OnProductClickListener {
    private static final String TAG = "ProductsFragment";

    private Context context;

    private RecyclerView rvProducts;

    private LinearLayout llNoResults;

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

        llNoResults = view.findViewById(R.id.ll_no_results);

        tvNoResults = view.findViewById(R.id.tv_no_results);

        rvProducts = view.findViewById(R.id.rv_products);

        // Configure the recycler view
        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvProducts.getContext(), DividerItemDecoration.VERTICAL);
        rvProducts.addItemDecoration(dividerItemDecoration);

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
        Intent productDetailsIntent = new Intent(context, ProductDetailsActivity.class);
        productDetailsIntent.putExtra("product", product);
        startActivity(productDetailsIntent);
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

    private void fetchProducts() {
        ProductsRemoteRepo.getProducts(context, new Callbacks.GetProductsComplete() {
            @Override
            public void onSuccess(List<Product> products) {
                ProductsFragment.this.products = products;

                loadProducts(products);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d(TAG, "fetchProducts: onFailure - " + errorMessage);
            }
        });
    }

    private void loadProducts(List<Product> products) {
        productsAdapter = new ProductsAdapter(requireContext(), products);
        productsAdapter.setOnProductClickListener(this);
        rvProducts.setAdapter(productsAdapter);
    }
}