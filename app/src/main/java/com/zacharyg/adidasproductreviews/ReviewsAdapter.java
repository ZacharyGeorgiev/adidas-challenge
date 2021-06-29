package com.zacharyg.adidasproductreviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private final Context context;

    private final List<Review> reviews;

    public ReviewsAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = new ArrayList<>();
        this.reviews.addAll(reviews);
    }

    public void updateReviews(List<Review> reviews) {
        this.reviews.clear();
        this.reviews.addAll(reviews);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layoutView = LayoutInflater.from(context).inflate(R.layout.row_review, parent, false);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.rbScore.setRating(review.getRating());

        holder.llRecommended.setVisibility(review.getRating() < 4 ? View.GONE : View.VISIBLE);

        holder.tvText.setText(review.getText());
        holder.tvUsername.setText(review.getUsername());
        holder.tvCountry.setText(review.getCountry());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        RatingBar rbScore;

        LinearLayout llRecommended;

        TextView tvText;
        TextView tvUsername;
        TextView tvCountry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rbScore           = itemView.findViewById(R.id.rb_score);

            llRecommended     = itemView.findViewById(R.id.ll_recommended);

            tvText            = itemView.findViewById(R.id.tv_text);
            tvUsername        = itemView.findViewById(R.id.tv_username);
            tvCountry         = itemView.findViewById(R.id.tv_country);
        }
    }
}
