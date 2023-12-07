package deakin.gopher.guardian.view.GP;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.GP;
import deakin.gopher.guardian.util.DataListener;

public class GPAddFragment extends Fragment {
  ViewPager2 viewPager2;
  private int status;
  private GP gp;
  private DataListener dataListener;
  private EditText first_name_input,
      middle_name_input,
      last_name_input,
      clinic_address_input,
      phone_number_input,
      email_input,
      fax_input;
  private String firstName, middleName, lastName, clinicAddress, phoneNumber, email, fax;

  public GPAddFragment() {
    // Required empty public constructor
  }

  public GPAddFragment(final int status) {
    this.status = status;
  }

  @Override
  public View onCreateView(
      final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    viewPager2 = requireActivity().findViewById(R.id.dataForViewViewPager);
    final View rootView = inflater.inflate(R.layout.fragment_gp_add, container, false);
    final Button left_button = rootView.findViewById(R.id.gp_add_polygon_left);
    final Button right_button = rootView.findViewById(R.id.gp_add_polygon_right);
    final Button next_button = rootView.findViewById(R.id.gp_add_NextButton);
    final Button prev_button = rootView.findViewById(R.id.gp_add_PrevButton);
    first_name_input = rootView.findViewById(R.id.input_gp_FirstName);
    middle_name_input = rootView.findViewById(R.id.input_gp_MiddleName);
    last_name_input = rootView.findViewById(R.id.input_gp_LastName);
    clinic_address_input = rootView.findViewById(R.id.input_gp_ClinicAddress);
    phone_number_input = rootView.findViewById(R.id.input_gp_Phone);
    email_input = rootView.findViewById(R.id.input_gp_Email);
    fax_input = rootView.findViewById(R.id.input_gp_Fax);

    final Button step1_button = rootView.findViewById(R.id.step1);
    final Button step2_button = rootView.findViewById(R.id.step2);
    final Button step3_button = rootView.findViewById(R.id.step3);
    final Button step4_button = rootView.findViewById(R.id.step4);
    final Button step5_button = rootView.findViewById(R.id.step5);

    if (1 == status) {
      step4_button.setBackgroundResource(R.drawable.roundshapeseletebtn);
      step5_button.setBackgroundResource(R.drawable.roundshapebtn);
    }
    if (2 == status) {
      step5_button.setBackgroundResource(R.drawable.roundshapeseletebtn);
      step4_button.setBackgroundResource(R.drawable.roundshapebtn);
    }

    step1_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveGp();

            viewPager2.setCurrentItem(0, true);
          }
        });

    step2_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveGp();

            viewPager2.setCurrentItem(1, true);
          }
        });
    step3_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveGp();

            viewPager2.setCurrentItem(2, true);
          }
        });
    step4_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveGp();

            viewPager2.setCurrentItem(3, true);
          }
        });
    step5_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveGp();

            viewPager2.setCurrentItem(4, true);
          }
        });

    if (2 == status) {
      left_button.setBackgroundResource(R.drawable.polygon_3);
      right_button.setBackgroundResource(R.drawable.polygon_4);
    } else {
      next_button.setText(R.string.next);
    }

    right_button.setOnClickListener(
        view -> {
          if (1 == status) {
            // for now I keep 2 next of kins and 2 gps waiting to be add
            // but defaultly adding 1 nok and 1gp, after alick right arrow the second one shows up
            // is better
            if (dataChecker()) {
              saveGp();
              scrollPage(true);
            }
          }
        });

    left_button.setOnClickListener(
        view -> {
          if (2 == status) {
            if (dataChecker()) {
              saveGp();
              scrollPage(false);
            }
          }
        });

    next_button.setOnClickListener(
        view -> {
          // als();
          if (dataChecker()) {
            saveGp();
            if (1 == status) {
              scrollPage(true);
            } else {
              final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
              builder.setTitle("Saving Changes?");
              builder.setPositiveButton(
                  "YES",
                  (dialog, whichButton) -> {
                    dataListener.onDataFinished(true);
                  });
              builder.setNegativeButton("No", null);

              final AlertDialog dialog = builder.create();
              dialog.setOnShowListener(
                  arg0 -> {
                    dialog
                        .getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.colorGreen));
                    dialog
                        .getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.colorRed));
                  });
              dialog.show();
            }
          }
        });

    prev_button.setOnClickListener(
        view -> {
          if (dataChecker()) {
            saveGp();
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

  private void saveGp() {
    if (null == gp) {
      gp = new GP(firstName, middleName, lastName, phoneNumber, email, fax, null);
    } else {
      gp.setFirstName(firstName);
      gp.setMiddleName(middleName);
      gp.setLastName(lastName);
      gp.setPhone(phoneNumber);
      gp.setEmail(email);
      gp.setFax(fax);
    }
    if (1 == status) {
      dataListener.onDataFilled(null, null, null, gp, null);
    } else {
      dataListener.onDataFilled(null, null, null, null, gp);
    }
  }

  private boolean dataChecker() {
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
    if (TextUtils.isEmpty(clinicAddress)) {
      setErrorAndReturn(clinic_address_input, "Clinic address is Required.");
    }
    if (TextUtils.isEmpty(phoneNumber)) {
      setErrorAndReturn(phone_number_input, "Phone number is Required.");
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
