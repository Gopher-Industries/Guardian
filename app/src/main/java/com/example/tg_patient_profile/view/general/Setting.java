package com.example.tg_patient_profile.view.general;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;
import com.example.tg_patient_profile.R;
import android.widget.Toast;

public class Setting extends AppCompatActivity implements View.OnClickListener {

    ConstraintLayout settings_theme_button;
    ConstraintLayout settings_notification_button;
    ConstraintLayout settings_app_update_button;
    ConstraintLayout settings_feedback_button;
    Button settings_menu_button;
    Switch notificationSwitch;

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

        ConstraintLayout settingsThemeButton = findViewById(R.id.settings_theme_button);
        settingsThemeButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
            boolean currentNightMode = sharedPreferences.getBoolean("night_mode", false);
            if (currentNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPreferences.edit().putBoolean("night_mode", false).apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedPreferences.edit().putBoolean("night_mode", true).apply();
            }
            recreate();
        });

        notificationSwitch = findViewById(R.id.notification_switch);
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> handleNotificationSwitch(isChecked));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.settings_feedback_button:
                showFeedbackDialog();
                break;
            case R.id.settings_app_update_button:
                checkForUpdates();
                break;
            default:
                break;
        }
    }
    // Notifications
    private void handleNotificationSwitch(boolean isChecked) {
        if (isChecked) {
            showNotification();
            showToast("Notifications turned on");
        } else {
            cancelNotification();
            showToast("Notifications turned off");
        }
    }
    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void cancelNotification() {
    }

//feedback
    private void showFeedbackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Provide Feedback");

        final EditText feedbackEditText = new EditText(this);
        feedbackEditText.setHint("Enter your feedback...");
        builder.setView(feedbackEditText);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String feedback = feedbackEditText.getText().toString();
                if (!feedback.isEmpty()) {
                    showToast("Feedback submitted: " + feedback);
                } else {
                    showToast("Please enter your feedback");
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
// app update
    private void checkForUpdates() {
        showToast("Checking for updates...");
        boolean isUpdated = false;

        if (isUpdated) {
            showToast("New version available! Please update.");
        } else {
            showToast("Your app is the latest version!");
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}






