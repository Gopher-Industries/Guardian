package com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.adapter.MedicalDiagnosticsViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MedicalDiagnosticsFragment extends Fragment {

    public MedicalDiagnosticsFragment() {
        // Required empty public constructor
    }

    public static MedicalDiagnosticsFragment newInstance() {
        MedicalDiagnosticsFragment fragment = new MedicalDiagnosticsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_diagnostics, container, false);

        TabLayout tabLayout = view.findViewById(R.id.medicalDiagnosticsTabLayout);
        ViewPager2 viewPager2 = view.findViewById(R.id.medicalDiagnosticsViewPager);

        MedicalDiagnosticsViewPagerAdapter viewPagerAdapter = new MedicalDiagnosticsViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 0) {
                            tab.setText("Current");
                        } else {
                            tab.setText("Past");
                        }
                    }
                }).attach();

        return view;
    }
}