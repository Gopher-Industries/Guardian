package deakin.gopher.guardian.view.patient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.Patient;
import deakin.gopher.guardian.util.DataListener;

public class PatientAddFragment extends Fragment {
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";
  private EditText first_name,
      middle_name,
      last_name,
      date_of_birth,
      medicare_no,
      western_affairs_no;
  private Patient patient;
  private DataListener dataListener;
  private String dateOfBirth;
  private String firstName;
  private String middleName;
  private String lastName;
  private String medicareNo;
  private String westernAffairsNo;

  public PatientAddFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (null != getArguments()) {
      // TODO: Rename and change types of parameters
      final String mParam1 = getArguments().getString(ARG_PARAM1);
      final String mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  @SuppressLint({"MissingInflatedId", "NewApi"})
  @Override
  public View onCreateView(
      final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    final View rootView = inflater.inflate(R.layout.fragment_patient_add, container, false);
    final Button next_button = rootView.findViewById(R.id.patient_add_NextButton);
    final ViewPager2 viewPager2 = getActivity().findViewById(R.id.dataForViewViewPager);
    first_name = rootView.findViewById(R.id.input_patient_FirstName);
    middle_name = rootView.findViewById(R.id.input_patient_MiddleName);
    last_name = rootView.findViewById(R.id.input_patient_LastName);
    date_of_birth = rootView.findViewById(R.id.input_patient_DateOfBirth);
    medicare_no = rootView.findViewById(R.id.input_patient_MedicareNumber);
    western_affairs_no = rootView.findViewById(R.id.input_patient_WesternAffairsNumber);
    final Button step1_button = rootView.findViewById(R.id.step1);
    final Button step2_button = rootView.findViewById(R.id.step2);
    final Button step3_button = rootView.findViewById(R.id.step3);
    final Button step4_button = rootView.findViewById(R.id.step4);
    final Button step5_button = rootView.findViewById(R.id.step5);

    step2_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            savePatient();
            final int nextPage = viewPager2.getCurrentItem() + 1;
            viewPager2.setCurrentItem(nextPage, true);
          }
        });
    step3_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            savePatient();
            final int nextPage = viewPager2.getCurrentItem() + 2;
            viewPager2.setCurrentItem(nextPage, true);
          }
        });
    step4_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            savePatient();
            final int nextPage = viewPager2.getCurrentItem() + 3;
            viewPager2.setCurrentItem(nextPage, true);
          }
        });
    step5_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            savePatient();
            final int nextPage = viewPager2.getCurrentItem() + 4;
            viewPager2.setCurrentItem(nextPage, true);
          }
        });

    // step1

    next_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            savePatient();
            final int nextPage = viewPager2.getCurrentItem() + 1;
            viewPager2.setCurrentItem(nextPage, true);
          }
        });

    viewPager2.registerOnPageChangeCallback(
        new ViewPager2.OnPageChangeCallback() {
          @Override
          public void onPageSelected(final int position) {
            super.onPageSelected(position);
            // once scroll, save data
            savePatient();
          }
        });

    return rootView;
  }

  private void savePatient() {
    if (null == patient) {
      patient =
          new Patient(
              dateOfBirth,
              firstName,
              middleName,
              lastName,
              medicareNo,
              westernAffairsNo,
              null,
              null,
              null,
              null);

    } else {
      patient.firstName = first_name.getText().toString();
      patient.middleName = middleName;
      patient.lastName = lastName;
      patient.medicareNo = medicareNo;
      patient.dob = dateOfBirth;
      patient.westernAffairsNo = westernAffairsNo;
    }
    dataListener.onDataFilled(patient, null, null, null, null);
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
