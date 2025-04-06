package deakin.gopher.guardian.view.general;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
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
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import deakin.gopher.guardian.R;

public class Setting extends BaseActivity implements View.OnClickListener {
  ConstraintLayout settingsThemeButton;
  ConstraintLayout settingsNotificationButton;
  ConstraintLayout settingsAppUpdateButton;
  ConstraintLayout settingsFeedbackButton;
  Switch notificationSwitch;
  Switch themeSwitch;
  ImageView settingsMenuButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    String userType = getIntent().getStringExtra("userType");

    settingsThemeButton = findViewById(R.id.settings_theme_button);
    settingsNotificationButton = findViewById(R.id.settings_notification_button);
    settingsAppUpdateButton = findViewById(R.id.settings_app_update_button);
    settingsFeedbackButton = findViewById(R.id.settings_feedback_button);

    settingsAppUpdateButton.setOnClickListener(this);
    settingsFeedbackButton.setOnClickListener(this);
    final NavigationView navigationView = findViewById(R.id.nav_view);
    settingsMenuButton = findViewById(R.id.settings_menu_button);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    settingsMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });

    final ConstraintLayout settingsThemeButton = findViewById(R.id.settings_theme_button);

    notificationSwitch = findViewById(R.id.notification_switch);
    notificationSwitch.setOnCheckedChangeListener(
        (buttonView, isChecked) -> handleNotificationSwitch(isChecked));

    themeSwitch = findViewById(R.id.theme_switch);
    themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> handleThemeSwitch(isChecked));
    configureNavigationDrawer(userType);
  }

  private void showUpdateDialog()
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("App Update");
    builder.setMessage("Your app is up to date!");

    builder.setPositiveButton("OK", null);

    builder.show();
  }

  private void checkForUpdates()
  {
    final ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Checking for updates...");
    progressDialog.setCancelable(false);
    progressDialog.show();

    new Handler().postDelayed(() -> {
      progressDialog.dismiss();
      showUpdateDialog();
    }, 2000); // simulate 2-second network check
  }

  @Override
  public void onClick(final View v) {
    if (R.id.settings_feedback_button == v.getId()) {
      showFeedbackDialog();
    } else if (R.id.settings_app_update_button == v.getId()) {
      checkForUpdates();
    }


  }

  private void showCheckingForUpdatesMessage() {
    showToast("Checking for updates...");
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

  private void configureNavigationDrawer(String userType) {
    NavigationView navigationView = findViewById(R.id.nav_view);
    Menu menu = navigationView.getMenu();
    menu.clear();

    navigationView.inflateMenu(R.menu.nav_menu);
    navigationView.setNavigationItemSelectedListener(
        menuItem -> {
          Intent intent = null;
          switch (menuItem.getItemId()) {
            case R.id.nav_home:
              intent =
                  new Intent(
                      Setting.this,
                      userType.equals("admin") ? Homepage4admin.class : Homepage4caretaker.class);
              break;
            case R.id.nav_signout:
              FirebaseAuth.getInstance().signOut();
              startActivity(new Intent(Setting.this, LoginActivity.class));
              finish();
          }

          if (intent != null) {
            startActivity(intent);
          }

          DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
          drawerLayout.closeDrawer(GravityCompat.START);
          return true;
        });
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
