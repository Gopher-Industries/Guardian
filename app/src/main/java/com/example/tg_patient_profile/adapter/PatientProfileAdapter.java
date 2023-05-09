package com.example.tg_patient_profile.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tg_patient_profile.view.GP.GPProfileFragment;
import com.example.tg_patient_profile.view.general.HealthDataForViewFragment;
import com.example.tg_patient_profile.view.nextofkin.NextOfKinFragment;
import com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics.MedicalDiagnosticsFragment;
import com.example.tg_patient_profile.view.patient.patientdata.patient.PatientProfileFragment;

public class PatientProfileAdapter extends FragmentStateAdapter {

    public PatientProfileAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                return new PatientProfileFragment();
            case 1:
                return new NextOfKinFragment();
            case 2:
                return new GPProfileFragment();
            case 3:
                return  new MedicalDiagnosticsFragment();
            default:
                return new HealthDataForViewFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
