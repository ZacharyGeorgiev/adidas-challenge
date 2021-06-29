package com.zacharyg.adidasproductreviews.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zacharyg.adidasproductreviews.R;
import com.zacharyg.adidasproductreviews.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter used to manage the data of the products recycler view in {@link com.zacharyg.adidasproductreviews.fragments.ProductsFragment}
 */
public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private final Context context;

    private OnProductClickListener onProductClickListener;

    private final List<Product> products;

    public ProductsAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = new ArrayList<>();
        this.products.addAll(products);
    }

    /**
     * Sets the listener which will be called when a product is selected
     * @param listener - a class implementing the OnProductClickListener that will respond to product clicks
     */
    public void setOnProductClickListener(@NonNull final OnProductClickListener listener) {
        this.onProductClickListener = listener;
    }

    /**
     * Updates the list of products
     * @param products - the new list that the adapter should use
     */
    public void updateProducts(List<Product> products) {
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layoutView = LayoutInflater.from(context).inflate(R.layout.row_product, parent, false);
        return new ProductsAdapter.ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);

        // Load the product image into the image view
        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.ivProduct);

        // Show the product information
        holder.tvPrice.setText(String.format(Locale.ENGLISH,"%s%d.00", product.getCurrency(), product.getPrice()));
        holder.tvName.setText(product.getName());
        holder.tvDescription.setText(product.getDescription());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @FunctionalInterface
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public final class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;

        TextView tvPrice;
        TextView tvName;
        TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProduct           = itemView.findViewById(R.id.iv_product);

            tvPrice             = itemView.findViewById(R.id.tv_price);
            tvName              = itemView.findViewById(R.id.tv_name);
            tvDescription       = itemView.findViewById(R.id.tv_description);

            LinearLayout llRoot = itemView.findViewById(R.id.ll_root);
            llRoot.setOnClickListener(v -> {
                if (onProductClickListener != null) {
                    onProductClickListener.onProductClick(products.get(getAdapterPosition()));
                }
            });
        }
    }
}
