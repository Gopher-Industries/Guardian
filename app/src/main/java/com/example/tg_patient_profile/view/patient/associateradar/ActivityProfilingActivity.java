package com.example.tg_patient_profile.view.patient.associateradar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tg_patient_profile.R;

public class ActivityProfilingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiling);
        LinearLayout activityProfilingLayout = findViewById(R.id.activityProfilingLayout);

        int choicesNum = getResources().getStringArray(R.array.ActivityStatusText).length;


        TypedArray texts = getResources().obtainTypedArray(R.array.ActivityStatusText);
        TypedArray imgs = getResources().obtainTypedArray(R.array.ActivityStatusImage);

        for (int i = 0; i < choicesNum; i++) {
            String text = texts.getString(i);
            int img = imgs.getResourceId(i, -1);

            View statusView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_status_layout,null);
            ConstraintLayout activityStatusBG = (ConstraintLayout) statusView.findViewById(R.id.activityStatusBG);
            ImageView statusImageView = statusView.findViewById(R.id.activityStatusIV);
            TextView statusTextView = statusView.findViewById(R.id.activityStatusTV);
            TextView statusOKTV = statusView.findViewById(R.id.statusOKTV);
            ImageView expandImageView = statusView.findViewById(R.id.activityStatusExpandIV);
            CardView expandedCardView = statusView.findViewById(R.id.activityExpandedCV);
            statusImageView.setImageResource(img);
            statusTextView.setText(text);
            statusView.setTag(i);
            expandImageView.setVisibility(View.VISIBLE);


            statusView.setOnClickListener(new View.OnClickListener() {

                boolean clicked = false;
                @Override
                public void onClick(View v) {
                    clicked = !clicked;

                    if (clicked) {
                        activityStatusBG.setBackground(getResources().getDrawable(R.drawable.blue_rectangle));
                        statusTextView.setTextColor(getResources().getColor(R.color.white));
                        statusOKTV.setText(getResources().getText(R.string.status_ok_white));
                        expandedCardView.setVisibility(View.VISIBLE);
                        expandImageView.setVisibility(View.GONE);
                    } else {
                        activityStatusBG.setBackground(getResources().getDrawable(R.drawable.rectangle));
                        statusTextView.setTextColor(getResources().getColor(R.color.black));
                        statusOKTV.setText(getResources().getText(R.string.status_ok));
                        expandedCardView.setVisibility(View.GONE);
                        expandImageView.setVisibility(View.VISIBLE);
                    }
                }
            });

            activityProfilingLayout.addView(statusView);
    }
}}