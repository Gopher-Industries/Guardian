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
 * Use the {@link CurrentMedicalDiagnosticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentMedicalDiagnosticsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private EditText[] editTextArray;
    private Button[] editButtonArray;
    private int[] editTextIds = {
            R.id.currentMedicalDiagnosticsNameTextView,
            R.id.currentMedicalDiagnosticsBloodPressureTextView,
            R.id.currentMedicalDiagnosticsPatientTemperatureTextView,
            R.id.currentMedicalDiagnosticsGlucoseLevelTextView,
            R.id.currentMedicalDiagnosticsO2SaturationTextView,
            R.id.currentMedicalDiagnosticsPulseRateTextView,
            R.id.currentMedicalDiagnosticsRespirationRateTextView,
            R.id.currentMedicalDiagnosticsBloodfatLevelTextView
    };

    private int[] editButtonIds = {
            R.id.current_name_pencil,
            R.id.current_blood_pressure_pencil,
            R.id.current_temperature_pencil,
            R.id.current_glucose_level_pencil,
            R.id.current_blood_o2_saturation_pencil,
            R.id.current_pulse_rate_pencil,
            R.id.current_respiration_rate_pencil,
            R.id.current_bloodfat_level_pencil
    };
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public CurrentMedicalDiagnosticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentMedicalDiagnosticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentMedicalDiagnosticsFragment newInstance(String param1, String param2) {
        CurrentMedicalDiagnosticsFragment fragment = new CurrentMedicalDiagnosticsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_current_medical_diagnostics, container, false);

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