package com.zacharyg.adidasproductreviews.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zacharyg.adidasproductreviews.R;

public class ServerIssueFragment extends Fragment {
    private static final String ARG_MESSAGE = "message";

    private Button btnTryAgain;

    private String message;

    private OnReloadListener listener;

    public ServerIssueFragment() {
        // Required empty public constructor
    }

    public static ServerIssueFragment newInstance(String message) {
        ServerIssueFragment fragment = new ServerIssueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnReloadListener(OnReloadListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(ARG_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_server_issue, container, false);

        setupViews(view);
        setupListeners();

        return view;
    }

    private void setupViews(View view) {
        btnTryAgain        = view.findViewById(R.id.btn_try_again);

        TextView tvMessage = view.findViewById(R.id.tv_message);

        tvMessage.setText(message);
    }

    private void setupListeners() {
        btnTryAgain.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReload();
            }
        });
    }

    @FunctionalInterface
    public interface OnReloadListener {
        void onReload();
    }
}