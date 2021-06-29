package com.zacharyg.adidasproductreviews.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zacharyg.adidasproductreviews.Callbacks;
import com.zacharyg.adidasproductreviews.R;
import com.zacharyg.adidasproductreviews.activities.ProductDetailsActivity;
import com.zacharyg.adidasproductreviews.helpers.Api;
import com.zacharyg.adidasproductreviews.helpers.Utils;

/**
 * Bottom sheet dialog used to create product reviews
 */
public class AddReviewBottomSheet extends BottomSheetDialogFragment {
    private RatingBar rbScore;

    private EditText etText;

    private Button btnSubmit;

    private Context context;

    private static final String TAG = "AddReviewBottomSheet";
    private static final String ARG_PRODUCT_ID = "product_id";

    private String productID;

    /**
     * Creates a new instance of the dialog
     * @param productID - the product for which the review will be created
     */
    public static AddReviewBottomSheet newInstance(String productID) {
        AddReviewBottomSheet addReviewBottomSheet = new AddReviewBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productID);
        addReviewBottomSheet.setArguments(args);
        return addReviewBottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_review, container, false);

        if (getArguments() != null) {
            productID = getArguments().getString(ARG_PRODUCT_ID);
        }

        setupViews(view);
        setupListeners();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void setupViews(View view) {
        rbScore   = view.findViewById(R.id.rb_score);

        etText    = view.findViewById(R.id.et_text);

        btnSubmit = view.findViewById(R.id.btn_submit);

        etText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        btnSubmit.setEnabled(false);
    }

    private void setupListeners() {
        rbScore.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> btnSubmit.setEnabled(etText.getText().length() > 4 && rbScore.getRating() >= 1));

        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Only allow submitting of reviews when the user has written something and selected a review score
                btnSubmit.setEnabled(s.length() > 4 && rbScore.getRating() >= 1);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnSubmit.setOnClickListener(v -> {
            // Check if internet is available before attempting to post the review
            if (Utils.internetIsUnavailable(context)) {
                Utils.showNoInternetToast(getActivity());
                return;
            }
            postReview();
        });
    }

    private void postReview() {
        Api.postReview(context, new Callbacks.PostReviewComplete() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    // Refresh the reviews after the new review has been posted successfully
                    ((ProductDetailsActivity) getActivity()).refreshReviews();
                }

                // Dismiss the bottom sheet
                AddReviewBottomSheet.this.dismiss();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d(TAG, "postReview onFailure: " + errorMessage);
                Utils.showToast(getActivity(), getString(R.string.failed_to_post_review), Toast.LENGTH_LONG);
            }
        }, productID, (int) rbScore.getRating(), etText.getText().toString());
    }
}
