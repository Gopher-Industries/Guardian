package com.example.tg_patient_profile.view.patient.dailyreport;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tg_patient_profile.R;

public class DailyReportActivity extends AppCompatActivity implements IArrowClick {

  //    DailyReportFragment fragment;
  //    FragmentContainerView fragmentContainerView;
  //    DailyReportScrollVariable dailyReportScrollVariable;
  //    CalendarView dailyReportCalendarView;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_daily_report);

    // Retrieve the patient's name from the intent extras
    final String patientName = getIntent().getStringExtra("patientName").split(" ")[0];

    // Find the TextView for the username
    final TextView usernameTextView = findViewById(R.id.username);

    if (null != patientName) {
      // Set the patient's name in the TextView
      usernameTextView.setText(patientName);
    } else {
      // Handle the case where patientName is not provided
      // You can set a default value or show an error message.
    }

    //
    //        fragmentContainerView = findViewById(R.id.dailyReportFragmentContainer);
    //
    //        fragment = new DailyReportFragment();
    //        FragmentManager fragmentManager = getSupportFragmentManager();
    //        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    //        fragmentTransaction.replace(R.id.dailyReportFragmentContainer, fragment).commit();
    //
    //        fragment.setClickInterface(this);
    //        dailyReportScrollVariable = DailyReportScrollVariable.getInstance();
    //        dailyReportScrollVariable.setListener(new DailyReportScrollVariable.ChangeListener() {
    //            @Override
    //            public void onChange() {
    //                if (DailyReportScrollVariable.getInstance().getScroll()) {
    //                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
    // fragmentContainerView.getLayoutParams();
    //                    params.setMargins(0, -600, 0, 0);
    //                    fragmentContainerView.setLayoutParams(params);
    //                } else {
    //                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
    // fragmentContainerView.getLayoutParams();
    //                    params.setMargins(0, -300, 0, 0);
    //                    fragmentContainerView.setLayoutParams(params);
    //                }
    //            }
    //        });
    //
    //        Date maxDate = new Date(System.currentTimeMillis()); //set your max date here
    //        fragment.dailyReportDate = DateFormat.format("dd/MM/yyyy ",
    // maxDate.getTime()).toString();
    //
    //        dailyReportCalendarView = (CalendarView) findViewById(R.id.calendarView);
    //        dailyReportCalendarView.setMaxDate(maxDate.getTime());
    //        dailyReportCalendarView.setOnDateChangeListener(new
    // CalendarView.OnDateChangeListener() {
    //            @Override
    //            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month,
    // int dayOfMonth) {
    //
    //                fragment.dailyReportDate = dayOfMonth + "/" + (month + 1) + "/" + year;
    //            }
    //        });
    //
    //    }
    //
    //    public void expandView() {
    //        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
    // fragmentContainerView.getLayoutParams();
    //        params.setMargins(0, -300, 0, 0);
    //        fragmentContainerView.setLayoutParams(params);;
    //    }
    //
    //    public void unExpandView() {
    //        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
    // fragmentContainerView.getLayoutParams();
    //        params.setMargins(0, 10, 0, 0);
    //        fragmentContainerView.setLayoutParams(params);
    //    }
    //
    //    public void fullExpandView() {
    //        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
    // fragmentContainerView.getLayoutParams();
    //        params.setMargins(0, -600, 0, 0);
    //        fragmentContainerView.setLayoutParams(params);;
    //    }
    //
    //    @Override
    //    public void arrowClicked(View v) {
    //
    //        if (fragment.expanded) {
    //            unExpandView();
    //        }
    //         else {
    //            expandView();
    //        }
  }

  @Override
  public void arrowClicked(final View v) {}
}
