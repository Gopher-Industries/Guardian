package com.example.tg_patient_profile.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tg_patient_profile.view.GP.GPAddFragment;
import com.example.tg_patient_profile.view.GP.GPProfileFragment;
import com.example.tg_patient_profile.view.general.HealthDataForViewFragment;
import com.example.tg_patient_profile.view.nextofkin.NextOfKinFragment;
import com.example.tg_patient_profile.view.nextofkin.NoKAddFragment;
import com.example.tg_patient_profile.view.patient.PatientAddFragment;
import com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics.MedicalDiagnosticsFragment;
import com.example.tg_patient_profile.view.patient.patientdata.patient.PatientProfileFragment;

public class PatientProfileAddAdapter extends FragmentStateAdapter {

    public PatientProfileAddAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                return new PatientAddFragment();
            case 1:
                return new NoKAddFragment(1);
            case 2:
                return new NoKAddFragment(2);
            case 3:
                return new GPAddFragment(1);
            default:
                return new GPAddFragment(2);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
