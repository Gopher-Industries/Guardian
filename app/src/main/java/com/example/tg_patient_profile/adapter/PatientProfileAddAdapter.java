package com.example.tg_patient_profile.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.tg_patient_profile.view.GP.GPAddFragment;
import com.example.tg_patient_profile.view.nextofkin.NoKAddFragment;
import com.example.tg_patient_profile.view.patient.PatientAddFragment;

public class PatientProfileAddAdapter extends FragmentStateAdapter {
  public PatientProfileAddAdapter(
      @NonNull final FragmentManager fragmentManager, @NonNull final Lifecycle lifecycle) {
    super(fragmentManager, lifecycle);
  }

  @NonNull
  @Override
  public Fragment createFragment(final int position) {
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
