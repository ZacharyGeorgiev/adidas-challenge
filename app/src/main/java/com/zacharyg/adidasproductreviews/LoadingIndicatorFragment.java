package com.zacharyg.adidasproductreviews;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class LoadingIndicatorFragment extends Fragment {

    private Context context;

    public LoadingIndicatorFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_indicator, container, false);

        ImageView ivLoading = view.findViewById(R.id.iv_loading);

        Glide.with(context).load(getResources().getIdentifier("loading_indicator", "drawable", context.getPackageName())).into(ivLoading);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}