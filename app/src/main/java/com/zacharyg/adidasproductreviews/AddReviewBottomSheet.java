package com.zacharyg.adidasproductreviews;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddReviewBottomSheet extends BottomSheetDialogFragment {

    private EditText etText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_review, container, false);

        etText = view.findViewById(R.id.et_text);

        etText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        return view;
    }
}
