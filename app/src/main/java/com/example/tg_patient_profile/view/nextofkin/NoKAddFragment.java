package com.example.tg_patient_profile.view.nextofkin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.model.NextofKin;
import com.example.tg_patient_profile.util.DataListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoKAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoKAddFragment extends Fragment {

    private Button left_button, right_button;
    private Button next_button, prev_button;
    private int status;
    private NextofKin nextofKin;
    ViewPager2 viewPager2;
    private DataListener dataListener;
    private EditText first_name_input, middle_name_input, last_name_input, address_input, phone_input, email_input;
    private String firstName,middleName, lastName,address, phoneNumber,email;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NoKAddFragment() {
        // Required empty public constructor
    }
    public NoKAddFragment(int status){
        this.status = status;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoKAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoKAddFragment newInstance(String param1, String param2) {
        NoKAddFragment fragment = new NoKAddFragment();
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
        View rootView =inflater.inflate(R.layout.fragment_nok_add, container, false);
        left_button = rootView.findViewById(R.id.nok_add_polygon_left);
        right_button = rootView.findViewById(R.id.nok_add_polygon_right);
        next_button = rootView.findViewById(R.id.nok_add_NextButton);
        prev_button = rootView.findViewById(R.id.nok_add_PrevButton);
        first_name_input = rootView.findViewById(R.id.input_nok_FirstName);
        middle_name_input = rootView.findViewById(R.id.input_nok_MiddleName);
        last_name_input= rootView.findViewById(R.id.input_nok_FirstName);
        address_input = rootView.findViewById(R.id.input_nok_adress);
        phone_input = rootView.findViewById(R.id.input_nok_PhoneNumber);
        email_input = rootView.findViewById(R.id.input_nok_EmailAdress);


        if(status==2){
            left_button.setBackgroundResource(R.drawable.polygon_3);
            right_button.setBackgroundResource(R.drawable.polygon_4);
        }
        right_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status==1){
                    //for now I keep 2 next of kins and 2 gps waiting to be add
                    //but defaultly adding 1 nok and 1gp, after alick right arrow the second one shows up is better
                    if(dataChecker()){
                        saveNextofKin();
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
                        saveNextofKin();
                        scrollPage(false);
                    }
                }
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataChecker()){
                    saveNextofKin();
                    scrollPage(true);
                }
            }
        });

        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataChecker()){
                    saveNextofKin();
                    scrollPage(false);
                }
            }
        });

        return rootView;
    }

    private void scrollPage(boolean isNextPage) {
        if(isNextPage){
            int nextPage = viewPager2.getCurrentItem()+1;
            viewPager2.setCurrentItem(nextPage,true);
        }else{
            int nextPage = viewPager2.getCurrentItem()-1;
            viewPager2.setCurrentItem(nextPage,true);
        }


    }

    private void saveNextofKin() {
            if(nextofKin == null){
                nextofKin = new NextofKin(firstName, middleName, lastName, address,phoneNumber,email, null);
            }else{
                nextofKin.setFirst_name(firstName);
                nextofKin.setMiddle_name(middleName);
                nextofKin.setLast_name(lastName);
                nextofKin.setHome_address(address);
                nextofKin.setMobile_phone(phoneNumber);
                nextofKin.setEmail_address(email);
            }
            if(status==1){
                dataListener.onDataFilled(null,nextofKin,null,null,null);
            }else{
                dataListener.onDataFilled(null,null,nextofKin,null,null);

            }

    }

    private boolean dataChecker() {
        firstName = first_name_input.getText().toString();
        middleName = middle_name_input.getText().toString();
        lastName = last_name_input.getText().toString();
        address = address_input.getText().toString();
        phoneNumber = phone_input.getText().toString();
        email = email_input.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            setErrorAndReturn(first_name_input, "First name is Required.");
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            setErrorAndReturn(last_name_input, "Last name is Required.");
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            setErrorAndReturn(address_input, "Address is Required.");
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            setErrorAndReturn(phone_input, "Phone number is Required.");
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