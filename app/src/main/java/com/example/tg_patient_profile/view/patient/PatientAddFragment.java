package com.example.tg_patient_profile.view.patient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.model.Patient;
import com.example.tg_patient_profile.util.DataListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientAddFragment extends Fragment {
    private Button step1_button;
    private Button step2_button;
    private Button step3_button;

    private Button next_button;
    private EditText first_name, middle_name, last_name, date_of_birth, medicare_no, western_affairs_no;
    private Patient patient;
    private DataListener dataListener;
    private String dateOfBirth;
    private String firstName;
    private String middleName;
    private String lastName;
    private String medicareNo;
    private String westernAffairsNo;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PatientAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PatientAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientAddFragment newInstance(String param1, String param2) {
        PatientAddFragment fragment = new PatientAddFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"MissingInflatedId", "NewApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_patient_add, container, false);
        next_button = rootView.findViewById(R.id.patient_add_NextButton);
        ViewPager2 viewPager2 = getActivity().findViewById(R.id.dataForViewViewPager);
        first_name = rootView.findViewById(R.id.input_patient_FirstName);
        middle_name = rootView.findViewById(R.id.input_patient_MiddleName);
       last_name = rootView.findViewById(R.id.input_patient_LastName);
       date_of_birth = rootView.findViewById(R.id.input_patient_DateOfBirth);
       medicare_no = rootView.findViewById(R.id.input_patient_MedicareNumber);
       western_affairs_no = rootView.findViewById(R.id.input_patient_WesternAffairsNumber);
        step1_button = rootView.findViewById(R.id.step1);
        Button step2_button = rootView.findViewById(R.id.step2);
        Button step3_button = rootView.findViewById(R.id.step3);
        Button step4_button = rootView.findViewById(R.id.step4);
        Button step5_button = rootView.findViewById(R.id.step5);

        step2_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataChecker()){
                    savePatient();
                    int nextPage = viewPager2.getCurrentItem()+1;
                    viewPager2.setCurrentItem(nextPage,true);
                }

            }
        });
        step3_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataChecker()){
                    savePatient();
                    int nextPage = viewPager2.getCurrentItem()+2;
                    viewPager2.setCurrentItem(nextPage,true);
                }

            }
        });
        step4_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataChecker()){
                    savePatient();
                    int nextPage = viewPager2.getCurrentItem()+3;
                    viewPager2.setCurrentItem(nextPage,true);
                }

            }
        });
        step5_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataChecker()){
                    savePatient();
                    int nextPage = viewPager2.getCurrentItem()+4;
                    viewPager2.setCurrentItem(nextPage,true);
                }

            }
        });

        //step1

        /*step1_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // step1_button.setBackgroundResource(R.drawable.roundshapeseletebtn);

            }
        });*/

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataChecker()){
                    savePatient();
                    int nextPage = viewPager2.getCurrentItem()+1;
                    viewPager2.setCurrentItem(nextPage,true);
                }

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //once scroll, save data
                savePatient();

            }
        });

        return rootView;
    }

    private void savePatient() {
            if(patient == null){
                patient = new Patient(dateOfBirth, firstName, middleName, lastName, medicareNo, westernAffairsNo,null,null,null,null);

            }else{
                patient.setFirst_name(first_name.getText().toString());
                patient.setMiddle_name(middleName);
                patient.setLast_name(lastName);
                patient.setMedicareNo(medicareNo);
                patient.setDob(dateOfBirth);
                patient.setWestwenAffairesNo(westernAffairsNo);
            }
            dataListener.onDataFilled(patient,null,null,null,null);


        }

    private boolean dataChecker() {
        dateOfBirth = date_of_birth.getText().toString();
        firstName = first_name.getText().toString();
        middleName = middle_name.getText().toString();
        lastName = last_name.getText().toString();
        medicareNo = medicare_no.getText().toString();
        westernAffairsNo = western_affairs_no.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            setErrorAndReturn(first_name, "First name is Required.");
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            setErrorAndReturn(last_name, "Last name is Required.");
            return false;
        }
        if (TextUtils.isEmpty(dateOfBirth)) {
            setErrorAndReturn(date_of_birth, "Date of Birth is Required.");
            return false;
        }
        if (TextUtils.isEmpty(medicareNo)) {
            setErrorAndReturn(medicare_no, "Medicare number is Required.");
            return false;
        }
        return true;
    }

    private void setErrorAndReturn(EditText editText, String s) {
        editText.setError(s);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dataListener = (DataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DataListener");
        }
    }
}