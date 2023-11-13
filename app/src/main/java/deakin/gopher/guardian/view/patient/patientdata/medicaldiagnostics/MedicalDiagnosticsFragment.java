package deakin.gopher.guardian.view.patient.patientdata.medicaldiagnostics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.MedicalDiagnosticsViewPagerAdapter;

public class MedicalDiagnosticsFragment extends Fragment {
  public CurrentMedicalDiagnosticsFragment currentFragment;
  public PastMedicalDiagnosticsFragment pastFragment;
  private Button editButton;
  private Boolean isEditable = false;
  private String patientId;

  public MedicalDiagnosticsFragment() {
    // Required empty public constructor
  }

  public MedicalDiagnosticsFragment(final String patientId) {
    this.patientId = patientId;
  }

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull final LayoutInflater inflater,
      @Nullable final ViewGroup container,
      @Nullable final Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_medical_diagnostics, container, false);
    editButton = view.findViewById(R.id.header_edit_button);

    final TabLayout tabLayout = view.findViewById(R.id.medicalDiagnosticsTabLayout);
    final ViewPager2 viewPager2 = view.findViewById(R.id.medicalDiagnosticsViewPager);

    final MedicalDiagnosticsViewPagerAdapter viewPagerAdapter =
        new MedicalDiagnosticsViewPagerAdapter(patientId, this);
    viewPager2.setAdapter(viewPagerAdapter);

    new TabLayoutMediator(
            tabLayout,
            viewPager2,
            (tab, position) -> {
              if (0 == position) {
                tab.setText("Current");
              } else {
                tab.setText("Past");
              }
            })
        .attach();

    editButton.setOnClickListener(
        view1 -> {
          if (isEditable) {
            editButton.setBackgroundResource(R.drawable.medical_diagnostics_edit);
          } else {
            editButton.setBackgroundResource(R.drawable.medical_diagnostics_stop);
          }
          handleEditButtonClick();
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
