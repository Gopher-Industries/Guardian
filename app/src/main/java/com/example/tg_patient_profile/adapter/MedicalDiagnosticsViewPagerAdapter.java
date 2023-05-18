package com.example.tg_patient_profile.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics.CurrentMedicalDiagnosticsFragment;
import com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics.MedicalDiagnosticsFragment;
import com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics.PastMedicalDiagnosticsFragment;

public class MedicalDiagnosticsViewPagerAdapter extends FragmentStateAdapter {

    public MedicalDiagnosticsViewPagerAdapter(@NonNull MedicalDiagnosticsFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new CurrentMedicalDiagnosticsFragment();
            default:
                return new PastMedicalDiagnosticsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
