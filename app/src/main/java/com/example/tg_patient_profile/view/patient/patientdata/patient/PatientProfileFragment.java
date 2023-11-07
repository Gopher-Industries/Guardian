package com.example.tg_patient_profile.view.patient.patientdata.patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.tg_patient_profile.R;

public class PatientProfileFragment extends Fragment {
  public PatientProfileFragment() {}

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_patient_profile, container, false);
  }
}
