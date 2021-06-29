package com.zacharyg.adidasproductreviews;

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

import java.util.List;
import java.util.Locale;

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

        llReviews = view.findViewById(R.id.ll_reviews);

        flLoading = view.findViewById(R.id.fl_loading);
        flServerIssue = view.findViewById(R.id.fl_server_issue);

        // Configure the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvReviews.setLayoutManager(layoutManager);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvReviews);

        if (getArguments() != null) {
            productID = getArguments().getString(ARG_PRODUCT_ID);
        }

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

    private void showServerIssueView() {
        flServerIssue.setVisibility(View.VISIBLE);
        flLoading.setVisibility(View.GONE);
        llReviews.setVisibility(View.INVISIBLE);
    }

    private void showLoadingIndicator() {
        flServerIssue.setVisibility(View.GONE);
        flLoading.setVisibility(View.VISIBLE);
        llReviews.setVisibility(View.INVISIBLE);
    }

    private void hideLoadingIndicator() {
        flServerIssue.setVisibility(View.GONE);
        flLoading.setVisibility(View.GONE);
        llReviews.setVisibility(View.VISIBLE);
    }

    public void refreshReviews() {
        fetchReviews();
    }

    private void fetchReviews() {
        showLoadingIndicator();
        if (!Utils.deviceIsConnectedToInternet(context)) {
            Utils.showNoInternetToast(getActivity());
            showServerIssueView();
            return;
        }
        Api.getReviews(context, new Callbacks.GetReviewsComplete() {
            @Override
            public void onSuccess(List<Review> reviews) {
                Log.d(TAG, "fetchReviews onSuccess: " + reviews);

                loadReviews(reviews);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d(TAG, "fetchReviews onFailure: " + errorMessage);
            }
        }, productID);
    }

    private void loadReviews(List<Review> reviews) {
        int reviewCount = reviews.size();
        if (reviewCount == 0) {
            tvCount.setText(R.string.no_reviews_yet);
        } else if (reviewCount == 1) {
            tvCount.setText(R.string.one_review);
        } else {
            tvCount.setText(String.format(Locale.ENGLISH, "%d reviews", reviews.size()));
        }

        tvNoReviews.setVisibility(reviewCount == 0 ? View.VISIBLE : View.GONE);
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