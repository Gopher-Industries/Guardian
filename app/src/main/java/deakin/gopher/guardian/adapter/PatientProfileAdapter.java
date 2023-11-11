package deakin.gopher.guardian.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import deakin.gopher.guardian.view.GP.GPProfileFragment;
import deakin.gopher.guardian.view.general.HealthDataForViewFragment;
import deakin.gopher.guardian.view.nextofkin.NextOfKinFragment;
import deakin.gopher.guardian.view.patient.patientdata.medicaldiagnostics.MedicalDiagnosticsFragment;
import deakin.gopher.guardian.view.patient.patientdata.patient.PatientProfileFragment;

public class PatientProfileAdapter extends FragmentStateAdapter {
  final String patient_id;

  public PatientProfileAdapter(
      @NonNull final String patient_id,
      @NonNull final FragmentManager fragmentManager,
      @NonNull final Lifecycle lifecycle) {
    super(fragmentManager, lifecycle);
    this.patient_id = patient_id;
  }

  @NonNull
  public Fragment createFragment(final int position) {
    switch (position) {
      case 0:
        return new PatientProfileFragment();
      case 1:
        return new NextOfKinFragment();
      case 2:
        return new GPProfileFragment();
      case 3:
        return new MedicalDiagnosticsFragment(patient_id);
      default:
        return new HealthDataForViewFragment();
    }
  }

  @Override
  public int getItemCount() {
    return 6;
  }
}
