package deakin.gopher.guardian.view.patient.patientdata.medicaldiagnostics;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.MedicalDiagnostic;

public class CurrentMedicalDiagnosticsFragment extends Fragment {
  private final int[] editTextIds = {
    R.id.currentMedicalDiagnosticsNameTextView,
    R.id.currentMedicalDiagnosticsBloodPressureTextView,
    R.id.currentMedicalDiagnosticsPatientTemperatureTextView,
    R.id.currentMedicalDiagnosticsGlucoseLevelTextView,
    R.id.currentMedicalDiagnosticsO2SaturationTextView,
    R.id.currentMedicalDiagnosticsPulseRateTextView,
    R.id.currentMedicalDiagnosticsRespirationRateTextView,
    R.id.currentMedicalDiagnosticsBloodfatLevelTextView
  };

  private final int[] editButtonIds = {
    R.id.current_name_pencil,
    R.id.current_blood_pressure_pencil,
    R.id.current_temperature_pencil,
    R.id.current_glucose_level_pencil,
    R.id.current_blood_o2_saturation_pencil,
    R.id.current_pulse_rate_pencil,
    R.id.current_respiration_rate_pencil,
    R.id.current_bloodfat_level_pencil
  };
  MedicalDiagnostic medical_diagnostic_current;
  private EditText[] editTextArray;
  private Button[] editButtonArray;
  private String patient_id;

  public CurrentMedicalDiagnosticsFragment() {
    // Required empty public constructor
  }

  public CurrentMedicalDiagnosticsFragment(final String patient_id) {
    this.patient_id = patient_id;
  }

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    final View rootView =
        inflater.inflate(R.layout.fragment_current_medical_diagnostics, container, false);

    editTextArray = new EditText[editTextIds.length];
    editButtonArray = new Button[editButtonIds.length];
    for (int i = 0; i < editTextIds.length; i++) {
      editTextArray[i] = rootView.findViewById(editTextIds[i]);
      editButtonArray[i] = rootView.findViewById(editButtonIds[i]);

      final EditText editText = editTextArray[i];
      final Button editButton = editButtonArray[i];
      editText.setFocusable(false);
      editText.setFocusableInTouchMode(false);
      editText.setEnabled(false);
      editButton.setVisibility(View.INVISIBLE);

      editButton.setOnClickListener(
          v -> {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setEnabled(true);
            editText.requestFocus();
            editText.selectAll();
            final InputMethodManager imm =
                (InputMethodManager)
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
          });
    }

    return rootView;
  }

  public void setEditState(final boolean isEditable) {
    if (isEditable) {
      for (final Button editButton : editButtonArray) {
        editButton.setVisibility(View.VISIBLE);
      }
    } else {
      for (final Button editButton : editButtonArray) {
        editButton.setVisibility(View.INVISIBLE);
      }
      for (final EditText editText : editTextArray) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setEnabled(false);
      }
      saveInFirebase();
    }
  }

  private void saveInFirebase() {
    final DatabaseReference reference =
        FirebaseDatabase.getInstance().getReference("health_details");
    final Query query = reference.orderByChild("patient_id").equalTo(patient_id);
    if (dataChecker()) {
      query.addListenerForSingleValueEvent(new MyValueEventListener());
    }
  }

  private Boolean dataChecker() {
    for (final EditText editText : editTextArray) {
      if (TextUtils.isEmpty(editText.getText())) {
        editText.setError("it shouldn't be empty!");
        return false;
      }
    }
    medical_diagnostic_current =
        new MedicalDiagnostic(
            patient_id,
            editTextArray[0].getText().toString(),
            editTextArray[1].getText().toString(),
            editTextArray[2].getText().toString(),
            editTextArray[3].getText().toString(),
            editTextArray[4].getText().toString(),
            editTextArray[5].getText().toString(),
            editTextArray[6].getText().toString(),
            editTextArray[7].getText().toString(),
            true);
    return true;
  }

  private class MyValueEventListener implements ValueEventListener {
    @Override
    public void onDataChange(@NonNull final DataSnapshot snapshot) {
      for (final DataSnapshot childSnapshot : snapshot.getChildren()) {
        final Boolean isCurrent = childSnapshot.child("current").getValue(Boolean.class);
        if (Boolean.TRUE.equals(isCurrent)) {
          childSnapshot.getRef().setValue(medical_diagnostic_current);
        }
      }
    }

    @Override
    public void onCancelled(@NonNull final DatabaseError error) {}
  }
}
