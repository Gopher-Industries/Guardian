package com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.tg_patient_profile.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PastMedicalDiagnosticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastMedicalDiagnosticsFragment extends Fragment {

    private EditText[] editTextArray;
    private Button[] editButtonArray;
    private int[] editTextIds = {
            R.id.pastMedicalDiagnosticsNameTextView,
            R.id.pastMedicalDiagnosticsBloodPressureTextView,
            R.id.pastMedicalDiagnosticsPatientTemperatureTextView,
            R.id.pastMedicalDiagnosticsGlucoseLevelTextView,
            R.id.pastMedicalDiagnosticsO2SaturationTextView,
            R.id.pastMedicalDiagnosticsPulseRateTextView,
            R.id.pastMedicalDiagnosticsRespirationRateTextView,
            R.id.pastMedicalDiagnosticsBloodfatLevelTextView
    };

    private int[] editButtonIds = {
            R.id.past_name_pencil,
            R.id.past_blood_pressure_pencil,
            R.id.past_temperature_pencil,
            R.id.past_glucose_level_pencil,
            R.id.past_blood_o2_saturation_pencil,
            R.id.past_pulse_rate_pencil,
            R.id.past_respiration_rate_pencil,
            R.id.past_bloodfat_level_pencil
    };
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PastMedicalDiagnosticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PastMedicalDiagnosticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PastMedicalDiagnosticsFragment newInstance(String param1, String param2) {
        PastMedicalDiagnosticsFragment fragment = new PastMedicalDiagnosticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_past_medical_diagnostics, container, false);
        editTextArray = new EditText[editTextIds.length];
        editButtonArray = new Button[editButtonIds.length];

        for (int i = 0; i < editTextIds.length; i++) {
            editTextArray[i] = rootView.findViewById(editTextIds[i]);
            editButtonArray[i] = rootView.findViewById(editButtonIds[i]);

            final EditText editText = editTextArray[i];
            final Button editButton = editButtonArray[i];

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editText.setEnabled(true);
                    editText.requestFocus();
                    editText.selectAll();
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);


                }
            });
        }

        return rootView;
    }

    public void setEditState(boolean isEditable){
        if(isEditable){
            for(Button editButton: editButtonArray){
                editButton.setVisibility(View.VISIBLE);
            }
        }else{
            for(Button editButton: editButtonArray){
                editButton.setVisibility(View.INVISIBLE);
            }
            for(EditText editText: editTextArray){
                editText.setFocusable(false);
                editText.setFocusableInTouchMode(false);
                editText.setEnabled(false);
            }
        }
    }


}