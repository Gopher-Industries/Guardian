package com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.adapter.MedicalDiagnosticsViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MedicalDiagnosticsFragment extends Fragment {

    public CurrentMedicalDiagnosticsFragment currentFragment;
    public PastMedicalDiagnosticsFragment pastFragment;
    private Button edit_button;
    private Boolean isEditable = false;
    private String patient_id;

    public MedicalDiagnosticsFragment() {
        // Required empty public constructor
    }

    public MedicalDiagnosticsFragment(final String patient_id) {
        this.patient_id = patient_id;
    }

    public static MedicalDiagnosticsFragment newInstance() {
        final MedicalDiagnosticsFragment fragment = new MedicalDiagnosticsFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_medical_diagnostics, container, false);
        edit_button = view.findViewById(R.id.header_edit_button);

        final TabLayout tabLayout = view.findViewById(R.id.medicalDiagnosticsTabLayout);
        final ViewPager2 viewPager2 = view.findViewById(R.id.medicalDiagnosticsViewPager);

        final MedicalDiagnosticsViewPagerAdapter viewPagerAdapter = new MedicalDiagnosticsViewPagerAdapter(patient_id, this);
        viewPager2.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull final TabLayout.Tab tab, final int position) {
                        if (0 == position) {
                            tab.setText("Current");
                        } else {
                            tab.setText("Past");
                        }
                    }
                }).attach();


        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (isEditable) {
                    edit_button.setBackgroundResource(R.drawable.medical_diagnostics_edit);
                } else {
                    edit_button.setBackgroundResource(R.drawable.medical_diagnostics_stop);
                }
                handleEditButtonClick();
            }
        });
        return view;
    }

    private void handleEditButtonClick() {
        isEditable = !isEditable;

        if (null != currentFragment) {
            currentFragment.setEditState(isEditable);
        }

        if (null != pastFragment) {
            pastFragment.setEditState(isEditable);
        }
    }

}