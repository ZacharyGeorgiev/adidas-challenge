package com.zacharyg.adidasproductreviews;

import android.content.Context;
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

import java.util.List;

public class ProductsFragment extends Fragment implements ProductsAdapter.OnProductClickListener {
    private static final String TAG = "ProductsFragment";

    private Context context;

    RecyclerView rvProducts;

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
    public void onProductClick(String id) {

    }

    private void fetchProducts() {
        ProductsRemoteRepo.getProducts(context, new Callbacks.GetProductsComplete() {
            @Override
            public void onSuccess(List<Product> products) {
                loadProducts(products);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d(TAG, "fetchProducts: onFailure - " + errorMessage);
            }
        });
    }

    private void loadProducts(List<Product> products) {
        final ProductsAdapter adapter = new ProductsAdapter(requireContext(), products);
        adapter.setOnProductClickListener(this);
        rvProducts.setAdapter(adapter);
    }
}