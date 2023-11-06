package com.example.tg_patient_profile.view.nextofkin;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.model.NextOfKin;
import com.example.tg_patient_profile.util.DataListener;

/**
 * A simple {@link Fragment} subclass. Use the {@link NoKAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoKAddFragment extends Fragment {

  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";
  ViewPager2 viewPager2;
  private Button left_button, right_button;
  private Button next_button, prev_button;
  private int status;
  private NextOfKin nextofKin;
  private DataListener dataListener;
  private EditText first_name_input,
      middle_name_input,
      last_name_input,
      address_input,
      phone_input,
      email_input;
  private String firstName, middleName, lastName, address, phoneNumber, email;
  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  public NoKAddFragment() {
    // Required empty public constructor
  }

  public NoKAddFragment(final int status) {
    this.status = status;
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment NoKAddFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static NoKAddFragment newInstance(final String param1, final String param2) {
    final NoKAddFragment fragment = new NoKAddFragment();
    final Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    Log.i("masuk", "coiming 1");

    super.onCreate(savedInstanceState);
    if (null != getArguments()) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  public View onCreateView(
      final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    Log.i("masuk", "coiming 2");
    viewPager2 = getActivity().findViewById(R.id.dataForViewViewPager);
    final View rootView = inflater.inflate(R.layout.fragment_nok_add, container, false);
    left_button = rootView.findViewById(R.id.nok_add_polygon_left);
    right_button = rootView.findViewById(R.id.nok_add_polygon_right);
    next_button = rootView.findViewById(R.id.nok_add_NextButton);
    prev_button = rootView.findViewById(R.id.nok_add_PrevButton);
    first_name_input = rootView.findViewById(R.id.input_nok_FirstName);
    middle_name_input = rootView.findViewById(R.id.input_nok_MiddleName);
    last_name_input = rootView.findViewById(R.id.input_nok_FirstName);
    address_input = rootView.findViewById(R.id.input_nok_adress);
    phone_input = rootView.findViewById(R.id.input_nok_PhoneNumber);
    email_input = rootView.findViewById(R.id.input_nok_EmailAdress);

    final Button step1_button = rootView.findViewById(R.id.step1);
    final Button step2_button = rootView.findViewById(R.id.step2);
    final Button step3_button = rootView.findViewById(R.id.step3);
    final Button step4_button = rootView.findViewById(R.id.step4);
    final Button step5_button = rootView.findViewById(R.id.step5);

    if (1 == status) {
      step2_button.setBackgroundResource(R.drawable.roundshapeseletebtn);
      step3_button.setBackgroundResource(R.drawable.roundshapebtn);
    }
    if (2 == status) {
      Log.i("masuk2", "Page 3");
      step3_button.setBackgroundResource(R.drawable.roundshapeseletebtn);
      step2_button.setBackgroundResource(R.drawable.roundshapebtn);
    }

    step1_button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            if (dataChecker()) {
              saveNextofKin();
              // int nextPage = viewPager2.getCurrentItem()-1;
              viewPager2.setCurrentItem(0, true);
            }
          }
        });

    step2_button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            Log.i("masuk1", "Pageno 2");
            if (dataChecker()) {
              saveNextofKin();
              // step3_button.setBackgroundResource(R.drawable.roundshapeseletebtn);

              // int nextPage = viewPager2.getCurrentItem()+1;
              viewPager2.setCurrentItem(1, true);
            }
          }
        });
    step3_button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            Log.i("masuk1", "Pageno 3");
            if (dataChecker()) {
              saveNextofKin();
              // int nextPage = viewPager2.getCurrentItem()+1;
              viewPager2.setCurrentItem(2, true);
            }
          }
        });
    step4_button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            if (dataChecker()) {
              saveNextofKin();
              // int nextPage = viewPager2.getCurrentItem()+2;
              viewPager2.setCurrentItem(3, true);
            }
          }
        });
    step5_button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            if (dataChecker()) {
              saveNextofKin();
              // int nextPage = viewPager2.getCurrentItem()+3;
              viewPager2.setCurrentItem(4, true);
            }
          }
        });

    if (2 == status) {
      left_button.setBackgroundResource(R.drawable.polygon_3);
      right_button.setBackgroundResource(R.drawable.polygon_4);
    }
    right_button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            if (1 == status) {
              // for now I keep 2 next of kins and 2 gps waiting to be add
              // but defaultly adding 1 nok and 1gp, after alick right arrow the second one shows up
              // is better
              if (dataChecker()) {
                saveNextofKin();
                scrollPage(true);
              }
            }
          }
        });

    left_button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            if (2 == status) {
              if (dataChecker()) {
                saveNextofKin();
                scrollPage(false);
              }
            }
          }
        });

    next_button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            if (dataChecker()) {
              saveNextofKin();
              scrollPage(true);
            }
          }
        });

    prev_button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            if (dataChecker()) {
              saveNextofKin();
              scrollPage(false);
            }
          }
        });

    return rootView;
  }

  private void scrollPage(final boolean isNextPage) {
    if (isNextPage) {
      final int nextPage = viewPager2.getCurrentItem() + 1;
      viewPager2.setCurrentItem(nextPage, true);
    } else {
      final int nextPage = viewPager2.getCurrentItem() - 1;
      viewPager2.setCurrentItem(nextPage, true);
    }
  }

  private void saveNextofKin() {
    if (null == nextofKin) {
      nextofKin = new NextOfKin(firstName, middleName, lastName, address, phoneNumber, email, null);
    } else {
      nextofKin.setFirstName(firstName);
      nextofKin.setMiddleName(middleName);
      nextofKin.setLastName(lastName);
      nextofKin.setHomeAddress(address);
      nextofKin.setMobilePhone(phoneNumber);
      nextofKin.setEmailAddress(email);
    }
    if (1 == status) {
      dataListener.onDataFilled(null, nextofKin, null, null, null);
    } else {
      dataListener.onDataFilled(null, null, nextofKin, null, null);
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

  private void setErrorAndReturn(final EditText editText, final String s) {
    editText.setError(s);
  }

  @Override
  public void onAttach(@NonNull final Context context) {
    super.onAttach(context);
    try {
      dataListener = (DataListener) context;
    } catch (final ClassCastException e) {
      throw new ClassCastException(context + " must implement DataListener");
    }
  }
}
