package com.zacharyg.adidasproductreviews.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zacharyg.adidasproductreviews.R;
import com.zacharyg.adidasproductreviews.models.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter used to manage the data of the reviews recycler view in {@link com.zacharyg.adidasproductreviews.fragments.ReviewsFragment}
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private final Context context;

    private final List<Review> reviews;

    public ReviewsAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = new ArrayList<>();
        this.reviews.addAll(reviews);
    }

    /**
     * Updates the list of reviews
     * @param reviews - the new list that the adapter should use
     */
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

        // Show the score
        holder.rbScore.setRating(review.getRating());

        // We consider a product is recommended only if a user has rated it with 4 or 5 stars
        holder.llRecommended.setVisibility(review.getRating() < 4 ? View.GONE : View.VISIBLE);

        // Show the review information
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
