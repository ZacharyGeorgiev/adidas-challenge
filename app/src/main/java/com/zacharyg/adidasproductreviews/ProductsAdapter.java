package com.zacharyg.adidasproductreviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Locale;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private final Context context;

    private OnProductClickListener onProductClickListener;

    private List<Product> products;

    public ProductsAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void setOnProductClickListener(@NonNull final OnProductClickListener listener) {
        this.onProductClickListener = listener;
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

        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.ivProduct);

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
        void onProductClick(String id);
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
                    onProductClickListener.onProductClick(products.get(getAdapterPosition()).getId());
                }
            });
        }
    }
}
