package com.zacharyg.adidasproductreviews.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zacharyg.adidasproductreviews.Callbacks;
import com.zacharyg.adidasproductreviews.R;
import com.zacharyg.adidasproductreviews.adapters.ReviewsAdapter;
import com.zacharyg.adidasproductreviews.helpers.Api;
import com.zacharyg.adidasproductreviews.helpers.Utils;
import com.zacharyg.adidasproductreviews.models.Review;

import java.util.List;
import java.util.Locale;

/**
 * Fragment used to display all reviews for a product
 */
public class ReviewsFragment extends Fragment implements ServerIssueFragment.OnReloadListener {
    private static final String TAG = "ReviewsFragment";

    private TextView tvCount;
    private TextView tvNoReviews;

    private RecyclerView rvReviews;

    private LinearLayout llReviews;

    private FrameLayout flLoading;
    private FrameLayout flServerIssue;

    private ReviewsAdapter reviewsAdapter;

    private static final String ARG_PRODUCT_ID = "product_id";

    private Context context;

    private String productID;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of the fragment
     * @param productID - the ID of the product to display the reviews for
     */
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

        if (getArguments() != null) {
            productID = getArguments().getString(ARG_PRODUCT_ID);
        }

        setupViews(view);
        loadFragments();
        fetchReviews();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onReload() {
        fetchReviews();
    }

    private void setupViews(View view) {
        tvCount       = view.findViewById(R.id.tv_count);
        tvNoReviews   = view.findViewById(R.id.tv_no_reviews);

        rvReviews     = view.findViewById(R.id.rv_reviews);

        llReviews     = view.findViewById(R.id.ll_reviews);

        flLoading     = view.findViewById(R.id.fl_loading);
        flServerIssue = view.findViewById(R.id.fl_server_issue);

        // Configure the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvReviews.setLayoutManager(layoutManager);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvReviews);
    }

    /**
     * Load the loading indicator and server issue fragments
     */
    private void loadFragments() {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            LoadingIndicatorFragment loadingIndicatorFragment = new LoadingIndicatorFragment();
            ServerIssueFragment serverIssueFragment = ServerIssueFragment.newInstance(getString(R.string.problem_loading_reviews));
            serverIssueFragment.setOnReloadListener(this);

            fragmentTransaction.add(R.id.fl_loading, loadingIndicatorFragment);
            fragmentTransaction.add(R.id.fl_server_issue, serverIssueFragment);
            fragmentTransaction.commit();
        }
    }

    /**
     * Shows the view used when an issue has occurred (no internet/failure to connect to the server)
     */
    private void showServerIssueView() {
        flServerIssue.setVisibility(View.VISIBLE);
        flLoading.setVisibility(View.GONE);
        llReviews.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows the loading indicator fragment
     */
    private void showLoadingIndicator() {
        flServerIssue.setVisibility(View.GONE);
        flLoading.setVisibility(View.VISIBLE);
        llReviews.setVisibility(View.INVISIBLE);
    }

    /**
     * Hides the loading indicator fragment
     */
    private void hideLoadingIndicator() {
        flServerIssue.setVisibility(View.GONE);
        flLoading.setVisibility(View.GONE);
        llReviews.setVisibility(View.VISIBLE);
    }

    public void refreshReviews() {
        fetchReviews();
    }

    /**
     * Fetches the review list from the server
     */
    private void fetchReviews() {
        showLoadingIndicator();
        // Check if internet is available
        if (Utils.internetIsUnavailable(context)) {
            Utils.showNoInternetToast(getActivity());
            showServerIssueView();
            return;
        }
        Api.getReviews(context, new Callbacks.GetReviewsComplete() {
            @Override
            public void onSuccess(List<Review> reviews) {
                Log.d(TAG, "fetchReviews onSuccess: " + reviews);

                // Show the reviews
                loadReviews(reviews);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d(TAG, "fetchReviews onFailure: " + errorMessage);
                showServerIssueView();
            }
        }, productID);
    }

    /**
     * Displays a list of reviews in the recycler view
     * @param reviews - the list of reviews to display
     */
    private void loadReviews(List<Review> reviews) {
        int reviewCount = reviews.size();
        // Set the appropriate text for the count text view
        if (reviewCount == 0) {
            tvCount.setText(R.string.no_reviews_yet);
        } else if (reviewCount == 1) {
            tvCount.setText(R.string.one_review);
        } else {
            tvCount.setText(String.format(Locale.ENGLISH, "%d reviews", reviews.size()));
        }

        // Show/hide the 'No reviews' view based on the number of reviews
        tvNoReviews.setVisibility(reviewCount == 0 ? View.VISIBLE : View.GONE);
        // Show/hide the reviews recycler view based on the number of reviews
        rvReviews.setVisibility(reviewCount >= 1 ? View.VISIBLE : View.GONE);

        if (reviewsAdapter == null) {
            reviewsAdapter = new ReviewsAdapter(requireContext(), reviews);
            rvReviews.setAdapter(reviewsAdapter);
        } else {
            reviewsAdapter.updateReviews(reviews);
        }

        hideLoadingIndicator();
    }
}