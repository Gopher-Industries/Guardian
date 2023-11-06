package com.example.tg_patient_profile.view.general;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.tg_patient_profile.R;

public class HealthDataForViewFragment2 extends Fragment {

    PrevButtonClickedListener mButtonClickedListener;

    public HealthDataForViewFragment2() {
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        try {
            mButtonClickedListener = (PrevButtonClickedListener) getParentFragment();
        } catch (final ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() + " must implement PrevButtonClickedListener");
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_health_data_for_view2, container, false);
        final Button prevButton = view.findViewById(R.id.prevButtonHealthData);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mButtonClickedListener.onPrevButtonClicked();
            }
        });
        return view;
    }

    public interface PrevButtonClickedListener {
        void onPrevButtonClicked();
    }
}