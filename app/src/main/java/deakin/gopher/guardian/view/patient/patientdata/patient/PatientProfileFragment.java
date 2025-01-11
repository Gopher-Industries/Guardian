package deakin.gopher.guardian.view.patient.patientdata.patient;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.Patient;
import deakin.gopher.guardian.services.api.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientProfileFragment extends Fragment {

    public PatientProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_profile, container, false);

        // Set up click listeners for the image views
        setupImageClickListeners(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String patientId = bundle.getString("patientId");
            if (patientId != null) {
                loadPatientDataFromApi(patientId, view);
            }
        }
        return view;
    }

    private void loadPatientDataFromApi(final String patientId, final View view) {
        Call<Patient> call = ApiClient.INSTANCE.getApiService().getPatient(patientId);

        call.enqueue(new Callback<Patient>() {
            @Override
            public void onResponse(@NonNull Call<Patient> call, @NonNull Response<Patient> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Patient patient = response.body();
                    updateUI(view, patient);
                } else {
                    Log.e("PatientProfileFragment", "API response error: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Patient> call, @NonNull Throwable t) {
                Log.e("PatientProfileFragment", "API error: " + t.getMessage());
            }
        });
    }

    private void updateUI(View view, Patient patient) {
        ((TextView) view.findViewById(R.id.txtFirstName)).setText(
                patient.getFirstName() != null ? patient.getFirstName() : "N/A"
        );
        ((TextView) view.findViewById(R.id.txtMiddleName)).setText(
                patient.getMiddleName() != null ? patient.getMiddleName() : ""
        );
        ((TextView) view.findViewById(R.id.txtLastName)).setText(
                patient.getLastName() != null ? patient.getLastName() : "N/A"
        );
        ((TextView) view.findViewById(R.id.txtDateOfBirth)).setText(
                patient.getDob() != null ? patient.getDob() : "N/A"
        );
    }

    private void setupImageClickListeners(View view) {
        ImageView bloodPressureImage = view.findViewById(R.id.imageBloodPressure);
        ImageView temperatureImage = view.findViewById(R.id.imagePatientTemperature);
        ImageView glucoseImage = view.findViewById(R.id.imageGlucoseLevel);
        ImageView oxygenImage = view.findViewById(R.id.imageOxygenSaturation);
        ImageView pulseImage = view.findViewById(R.id.imagePulseRate);
        ImageView respirationImage = view.findViewById(R.id.imageRespirationRate);

        bloodPressureImage.setOnClickListener(v -> showInfoDialog(
                getString(R.string.blood_pressure_title),
                getString(R.string.blood_pressure_explanation)
        ));

        temperatureImage.setOnClickListener(v -> showInfoDialog(
                getString(R.string.temperature_title),
                getString(R.string.temperature_explanation)
        ));

        glucoseImage.setOnClickListener(v -> showInfoDialog(
                getString(R.string.glucose_level_title),
                getString(R.string.glucose_level_explanation)
        ));

        oxygenImage.setOnClickListener(v -> showInfoDialog(
                getString(R.string.oxygen_saturation_title),
                getString(R.string.oxygen_saturation_explanation)
        ));

        pulseImage.setOnClickListener(v -> showInfoDialog(
                getString(R.string.pulse_rate_title),
                getString(R.string.pulse_rate_explanation)
        ));

        respirationImage.setOnClickListener(v -> showInfoDialog(
                getString(R.string.respiration_rate_title),
                getString(R.string.respiration_rate_explanation)
        ));
    }

    private void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}



