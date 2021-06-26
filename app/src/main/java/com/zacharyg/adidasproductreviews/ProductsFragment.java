package com.zacharyg.adidasproductreviews;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProductsFragment extends Fragment implements ProductsAdapter.OnProductClickListener {

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

        final RecyclerView rvProducts = view.findViewById(R.id.rv_products);

        // Configure the recycler view
        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvProducts.getContext(), DividerItemDecoration.VERTICAL);
        rvProducts.addItemDecoration(dividerItemDecoration);
        final ProductsAdapter adapter = new ProductsAdapter(requireContext());
        adapter.setOnProductClickListener(this);
        rvProducts.setAdapter(adapter);

        return view;
    }

    @Override
    public void onProductClick(String id) {

    }
}