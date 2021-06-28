package com.zacharyg.adidasproductreviews;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class ReviewsFragment extends Fragment {
    private static final String TAG = "ReviewsFragment";

    private TextView tvCount;
    private TextView tvNoReviews;

    private RecyclerView rvReviews;

    private ReviewsAdapter reviewsAdapter;

    private static final String ARG_PRODUCT_ID = "product_id";

    private Context context;

    private String productID;

    private List<Review> reviews;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    public static ReviewsFragment newInstance(String productID) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        tvCount = view.findViewById(R.id.tv_count);
        tvNoReviews = view.findViewById(R.id.tv_no_reviews);

        rvReviews = view.findViewById(R.id.rv_reviews);

        // Configure the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvReviews.setLayoutManager(layoutManager);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvReviews);

        if (getArguments() != null) {
            productID = getArguments().getString(ARG_PRODUCT_ID);
        }

//        postReview();
        fetchReviews();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void fetchReviews() {
        Api.getReviews(context, new Callbacks.GetReviewsComplete() {
            @Override
            public void onSuccess(List<Review> reviews) {
                ReviewsFragment.this.reviews = reviews;

                Log.d(TAG, "fetchReviews onSuccess: " + reviews);

                loadReviews(reviews);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d(TAG, "fetchReviews onFailure: " + errorMessage);
            }
        }, productID);
    }

    private void postReview() {
        Api.postReview(context, new Callbacks.PostReviewComplete() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String errorMessage) {

            }
        }, productID, 3, "The shoes look great but are a little too tight on my feet. Would recommend to size up.");
    }

    private void loadReviews(List<Review> reviews) {
        int reviewCount = reviews.size();
        if (reviewCount == 0) {
            tvCount.setText(R.string.no_reviews_yet);

            tvNoReviews.setVisibility(View.VISIBLE);
            rvReviews.setVisibility(View.GONE);
        } else if (reviewCount == 1) {
            tvCount.setText(R.string.one_review);
        } else {
            tvCount.setText(String.format(Locale.ENGLISH, "%d reviews", reviews.size()));
        }

        reviewsAdapter = new ReviewsAdapter(requireContext(), reviews);
        rvReviews.setAdapter(reviewsAdapter);
    }
}