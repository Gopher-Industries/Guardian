package com.example.tg_patient_profile.view.GP;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.tg_patient_profile.model.GP;
import com.example.tg_patient_profile.util.DataListener;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GPAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GPAddFragment extends Fragment {
    private int status;
    private Button left_button, right_button;
    private Button next_button,prev_button;
    private GP gp;
    ViewPager2 viewPager2;
    private DataListener dataListener;
    private EditText first_name_input, middle_name_input, last_name_input,clinic_address_input, phone_number_input,email_input,fax_input;
    private String firstName,middleName, lastName,clinicAddress, phoneNumber,email,fax;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GPAddFragment() {
        // Required empty public constructor
    }
    public GPAddFragment(int status){
        this.status = status;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GPAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GPAddFragment newInstance(String param1, String param2) {
        GPAddFragment fragment = new GPAddFragment();
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
        viewPager2 = getActivity().findViewById(R.id.dataForViewViewPager);
        View rootView = inflater.inflate(R.layout.fragment_gp_add, container, false);
        left_button = rootView.findViewById(R.id.gp_add_polygon_left);
        right_button = rootView.findViewById(R.id.gp_add_polygon_right);
        next_button = rootView.findViewById(R.id.gp_add_NextButton);
        prev_button = rootView.findViewById(R.id.gp_add_PrevButton);
        first_name_input = rootView.findViewById(R.id.input_gp_FirstName);
        middle_name_input = rootView.findViewById(R.id.input_gp_MiddleName);
        last_name_input = rootView.findViewById(R.id.input_gp_LastName);
        clinic_address_input = rootView.findViewById(R.id.input_gp_ClinicAddress);
        phone_number_input = rootView.findViewById(R.id.input_gp_Phone);
        email_input = rootView.findViewById(R.id.input_gp_Email);
        fax_input = rootView.findViewById(R.id.input_gp_Fax);

        Button step1_button = rootView.findViewById(R.id.step1);
        Button step2_button = rootView.findViewById(R.id.step2);
        Button step3_button = rootView.findViewById(R.id.step3);
        Button step4_button = rootView.findViewById(R.id.step4);
        Button step5_button = rootView.findViewById(R.id.step5);

        if (status == 1) {
            step4_button.setBackgroundResource(R.drawable.roundshapeseletebtn);
            step5_button.setBackgroundResource(R.drawable.roundshapebtn);

        }
        if (status == 2) {
            //Log.i("masuk2", "Page 3");
            step5_button.setBackgroundResource(R.drawable.roundshapeseletebtn);
            step4_button.setBackgroundResource(R.drawable.roundshapebtn);
        }

        step1_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // if(dataChecker()){
                    saveGp();

                    //int nextPage = viewPager2.getCurrentItem()-1;
                    viewPager2.setCurrentItem(0,true);
               // }

            }
        });

        step2_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(dataChecker()){
                    saveGp();

                    //step3_button.setBackgroundResource(R.drawable.roundshapeseletebtn);

                    // int nextPage = viewPager2.getCurrentItem()+1;
                    viewPager2.setCurrentItem(1,true);
               // }

            }
        });
        step3_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // if(dataChecker()){
                    saveGp();

                    //int nextPage = viewPager2.getCurrentItem()+1;
                    viewPager2.setCurrentItem(2,true);
               // }

            }
        });
        step4_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(dataChecker()){
                    saveGp();

                    //int nextPage = viewPager2.getCurrentItem()+2;
                    viewPager2.setCurrentItem(3,true);
               // }

            }
        });
        step5_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(dataChecker()){
                    saveGp();

                    // int nextPage = viewPager2.getCurrentItem()+3;
                    viewPager2.setCurrentItem(4,true);
               // }

            }
        });


        if(status==2){
            left_button.setBackgroundResource(R.drawable.polygon_3);
            right_button.setBackgroundResource(R.drawable.polygon_4);
        }else{
            next_button.setText("Next");
        }



        right_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status==1){
                    //for now I keep 2 next of kins and 2 gps waiting to be add
                    //but defaultly adding 1 nok and 1gp, after alick right arrow the second one shows up is better
                    if(dataChecker()){
                        saveGp();
                        scrollPage(true);
                    }

                }

            }
        });

        left_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status ==2){
                    if(dataChecker()){
                        saveGp();
                        scrollPage(false);
                    }
                }
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataChecker()){
                    saveGp();
                    if(status==1){
                        scrollPage(true);
                    }else{
                        dataListener.onDataFinihsed(true);
                    }

                }
            }
        });

        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataChecker()){
                    saveGp();
                    scrollPage(false);
                }
            }
        });
        return rootView;

    }

    private void scrollPage(boolean isNextPage){
        if(isNextPage){
            int nextPage = viewPager2.getCurrentItem()+1;
            viewPager2.setCurrentItem(nextPage,true);
        }else{
            int nextPage = viewPager2.getCurrentItem()-1;
            viewPager2.setCurrentItem(nextPage,true);
        }
    }

    private void saveGp(){
        if (gp == null) {
            gp = new GP(firstName,middleName,lastName,clinicAddress,phoneNumber,email,fax,null);
        }else{
            gp.setFirst_name(firstName);
            gp.setMiddle_name(middleName);
            gp.setLast_name(lastName);
            gp.setClinic_address(clinicAddress);
            gp.setPhone(phoneNumber);
            gp.setEmail(email);
            gp.setFax(fax);
        }
        if(status==1){
            dataListener.onDataFilled(null,null,null,gp,null);
        }else{
            dataListener.onDataFilled(null,null,null,null,gp);

        }
    }

    private boolean dataChecker(){
        firstName = first_name_input.getText().toString();
        middleName = middle_name_input.getText().toString();
        lastName = last_name_input.getText().toString();
        clinicAddress = clinic_address_input.getText().toString();
        phoneNumber = phone_number_input.getText().toString();
        email = email_input.getText().toString();
        fax = fax_input.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            setErrorAndReturn(first_name_input, "First name is Required.");
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            setErrorAndReturn(last_name_input, "Last name is Required.");
            return false;
        }
        if(TextUtils.isEmpty(clinicAddress)){
            setErrorAndReturn(clinic_address_input,"Clinic address is Required.");
        }
        if(TextUtils.isEmpty(phoneNumber)){
            setErrorAndReturn(phone_number_input,"Phone number is Required.");
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

