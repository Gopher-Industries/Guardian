package com.example.tg_patient_profile.view.general;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.tg_patient_profile.R;

public class HealthDataForViewFragment1 extends Fragment {

    NextButtonClickedListener mButtonClickedListener;

    public HealthDataForViewFragment1() {
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        try {
            mButtonClickedListener = (NextButtonClickedListener) getParentFragment();
        } catch (final ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() + " must implement NextButtonClickedListener");
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_health_data_for_view1, container, false);
        final Button nextButton = view.findViewById(R.id.nextButtonHealthData);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mButtonClickedListener.onNextButtonClicked();
            }
        });
        return view;
    }

    public interface NextButtonClickedListener {
        void onNextButtonClicked();
    }

}