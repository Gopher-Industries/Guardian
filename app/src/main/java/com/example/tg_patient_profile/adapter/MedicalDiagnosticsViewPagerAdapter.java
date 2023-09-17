package com.example.tg_patient_profile.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics.CurrentMedicalDiagnosticsFragment;
import com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics.MedicalDiagnosticsFragment;
import com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics.PastMedicalDiagnosticsFragment;

public class MedicalDiagnosticsViewPagerAdapter extends FragmentStateAdapter {

    private MedicalDiagnosticsFragment parentFragment;
    private String patient_id;
    public MedicalDiagnosticsViewPagerAdapter(String patient_id, @NonNull MedicalDiagnosticsFragment fragmentActivity) {
        super(fragmentActivity);
        parentFragment = fragmentActivity;
        this.patient_id = patient_id;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                parentFragment.currentFragment = new CurrentMedicalDiagnosticsFragment(patient_id);
                return parentFragment.currentFragment;
            default:
                parentFragment.pastFragment = new PastMedicalDiagnosticsFragment(patient_id);
                return parentFragment.pastFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
