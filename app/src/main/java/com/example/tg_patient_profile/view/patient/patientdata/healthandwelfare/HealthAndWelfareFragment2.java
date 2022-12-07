package com.example.tg_patient_profile.view.patient.patientdata.healthandwelfare;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.tg_patient_profile.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthAndWelfareFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthAndWelfareFragment2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Declare variables for Views
    Spinner indigenousStatusSpinner;
    Spinner mmmCodeSpinner;
    Spinner operationalTypeSpinner;
    Spinner preferredLanguageSpinner;
    Spinner programTypeSpinner;
    Spinner remotenessSpinner;
    Spinner sexSpinner;
    Spinner serviceSizeSpinner;
    Spinner stateSpinner;
    Spinner yearSpinner;


    //Declare variable for the selection of the Spinner
    String indigenousStatusSpinnerSelection;
    String mmmCodeSpinnerSelection;
    String operationalTypeSpinnerSelection;
    String preferredLanguageSpinnerSelection;
    String programTypeSpinnerSelection;
    String remotenessSpinnerSelection;
    String sexSpinnerSelection;
    String serviceSizeSpinnerSelection;
    String stateSpinnerSelection;
    String yearSpinnerSelection;

    public HealthAndWelfareFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileProfileUpdate2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HealthAndWelfareFragment2 newInstance(String param1, String param2) {
        HealthAndWelfareFragment2 fragment = new HealthAndWelfareFragment2();
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

        View fragmentView = inflater.inflate(R.layout.fragment_patient_profile_update2, container, false);


        //Assign View variables to the associated View objects
        indigenousStatusSpinner = (Spinner) fragmentView.findViewById(R.id.indigenousStatusSpinner); //Spinner to select Indigenous Status

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> indigenousStatusAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.indigenous_status_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        indigenousStatusSpinner.setAdapter(indigenousStatusAdapter);

        //Set the Spinner to the list of choices (i.e. Non-Indigenous, Indigenous)
        indigenousStatusSpinner.setOnItemSelectedListener(new HealthAndWelfareFragment2.SpinnerActivity());


        //Assign View variables to the associated View objects
        mmmCodeSpinner = (Spinner) fragmentView.findViewById(R.id.mmmCodeSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> lengthOfStayAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.mmm_code_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        mmmCodeSpinner.setAdapter(lengthOfStayAdapter);

        //Set the Spinner to the list of choices (i.e. Metre, Temperature, Weight)
        mmmCodeSpinner.setOnItemSelectedListener(new HealthAndWelfareFragment2.SpinnerActivity());


        //Assign View variables to the associated View objects
        operationalTypeSpinner = (Spinner) fragmentView.findViewById(R.id.operationalTypeSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> operationalTypeAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.operational_type_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        operationalTypeSpinner.setAdapter(operationalTypeAdapter);

        //Set the Spinner to the list of choices (i.e. Metre, Temperature, Weight)
        operationalTypeSpinner.setOnItemSelectedListener(new HealthAndWelfareFragment2.SpinnerActivity());


        //Assign View variables to the associated View objects
        preferredLanguageSpinner = (Spinner) fragmentView.findViewById(R.id.preferredLanguageSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> preferredLanguageAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.preferred_language_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        preferredLanguageSpinner.setAdapter(preferredLanguageAdapter);

        //Set the Spinner to the list of choices (i.e. Metre, Temperature, Weight)
        preferredLanguageSpinner.setOnItemSelectedListener(new HealthAndWelfareFragment2.SpinnerActivity());



        //Assign View variables to the associated View objects
        programTypeSpinner = (Spinner) fragmentView.findViewById(R.id.programTypeSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> programTypeAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.program_type_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        programTypeSpinner.setAdapter(programTypeAdapter);

        //Set the Spinner to the list of choices (i.e. Metre, Temperature, Weight)
        programTypeSpinner.setOnItemSelectedListener(new HealthAndWelfareFragment2.SpinnerActivity());



        //Assign View variables to the associated View objects
        remotenessSpinner = (Spinner) fragmentView.findViewById(R.id.remotenessSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> remotenessTypeAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.remoteness_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        remotenessSpinner.setAdapter(remotenessTypeAdapter);

        //Set the Spinner to the list of choices (i.e. Metre, Temperature, Weight)
        remotenessSpinner.setOnItemSelectedListener(new HealthAndWelfareFragment2.SpinnerActivity());



        //Assign View variables to the associated View objects
        sexSpinner = (Spinner) fragmentView.findViewById(R.id.sexSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.sex_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        sexSpinner.setAdapter(sexAdapter);

        //Set the Spinner to the list of choices (i.e. Metre, Temperature, Weight)
        sexSpinner.setOnItemSelectedListener(new HealthAndWelfareFragment2.SpinnerActivity());




        //Assign View variables to the associated View objects
        serviceSizeSpinner = (Spinner) fragmentView.findViewById(R.id.serviceSizeSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> serviceStateAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.service_size_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        serviceSizeSpinner.setAdapter(serviceStateAdapter);

        //Set the Spinner to the list of choices (i.e. Metre, Temperature, Weight)
        serviceSizeSpinner.setOnItemSelectedListener(new HealthAndWelfareFragment2.SpinnerActivity());



        //Assign View variables to the associated View objects
        stateSpinner = (Spinner) fragmentView.findViewById(R.id.stateSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.state_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        stateSpinner.setAdapter(stateAdapter);

        //Set the Spinner to the list of choices (i.e. Metre, Temperature, Weight)
        stateSpinner.setOnItemSelectedListener(new HealthAndWelfareFragment2.SpinnerActivity());



        //Assign View variables to the associated View objects
        yearSpinner = (Spinner) fragmentView.findViewById(R.id.yearSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.year_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        yearSpinner.setAdapter(yearAdapter);

        //Set the Spinner to the list of choices (i.e. Metre, Temperature, Weight)
        yearSpinner.setOnItemSelectedListener(new HealthAndWelfareFragment2.SpinnerActivity());


        indigenousStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                indigenousStatusSpinnerSelection = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        mmmCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mmmCodeSpinnerSelection = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        operationalTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                operationalTypeSpinnerSelection = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        preferredLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                preferredLanguageSpinnerSelection = parentView.getItemAtPosition(position).toString();

                Log.i("Position", "Overview: " + indigenousStatusSpinnerSelection + " " + mmmCodeSpinnerSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


        programTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                programTypeSpinnerSelection = parentView.getItemAtPosition(position).toString();

                Log.i("Position", "Overview: " + indigenousStatusSpinnerSelection + " " + mmmCodeSpinnerSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });



        remotenessSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                remotenessSpinnerSelection = parentView.getItemAtPosition(position).toString();

                Log.i("Position", "Overview: " + indigenousStatusSpinnerSelection + " " + mmmCodeSpinnerSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });



        sexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sexSpinnerSelection = parentView.getItemAtPosition(position).toString();

                Log.i("Position", "Overview: " + indigenousStatusSpinnerSelection + " " + mmmCodeSpinnerSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });



        serviceSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                serviceSizeSpinnerSelection = parentView.getItemAtPosition(position).toString();

                Log.i("Position", "Overview: " + indigenousStatusSpinnerSelection + " " + mmmCodeSpinnerSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });



        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                stateSpinnerSelection = parentView.getItemAtPosition(position).toString();

                Log.i("Position", "Overview: " + indigenousStatusSpinnerSelection + " " + mmmCodeSpinnerSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                yearSpinnerSelection = parentView.getItemAtPosition(position).toString();

                Log.i("Position", "Overview: " + indigenousStatusSpinnerSelection + " " + mmmCodeSpinnerSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        // Inflate the layout for this fragment
        return fragmentView;
    }

    //Define the Spinner class
    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}