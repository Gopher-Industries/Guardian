package com.example.tg_patient_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DailyReportActivity extends AppCompatActivity implements IArrowClick{

    DailyReportFragment fragment;
    FragmentContainerView fragmentContainerView;
    DailyReportScrollVariable dailyReportScrollVariable;

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
                    fragmentContainerView.setLayoutParams(params);;
                } else {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) fragmentContainerView.getLayoutParams();
                    params.setMargins(0, -300, 0, 0);
                    fragmentContainerView.setLayoutParams(params);;
                }
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