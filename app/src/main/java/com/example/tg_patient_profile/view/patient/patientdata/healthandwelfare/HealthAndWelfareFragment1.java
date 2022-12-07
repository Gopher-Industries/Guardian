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
 * Use the {@link HealthAndWelfareFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthAndWelfareFragment1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //Declare variables for Views
    Spinner agedCarePlanningRegionalCodeSpinner;
    Spinner agedCarePlanningRegionNameSpinner;
    Spinner activitiesOfDailyLivingSpinner;
    Spinner admissionTypeSpinner;
    Spinner ageGroupSpinner;
    Spinner careTypeSpinner;
    Spinner complexHealthcareScoreSpinner;
    Spinner countryOfBirthSpinner;
    Spinner reasonForDepartureSpinner;
    Spinner reasonForDischargeSpinner;
    Spinner firstAdmissionSpinner;
    Spinner homecareLevelSpinner;

    //Declare variable for the selection of the Spinner
    String agedCarePlanningRegionalCodeSpinnerSelection;
    String agedCarePlanningRegionalNameSpinnerSelection;
    String activitiesOfDailyLivingSpinnerSelection;
    String admissionTypeSpinnerSelection;
    String ageGroupSpinnerSelection;
    String careTypeSpinnerSelection;
    String complexHealthcareScoreSpinnerSelection;
    String countryOfBirthSpinnerSelection;
    String reasonForDepartureSpinnerSelection;
    String reasonForDischargeSpinnerSelection;
    String firstAdmissionSpinnerSelection;
    String homecareLevelSpinnerSelection;


    public HealthAndWelfareFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PatientProfileUpdate1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HealthAndWelfareFragment1 newInstance(String param1, String param2) {
        HealthAndWelfareFragment1 fragment = new HealthAndWelfareFragment1();
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
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_patient_profile_update1, container, false);
        View fragmentView = inflater.inflate(R.layout.fragment_patient_profile_update1, container, false);



        //Assign View variables to the associated View objects
        agedCarePlanningRegionalCodeSpinner = (Spinner) fragmentView.findViewById(R.id.agedCarePlanningRegionalCodeSpinner); //Spinner to select Indigenous Status

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> agedCarPlanningRegionalCodeAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.aged_care_planning_regional_code_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        agedCarePlanningRegionalCodeSpinner.setAdapter(agedCarPlanningRegionalCodeAdapter);

        //Set the Spinner to the list of choices (i.e. Non-Indigenous, Indigenous)
        agedCarePlanningRegionalCodeSpinner.setOnItemSelectedListener(new SpinnerActivity());



        //Assign View variables to the associated View objects
        agedCarePlanningRegionNameSpinner = (Spinner) fragmentView.findViewById(R.id.agedCarePlanningRegionNameSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> agedCarPlanningRegionNameAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.aged_care_planning_region_name_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        agedCarePlanningRegionNameSpinner.setAdapter(agedCarPlanningRegionNameAdapter);

        //Set the Spinner to the list of choices
        agedCarePlanningRegionNameSpinner.setOnItemSelectedListener(new SpinnerActivity());



        //Assign View variables to the associated View objects
        activitiesOfDailyLivingSpinner = (Spinner) fragmentView.findViewById(R.id.activitiesOfDailyLivingStatusSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> activitiesOfDailyLivingAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.activities_of_daily_living_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        activitiesOfDailyLivingSpinner.setAdapter(activitiesOfDailyLivingAdapter);

        //Set the Spinner to the list of choices
        activitiesOfDailyLivingSpinner.setOnItemSelectedListener(new SpinnerActivity());



        //Assign View variables to the associated View objects
        admissionTypeSpinner = (Spinner) fragmentView.findViewById(R.id.admissionTypeStatusSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> admissionTypeAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.admission_type_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        admissionTypeSpinner.setAdapter(admissionTypeAdapter);

        //Set the Spinner to the list of choices
        admissionTypeSpinner.setOnItemSelectedListener(new SpinnerActivity());




        //Assign View variables to the associated View objects
        ageGroupSpinner = (Spinner) fragmentView.findViewById(R.id.ageGroupSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> ageGroupAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.age_group_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        ageGroupSpinner.setAdapter(ageGroupAdapter);

        //Set the Spinner to the list of choices
        ageGroupSpinner.setOnItemSelectedListener(new SpinnerActivity());

















        //Assign View variables to the associated View objects
        careTypeSpinner = (Spinner) fragmentView.findViewById(R.id.careTypeSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> careTypeAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.care_type_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        careTypeSpinner.setAdapter(careTypeAdapter);

        //Set the Spinner to the list of choices
        careTypeSpinner.setOnItemSelectedListener(new SpinnerActivity());


        //Assign View variables to the associated View objects
        complexHealthcareScoreSpinner = (Spinner) fragmentView.findViewById(R.id.complexHealthCareScoreSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> complexHealthcareScoreAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.complex_health_care_score_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        complexHealthcareScoreSpinner.setAdapter(complexHealthcareScoreAdapter);

        //Set the Spinner to the list of choices
        complexHealthcareScoreSpinner.setOnItemSelectedListener(new SpinnerActivity());


        //Assign View variables to the associated View objects
        countryOfBirthSpinner = (Spinner) fragmentView.findViewById(R.id.countryOfBirthSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> countryOfBirthAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.country_of_birth_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        countryOfBirthSpinner.setAdapter(countryOfBirthAdapter);

        //Set the Spinner to the list of choices
        countryOfBirthSpinner.setOnItemSelectedListener(new SpinnerActivity());



        //Assign View variables to the associated View objects
        reasonForDepartureSpinner = (Spinner) fragmentView.findViewById(R.id.reasonForDepartureSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> reasonForDepartureAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.reason_for_departure_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        reasonForDepartureSpinner.setAdapter(reasonForDepartureAdapter);

        //Set the Spinner to the list of choices
        reasonForDepartureSpinner.setOnItemSelectedListener(new SpinnerActivity());



        //Assign View variables to the associated View objects
        reasonForDischargeSpinner = (Spinner) fragmentView.findViewById(R.id.reasonForDischargeSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> reasonForDischargeAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.reason_for_discharge_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        reasonForDischargeSpinner.setAdapter(reasonForDischargeAdapter);

        //Set the Spinner to the list of choices
        reasonForDischargeSpinner.setOnItemSelectedListener(new SpinnerActivity());



        //Assign View variables to the associated View objects
        firstAdmissionSpinner = (Spinner) fragmentView.findViewById(R.id.firstAdmissionSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> firstAdmissionAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.first_admission_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        firstAdmissionSpinner.setAdapter(firstAdmissionAdapter);

        //Set the Spinner to the list of choices
        firstAdmissionSpinner.setOnItemSelectedListener(new SpinnerActivity());



        //Assign View variables to the associated View objects
        homecareLevelSpinner = (Spinner) fragmentView.findViewById(R.id.homecareLevelSpinner); //Spinner to select Length Of Stay

        // Create an ArrayAdapter using the string array and a default Spinner layout
        ArrayAdapter<CharSequence> homecareLevelAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.homecare_level_array, android.R.layout.simple_spinner_item);

        // Apply the adapter to the Spinner
        homecareLevelSpinner.setAdapter(homecareLevelAdapter);

        //Set the Spinner to the list of choices
        homecareLevelSpinner.setOnItemSelectedListener(new SpinnerActivity());





        agedCarePlanningRegionalCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                agedCarePlanningRegionalCodeSpinnerSelection = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        agedCarePlanningRegionNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                agedCarePlanningRegionalNameSpinnerSelection = parentView.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        activitiesOfDailyLivingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                activitiesOfDailyLivingSpinnerSelection = parentView.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        admissionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                admissionTypeSpinnerSelection = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ageGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ageGroupSpinnerSelection = parentView.getItemAtPosition(position).toString();

                Log.i("Overview: ", agedCarePlanningRegionalCodeSpinnerSelection + " " + agedCarePlanningRegionalNameSpinnerSelection + " " + activitiesOfDailyLivingSpinnerSelection + admissionTypeSpinnerSelection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });









        careTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                careTypeSpinnerSelection = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        complexHealthcareScoreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                complexHealthcareScoreSpinnerSelection = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        countryOfBirthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                countryOfBirthSpinnerSelection = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        reasonForDepartureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                reasonForDepartureSpinnerSelection = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        reasonForDischargeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                reasonForDischargeSpinnerSelection = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        firstAdmissionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                firstAdmissionSpinnerSelection = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        homecareLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                homecareLevelSpinnerSelection = parentView.getItemAtPosition(position).toString();}

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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