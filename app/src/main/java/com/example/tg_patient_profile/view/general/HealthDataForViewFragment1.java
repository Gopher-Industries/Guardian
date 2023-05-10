package com.example.tg_patient_profile.view.general;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tg_patient_profile.R;

public class HealthDataForViewFragment1 extends Fragment {

    public HealthDataForViewFragment1() {
    }

    NextButtonClickedListener mButtonClickedListener;

    public interface NextButtonClickedListener {
        void onNextButtonClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mButtonClickedListener = (NextButtonClickedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() + " must implement NextButtonClickedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_data_for_view1, container, false);
        Button nextButton = view.findViewById(R.id.nextButtonHealthData);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonClickedListener.onNextButtonClicked();
            }
        });
        return view;
    }

}