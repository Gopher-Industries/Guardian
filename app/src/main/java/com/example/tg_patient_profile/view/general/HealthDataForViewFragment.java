package com.example.tg_patient_profile.view.general;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tg_patient_profile.R;

public class HealthDataForViewFragment extends Fragment implements HealthDataForViewFragment1.NextButtonClickedListener, HealthDataForViewFragment2.PrevButtonClickedListener {

    public HealthDataForViewFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_health_data_for_view, container, false);
        final FragmentManager childFragmentManager = getChildFragmentManager();
        final FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
        final HealthDataForViewFragment1 fragment1 = new HealthDataForViewFragment1();
        fragmentTransaction.add(R.id.child_fragment_container, fragment1);
        fragmentTransaction.addToBackStack("A");
        fragmentTransaction.commit();

        return view;
    }

    @Override
    public void onNextButtonClicked() {
        final FragmentManager childFragmentManager = getChildFragmentManager();
        final FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
        final HealthDataForViewFragment2 fragment2 = new HealthDataForViewFragment2();
        fragmentTransaction.replace(R.id.child_fragment_container, fragment2);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onPrevButtonClicked() {
        final FragmentManager childFragmentManager = getChildFragmentManager();
        final FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
        final HealthDataForViewFragment1 fragment1 = new HealthDataForViewFragment1();
        fragmentTransaction.replace(R.id.child_fragment_container, fragment1);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}