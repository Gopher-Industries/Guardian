package deakin.gopher.guardian.view.general;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import android.widget.Button;
import deakin.gopher.guardian.model.BaseModel;
import deakin.gopher.guardian.model.login.ChangePasswordRequest;
import deakin.gopher.guardian.model.login.SessionManager;
import deakin.gopher.guardian.services.NavigationService;
import deakin.gopher.guardian.services.api.ApiClient;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Setting extends BaseActivity implements View.OnClickListener {
  ConstraintLayout settingsThemeButton;
  ConstraintLayout settingsNotificationButton;
  ConstraintLayout settingsAppUpdateButton;
  ConstraintLayout settingsFeedbackButton;
  ConstraintLayout settingsChangePasswordButton;
  Switch notificationSwitch;
  Switch themeSwitch;
  ImageView settingsMenuButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);

    settingsThemeButton = findViewById(R.id.settings_theme_button);
    settingsNotificationButton = findViewById(R.id.settings_notification_button);
    settingsAppUpdateButton = findViewById(R.id.settings_app_update_button);
    settingsFeedbackButton = findViewById(R.id.settings_feedback_button);
    settingsChangePasswordButton = findViewById(R.id.settings_change_password_button);

    settingsAppUpdateButton.setOnClickListener(this);
    settingsFeedbackButton.setOnClickListener(this);
    settingsChangePasswordButton.setOnClickListener(this);
    final NavigationView navigationView = findViewById(R.id.nav_view);
    settingsMenuButton = findViewById(R.id.settings_menu_button);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    settingsMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });

    DrawerNavigationHelper.bindStandardDrawer(
        this, drawerLayout, navigationView, settingsMenuButton);

    final ConstraintLayout settingsThemeButton = findViewById(R.id.settings_theme_button);

    notificationSwitch = findViewById(R.id.notification_switch);
    notificationSwitch.setOnCheckedChangeListener(
        (buttonView, isChecked) -> handleNotificationSwitch(isChecked));

    themeSwitch = findViewById(R.id.theme_switch);
    themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> handleThemeSwitch(isChecked));
  }

  @Override
  public void onClick(final View v) {
    if (R.id.settings_change_password_button == v.getId()) {
      showChangePasswordDialog();
    } else if (R.id.settings_feedback_button == v.getId()) {
      showFeedbackDialog();
    }
  }

  private void showChangePasswordDialog() {
    final View dialogView =
        getLayoutInflater().inflate(R.layout.dialog_change_password, null);

    final EditText currentPasswordInput =
        dialogView.findViewById(R.id.current_password_input);

    final EditText newPasswordInput =
        dialogView.findViewById(R.id.new_password_input);

    final EditText confirmPasswordInput =
        dialogView.findViewById(R.id.confirm_password_input);

    final AlertDialog dialog =
        new AlertDialog.Builder(this)
            .setTitle(R.string.change_password)
            .setView(dialogView)
            .setPositiveButton(R.string.update_password, null)
            .setNegativeButton(R.string.cancel, null)
            .create();

    dialog.show();

    final Button updateButton =
     dialog.getButton(AlertDialog.BUTTON_POSITIVE);
    updateButton.setOnClickListener(
        button -> {
          final String currentPassword =
              currentPasswordInput.getText().toString();

          final String newPassword =
              newPasswordInput.getText().toString();

          final String confirmPassword =
              confirmPasswordInput.getText().toString();

          if (currentPassword.isEmpty()
              || newPassword.isEmpty()
              || confirmPassword.isEmpty()) {
            showToast(
                getString(R.string.validation_password_fields_required));
            return;
          }

          if (newPassword.length() < 8) {
            newPasswordInput.setError(
                getString(R.string.validation_new_password_length));
            newPasswordInput.requestFocus();
            return;
          }

          if (!newPassword.equals(confirmPassword)) {
            confirmPasswordInput.setError(
                getString(
                    R.string.validation_error_passwords_do_not_match));
            confirmPasswordInput.requestFocus();
            return;
          }

          if (newPassword.equals(currentPassword)) {
            newPasswordInput.setError(
                getString(R.string.validation_new_password_same));
            newPasswordInput.requestFocus();
            return;
          }

          submitChangePassword(
            currentPassword,
            newPassword,
            confirmPassword,
            dialog,
            updateButton);
        });
  }
private void submitChangePassword(
    final String currentPassword,
    final String newPassword,
    final String confirmPassword,
    final AlertDialog dialog,
    final Button updateButton) {

  final String token;

  try {
    token = "Bearer " + SessionManager.INSTANCE.getToken();
  } catch (RuntimeException exception) {
    showToast(getString(R.string.session_expired));
    SessionManager.INSTANCE.logoutUser();
    new NavigationService(this).onSignOut();
    return;
  }

  final ChangePasswordRequest request =
      new ChangePasswordRequest(
          currentPassword,
          newPassword,
          confirmPassword);

  updateButton.setEnabled(false);
  updateButton.setText(R.string.changing_password);

  ApiClient.INSTANCE
      .getApiService()
      .changePassword(token, request)
      .enqueue(
          new Callback<BaseModel>() {
            @Override
            public void onResponse(
                final Call<BaseModel> call,
                final Response<BaseModel> response) {

              if (response.isSuccessful()) {
                dialog.dismiss();
                SessionManager.INSTANCE.logoutUser();

                showToast(
                    getString(
                        R.string.password_changed_sign_in_again));

                new NavigationService(Setting.this).onSignOut();
                return;
              }

              if (response.code() == 401) {
                SessionManager.INSTANCE.logoutUser();
                showToast(getString(R.string.session_expired));
                new NavigationService(Setting.this).onSignOut();
                return;
              }

              updateButton.setEnabled(true);
              updateButton.setText(R.string.update_password);
              showToast(readApiError(response));
            }

            @Override
            public void onFailure(
                final Call<BaseModel> call,
                final Throwable throwable) {

              updateButton.setEnabled(true);
              updateButton.setText(R.string.update_password);
              showToast(
                  getString(R.string.change_password_network_error));
            }
          });
}

private String readApiError(final Response<?> response) {
  try {
    if (response.errorBody() != null) {
      final JSONObject error =
          new JSONObject(response.errorBody().string());

      final String errorMessage = error.optString("error");
      if (!errorMessage.isEmpty()) {
        return errorMessage;
      }

      final String message = error.optString("message");
      if (!message.isEmpty()) {
        return message;
      }
    }
  } catch (Exception ignored) {
    // Use the general message below.
  }

  return getString(R.string.change_password_failed);
}

  private void initializeSwitchStates() {
    final SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
    boolean isNightMode = sharedPreferences.getBoolean("night_mode", false);
    themeSwitch.setChecked(isNightMode);
  }

  private void handleNotificationSwitch(final boolean isChecked) {
    if (isChecked) {
      showNotification();
      showToast("Notifications turned on");
    } else {
      showToast("Notifications turned off");
    }
  }

  private void handleThemeSwitch(final boolean isChecked) {
    final SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
    if (isChecked) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      sharedPreferences.edit().putBoolean("night_mode", true).apply();
    } else {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      sharedPreferences.edit().putBoolean("night_mode", false).apply();
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

  @Override
  protected void onResume() {
    super.onResume();
    initializeSwitchStates();
  }
}
