package com.example.tg_patient_profile.view.general;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tg_patient_profile.R;

public class HealthDataForViewFragment2 extends Fragment {

    PrevButtonClickedListener mButtonClickedListener;

    public interface PrevButtonClickedListener {
        void onPrevButtonClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mButtonClickedListener = (PrevButtonClickedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() + " must implement PrevButtonClickedListener");
        }
    }

    public HealthDataForViewFragment2() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_data_for_view2, container, false);
        Button prevButton = view.findViewById(R.id.prevButtonHealthData);
       prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonClickedListener.onPrevButtonClicked();
            }
        });
        return view;
    }
}