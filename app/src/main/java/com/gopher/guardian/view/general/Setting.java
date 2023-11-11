package com.gopher.guardian.view.general;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.gopher.guardian.R;

public class Setting extends AppCompatActivity implements View.OnClickListener {
  ConstraintLayout settingsThemeButton;
  ConstraintLayout settingsNotificationButton;
  ConstraintLayout settingsAppUpdateButton;
  ConstraintLayout settingsFeedbackButton;
  Switch notificationSwitch;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);

    settingsThemeButton = findViewById(R.id.settings_theme_button);
    settingsNotificationButton = findViewById(R.id.settings_notification_button);
    settingsAppUpdateButton = findViewById(R.id.settings_app_update_button);
    settingsFeedbackButton = findViewById(R.id.settings_feedback_button);

    settingsThemeButton.setOnClickListener(this);
    settingsNotificationButton.setOnClickListener(this);
    settingsAppUpdateButton.setOnClickListener(this);
    settingsFeedbackButton.setOnClickListener(this);

    final ConstraintLayout settingsThemeButton = findViewById(R.id.settings_theme_button);
    settingsThemeButton.setOnClickListener(
        v -> {
          final SharedPreferences sharedPreferences =
              getSharedPreferences("settings", MODE_PRIVATE);
          final boolean currentNightMode = sharedPreferences.getBoolean("night_mode", false);
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
    notificationSwitch.setOnCheckedChangeListener(
        (buttonView, isChecked) -> handleNotificationSwitch(isChecked));
  }

  @Override
  public void onClick(final View v) {
    if (R.id.settings_feedback_button == v.getId()) {
      showFeedbackDialog();
    }
  }

  private void handleNotificationSwitch(final boolean isChecked) {
    if (isChecked) {
      showNotification();
      showToast("Notifications turned on");
    } else {
      showToast("Notifications turned off");
    }
  }

  private void showNotification() {
    if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
      final NotificationChannel channel =
          new NotificationChannel(
              "channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
      final NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }

  // feedback
  private void showFeedbackDialog() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Provide Feedback");

    final EditText feedbackEditText = new EditText(this);
    feedbackEditText.setHint("Enter your feedback...");
    builder.setView(feedbackEditText);

    builder.setPositiveButton(
        "Submit",
        (dialog, which) -> {
          final String feedback = feedbackEditText.getText().toString();
          if (!feedback.isEmpty()) {
            showToast("Feedback submitted: " + feedback);
          } else {
            showToast("Please enter your feedback");
          }
        });

    builder.setNegativeButton("Cancel", null);

    final AlertDialog dialog = builder.create();
    dialog.show();
  }

  private void showToast(final CharSequence message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
}
