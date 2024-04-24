package deakin.gopher.guardian.view.nextofkin;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.NextOfKin;
import deakin.gopher.guardian.util.DataListener;

public class NoKAddFragment extends Fragment {

  ViewPager2 viewPager2;
  private int status;
  private NextOfKin nextofKin;
  private DataListener dataListener;
  private EditText firstNameInput,
      middleNameInput,
      lastNameInput,
      addressInput,
      phoneInput,
      emailInput;
  private String firstName, middleName, lastName, address, phoneNumber, email;

  public NoKAddFragment() {
    // Required empty public constructor
  }

  public NoKAddFragment(final int status) {
    this.status = status;
  }

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    viewPager2 = requireActivity().findViewById(R.id.dataForViewViewPager);
    final View rootView = inflater.inflate(R.layout.fragment_nok_add, container, false);
    final Button leftButton = rootView.findViewById(R.id.nok_add_polygon_left);
    final Button rightButton = rootView.findViewById(R.id.nok_add_polygon_right);
    final Button nextButton = rootView.findViewById(R.id.nok_add_NextButton);
    final Button prevButton = rootView.findViewById(R.id.nok_add_PrevButton);
    firstNameInput = rootView.findViewById(R.id.input_nok_FirstName);
    middleNameInput = rootView.findViewById(R.id.input_nok_MiddleName);
    lastNameInput = rootView.findViewById(R.id.input_nok_LastName);
    addressInput = rootView.findViewById(R.id.input_nok_adress);
    phoneInput = rootView.findViewById(R.id.input_nok_PhoneNumber);
    emailInput = rootView.findViewById(R.id.input_nok_EmailAdress);

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
      step3_button.setBackgroundResource(R.drawable.roundshapeseletebtn);
      step2_button.setBackgroundResource(R.drawable.roundshapebtn);
    }

    step1_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveNextofKin();
            // int nextPage = viewPager2.getCurrentItem()-1;
            viewPager2.setCurrentItem(0, true);
          }
        });

    step2_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveNextofKin();

            viewPager2.setCurrentItem(1, true);
          }
        });
    step3_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveNextofKin();
            // int nextPage = viewPager2.getCurrentItem()+1;
            viewPager2.setCurrentItem(2, true);
          }
        });
    step4_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveNextofKin();
            // int nextPage = viewPager2.getCurrentItem()+2;
            viewPager2.setCurrentItem(3, true);
          }
        });
    step5_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveNextofKin();
            // int nextPage = viewPager2.getCurrentItem()+3;
            viewPager2.setCurrentItem(4, true);
          }
        });

    if (2 == status) {
      leftButton.setBackgroundResource(R.drawable.polygon_3);
      rightButton.setBackgroundResource(R.drawable.polygon_4);
    }
    rightButton.setOnClickListener(
        view -> {
          if (1 == status) {
            // for now I keep 2 next of kins and 2 gps waiting to be add
            // but defaultly adding 1 nok and 1gp, after alick right arrow the second one shows up
            // is better
            if (dataChecker()) {
              saveNextofKin();
              scrollPage(true);
            }
          }
        });

    leftButton.setOnClickListener(
        view -> {
          if (2 == status) {
            if (dataChecker()) {
              saveNextofKin();
              scrollPage(false);
            }
          }
        });

    nextButton.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveNextofKin();
            scrollPage(true);
          }
        });

    prevButton.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveNextofKin();
            scrollPage(false);
          }
        });

    return rootView;
  }

  private void scrollPage(final boolean isNextPage) {
    final int nextPage;
    if (isNextPage) {
      nextPage = viewPager2.getCurrentItem() + 1;
    } else {
      nextPage = viewPager2.getCurrentItem() - 1;
    }
    viewPager2.setCurrentItem(nextPage, true);
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
    firstName = firstNameInput.getText().toString();
    middleName = middleNameInput.getText().toString();
    lastName = lastNameInput.getText().toString();
    address = addressInput.getText().toString();
    phoneNumber = phoneInput.getText().toString();
    email = emailInput.getText().toString();

    if (TextUtils.isEmpty(firstName)) {
      setErrorAndReturn(firstNameInput, "First name is Required.");
      return false;
    }
    if (TextUtils.isEmpty(lastName)) {
      setErrorAndReturn(lastNameInput, "Last name is Required.");
      return false;
    }
    if (TextUtils.isEmpty(address)) {
      setErrorAndReturn(addressInput, "Address is Required.");
      return false;
    }
    if (TextUtils.isEmpty(phoneNumber)) {
      setErrorAndReturn(phoneInput, "Phone number is Required.");
      return false;
    }

    return true;
  }

  private void setErrorAndReturn(final EditText editText, final CharSequence s) {
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
