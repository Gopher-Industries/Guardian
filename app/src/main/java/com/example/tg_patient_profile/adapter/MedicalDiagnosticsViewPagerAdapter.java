package com.example.tg_patient_profile.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics.CurrentMedicalDiagnosticsFragment;
import com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics.MedicalDiagnosticsFragment;
import com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics.PastMedicalDiagnosticsFragment;

public class MedicalDiagnosticsViewPagerAdapter extends FragmentStateAdapter {

    private MedicalDiagnosticsFragment parentFragment;
    public MedicalDiagnosticsViewPagerAdapter(@NonNull MedicalDiagnosticsFragment fragmentActivity) {
        super(fragmentActivity);
        parentFragment = fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                parentFragment.currentFragment = new CurrentMedicalDiagnosticsFragment();
                return parentFragment.currentFragment;
            default:
                parentFragment.pastFragment = new PastMedicalDiagnosticsFragment();
                return parentFragment.pastFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
