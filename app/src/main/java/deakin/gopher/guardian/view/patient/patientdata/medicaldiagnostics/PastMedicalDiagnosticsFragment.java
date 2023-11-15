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

public class PastMedicalDiagnosticsFragment extends Fragment {

  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private final int[] editTextIds = {
    R.id.pastMedicalDiagnosticsNameTextView,
    R.id.pastMedicalDiagnosticsBloodPressureTextView,
    R.id.pastMedicalDiagnosticsPatientTemperatureTextView,
    R.id.pastMedicalDiagnosticsGlucoseLevelTextView,
    R.id.pastMedicalDiagnosticsO2SaturationTextView,
    R.id.pastMedicalDiagnosticsPulseRateTextView,
    R.id.pastMedicalDiagnosticsRespirationRateTextView,
    R.id.pastMedicalDiagnosticsBloodfatLevelTextView
  };

  private final int[] editButtonIds = {
    R.id.past_name_pencil,
    R.id.past_blood_pressure_pencil,
    R.id.past_temperature_pencil,
    R.id.past_glucose_level_pencil,
    R.id.past_blood_o2_saturation_pencil,
    R.id.past_pulse_rate_pencil,
    R.id.past_respiration_rate_pencil,
    R.id.past_bloodfat_level_pencil
  };
  MedicalDiagnostic medical_diagnostic_past;
  private EditText[] editTextArray;
  private Button[] editButtonArray;
  private String patientId;

  public PastMedicalDiagnosticsFragment() {
    // Required empty public constructor
  }

  public PastMedicalDiagnosticsFragment(final String patientId) {
    this.patientId = patientId;
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
        inflater.inflate(R.layout.fragment_past_medical_diagnostics, container, false);
    editTextArray = new EditText[editTextIds.length];
    editButtonArray = new Button[editButtonIds.length];

    for (int i = 0; i < editTextIds.length; i++) {
      editTextArray[i] = rootView.findViewById(editTextIds[i]);
      editButtonArray[i] = rootView.findViewById(editButtonIds[i]);

      final EditText editText = editTextArray[i];
      final Button editButton = editButtonArray[i];

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
    final Query query = reference.orderByChild("patient_id").equalTo(patientId);
    if (dataChecker()) {
      query.addListenerForSingleValueEvent(new SaveInFirebaseListener());
    }
  }

  private Boolean dataChecker() {
    for (final EditText editText : editTextArray) {
      if (TextUtils.isEmpty(editText.getText())) {
        editText.setError("it shouldn't be empty!");
        return false;
      }
    }
    medical_diagnostic_past =
        new MedicalDiagnostic(
            patientId,
            editTextArray[0].getText().toString(),
            editTextArray[1].getText().toString(),
            editTextArray[2].getText().toString(),
            editTextArray[3].getText().toString(),
            editTextArray[4].getText().toString(),
            editTextArray[5].getText().toString(),
            editTextArray[6].getText().toString(),
            editTextArray[7].getText().toString(),
            false);
    return true;
  }

  private class SaveInFirebaseListener implements ValueEventListener {
    @Override
    public void onDataChange(@NonNull final DataSnapshot snapshot) {
      for (final DataSnapshot childSnapshot : snapshot.getChildren()) {
        final Boolean isCurrent = childSnapshot.child("current").getValue(Boolean.class);
        if (Boolean.FALSE.equals(isCurrent)) {
          childSnapshot.getRef().setValue(medical_diagnostic_past);
        }
      }
    }

    @Override
    public void onCancelled(@NonNull final DatabaseError error) {}
  }
}
