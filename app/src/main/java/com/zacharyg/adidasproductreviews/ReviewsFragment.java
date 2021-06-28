package com.zacharyg.adidasproductreviews;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

    private RecyclerView rvReviews;

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

        rvReviews = view.findViewById(R.id.rv_reviews);

        if (getArguments() != null) {
            productID = getArguments().getString(ARG_PRODUCT_ID);
        }

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

                loadReviews();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d(TAG, "fetchReviews onFailure: " + errorMessage);
            }
        }, productID);
    }

    private void loadReviews() {
        tvCount.setText(String.format(Locale.ENGLISH, "%d reviews", reviews.size()));
    }
}