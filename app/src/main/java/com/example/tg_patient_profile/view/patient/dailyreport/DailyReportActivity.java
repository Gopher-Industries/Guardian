package com.example.tg_patient_profile.view.patient.dailyreport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CalendarView;

import com.example.tg_patient_profile.R;

import java.util.Date;

public class DailyReportActivity extends AppCompatActivity implements IArrowClick{

    DailyReportFragment fragment;
    FragmentContainerView fragmentContainerView;
    DailyReportScrollVariable dailyReportScrollVariable;
    CalendarView dailyReportCalendarView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);

        fragmentContainerView = findViewById(R.id.dailyReportFragmentContainer);

        fragment = new DailyReportFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dailyReportFragmentContainer, fragment).commit();

        fragment.setClickInterface(this);
        dailyReportScrollVariable = DailyReportScrollVariable.getInstance();
        dailyReportScrollVariable.setListener(new DailyReportScrollVariable.ChangeListener() {
            @Override
            public void onChange() {
                if (DailyReportScrollVariable.getInstance().getScroll()) {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) fragmentContainerView.getLayoutParams();
                    params.setMargins(0, -600, 0, 0);
                    fragmentContainerView.setLayoutParams(params);
                } else {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) fragmentContainerView.getLayoutParams();
                    params.setMargins(0, -300, 0, 0);
                    fragmentContainerView.setLayoutParams(params);
                }
            }
        });

        Date maxDate = new Date(System.currentTimeMillis()); //set your max date here
        fragment.dailyReportDate = DateFormat.format("dd/MM/yyyy ", maxDate.getTime()).toString();

        dailyReportCalendarView = (CalendarView) findViewById(R.id.calendarView);
        dailyReportCalendarView.setMaxDate(maxDate.getTime());
        dailyReportCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                fragment.dailyReportDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            }
        });

    }

    public void expandView() {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) fragmentContainerView.getLayoutParams();
        params.setMargins(0, -300, 0, 0);
        fragmentContainerView.setLayoutParams(params);;
    }

    public void unExpandView() {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) fragmentContainerView.getLayoutParams();
        params.setMargins(0, 10, 0, 0);
        fragmentContainerView.setLayoutParams(params);
    }

    public void fullExpandView() {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) fragmentContainerView.getLayoutParams();
        params.setMargins(0, -600, 0, 0);
        fragmentContainerView.setLayoutParams(params);;
    }

    @Override
    public void arrowClicked(View v) {

        if (fragment.expanded) {
            unExpandView();
        }
         else {
            expandView();
        }
    }
}