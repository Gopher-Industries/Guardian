package com.example.tg_patient_profile.view.general;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tg_patient_profile.R;

public class Setting extends AppCompatActivity implements View.OnClickListener {

    ConstraintLayout settings_theme_button;
    ConstraintLayout settings_notification_button;
    ConstraintLayout settings_app_update_button;
    ConstraintLayout settings_feedback_button;
    Button settings_menu_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settings_theme_button = findViewById(R.id.settings_theme_button);
        settings_notification_button = findViewById(R.id.settings_notification_button);
        settings_app_update_button = findViewById(R.id.settings_app_update_button);
        settings_feedback_button = findViewById(R.id.settings_feedback_button);


        settings_theme_button.setOnClickListener(this);
        settings_notification_button.setOnClickListener(this);
        settings_app_update_button.setOnClickListener(this);
        settings_feedback_button.setOnClickListener(this);
    }

    public void onClick(View v) {
        // do something when the button is clicked
        switch (v.getId()) {
            case R.id.settings_theme_button:
                System.out.println("Settings theme button clicked");
                break;
            case R.id.settings_notification_button:
                System.out.println("Settings Notification button clicked");
                break;
            case R.id.settings_app_update_button:
                System.out.println("Settings App Update button clicked");
                break;
            case R.id.settings_feedback_button:
                System.out.println("Settings Feedback button clicked");
                break;
            default:
                break;
        }
    }
}