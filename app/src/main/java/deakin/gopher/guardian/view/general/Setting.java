package deakin.gopher.guardian.view.general;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import deakin.gopher.guardian.R;

public class Setting extends BaseActivity implements View.OnClickListener {

  private ConstraintLayout settingsThemeButton;
  private ConstraintLayout settingsNotificationButton;
  private ConstraintLayout settingsAppUpdateButton;
  private ConstraintLayout settingsFeedbackButton;
  private Switch notificationSwitch;
  private Switch themeSwitch; //Aryansharma@1017&
  private ImageView settingsMenuButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Load the saved theme preference from SharedPreferences
    SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
    boolean isNightMode = sharedPreferences.getBoolean("night_mode", false);

    // Apply the correct theme based on the saved preference
    if (isNightMode) {
      setTheme(R.style.Theme_TeamGuardians_Dark);  // Dark theme
    } else {
      setTheme(R.style.Theme_TeamGuardians_Light);  // Light theme
    }

    // Now set the content view after the theme has been applied
    setContentView(R.layout.activity_setting);

    // Continue with the initialization
    String userType = getIntent().getStringExtra("userType");

    // Init views
    settingsThemeButton = findViewById(R.id.settings_theme_button);
    settingsNotificationButton = findViewById(R.id.settings_notification_button);
    settingsAppUpdateButton = findViewById(R.id.settings_app_update_button);
    settingsFeedbackButton = findViewById(R.id.settings_feedback_button);
    notificationSwitch = findViewById(R.id.notification_switch);
    themeSwitch = findViewById(R.id.theme_switch);
    settingsMenuButton = findViewById(R.id.settings_menu_button);

    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    final NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setItemIconTintList(null);

    // Accessibility fix for imageView (in case XML missed it)
    settingsMenuButton.setContentDescription("Open navigation menu");

    // Setup click listeners
    settingsAppUpdateButton.setOnClickListener(this);
    settingsFeedbackButton.setOnClickListener(this);

    settingsMenuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

    // Notification switch
    notificationSwitch.setOnCheckedChangeListener(
            (buttonView, isChecked) -> handleNotificationSwitch(isChecked));

    // Initialize theme switch state
    themeSwitch.setChecked(isNightMode);
    themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> handleThemeSwitch(isChecked));

    configureNavigationDrawer(userType);
  }

  @Override
  public void onClick(final View v) {
    if (v.getId() == R.id.settings_feedback_button) {
      showFeedbackDialog();
    }
  }

  private void initializeSwitchStates() {
    final SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
    boolean isNightMode = sharedPreferences.getBoolean("night_mode", false);

    themeSwitch.setChecked(isNightMode);

    if (isNightMode) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    } else {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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

////  private void handleThemeSwitch(final boolean isChecked) {
////    final SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
////    ConstraintLayout rootLayout = findViewById(R.id.root_layout);
////
////    if (isChecked) {
////      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
////      sharedPreferences.edit().putBoolean("night_mode", true).apply();
//////      rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.black_overlay));
////    } else {
////      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
////      sharedPreferences.edit().putBoolean("night_mode", false).apply();
//////      rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
////    }
////
////    sharedPreferences.edit().apply();
////  }
//
//private void handleThemeSwitch(final boolean isChecked) {
//  final SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
//  ConstraintLayout rootLayout = findViewById(R.id.root_layout);
//
//  // Main layout components
//  ConstraintLayout themeButtonLayout = findViewById(R.id.settings_theme_button);
//  TextView themeButtonTitle = findViewById(R.id.theme_button_title);
//
//  ConstraintLayout notificationButtonLayout = findViewById(R.id.settings_notification_button);
//  TextView notificationButtonTitle = findViewById(R.id.notification_button_title);
//
//  ConstraintLayout appUpdateButtonLayout = findViewById(R.id.settings_app_update_button);
//  TextView appUpdateButtonTitle = findViewById(R.id.app_update_button_title);
//
//  ConstraintLayout feedbackButtonLayout = findViewById(R.id.settings_feedback_button);
//  TextView feedbackButtonTitle = findViewById(R.id.feedback_button_title);
//
//  TextView settingsTitle = findViewById(R.id.medicalDiagnosticsTitleTextView);
//  CardView headerCard = findViewById(R.id.settings_header_card_view);
//
//  if (isChecked) {
//    sharedPreferences.edit().putBoolean("night_mode", true).apply();
//
//    // Set dark background
//    rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.black_overlay));
//    headerCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.black_overlay));
//
//    // Buttons background can stay the same if using drawable
//    // Change text colors to white
//    themeButtonTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
//    notificationButtonTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
//    appUpdateButtonTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
//    feedbackButtonTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
//    settingsTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
//  } else {
//    sharedPreferences.edit().putBoolean("night_mode", false).apply();
//
//    // Set light background
//    rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//    headerCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.TG_blue));
//
//    // Change text colors to default light theme values
//    themeButtonTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
//    notificationButtonTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
//    appUpdateButtonTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
//    feedbackButtonTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
//    settingsTitle.setTextColor(ContextCompat.getColor(this, R.color.white)); // Because the header is still blue
//  }
//}

private void handleThemeSwitch(final boolean isChecked) {
  // Save the theme preference in SharedPreferences
  SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
  SharedPreferences.Editor editor = sharedPreferences.edit();
  editor.putBoolean("night_mode", isChecked);  // Save the theme preference
  editor.apply();

  // Apply the correct theme based on the saved preference
  if (isChecked) {
    // Apply dark theme manually
    setTheme(R.style.Theme_TeamGuardians_Dark);  // Set dark theme
  } else {
    // Apply light theme manually
    setTheme(R.style.Theme_TeamGuardians_Light);  // Set light theme
  }

  // Recreate the activity to apply the new theme
  recreate();
}

  private void configureNavigationDrawer(String userType) {
    NavigationView navigationView = findViewById(R.id.nav_view);
    Menu menu = navigationView.getMenu();
    menu.clear();

    navigationView.inflateMenu(R.menu.nav_menu);

    navigationView.setNavigationItemSelectedListener(menuItem -> {
      Intent intent = null;
      switch (menuItem.getItemId()) {
        case R.id.nav_home:
          intent = new Intent(
                  Setting.this,
                  "admin".equals(userType) ? Homepage4admin.class : Homepage4caretaker.class);
          break;
        case R.id.nav_signout:
          FirebaseAuth.getInstance().signOut();
          startActivity(new Intent(Setting.this, LoginActivity.class));
          finish();
          break;
      }

      if (intent != null) {
        startActivity(intent);
      }

      DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
      drawerLayout.closeDrawer(GravityCompat.START);
      return true;
    });
  }

  private void showNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
              "channel_id", "General Notifications", NotificationManager.IMPORTANCE_DEFAULT);
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      if (notificationManager != null) {
        notificationManager.createNotificationChannel(channel);
      }
    }
  }

  private void showFeedbackDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Provide Feedback");

    final EditText feedbackEditText = new EditText(this);
    feedbackEditText.setHint("Enter your feedback...");
    builder.setView(feedbackEditText);

    builder.setPositiveButton("Submit", (dialog, which) -> {
      String feedback = feedbackEditText.getText().toString().trim();
      if (!feedback.isEmpty()) {
        showToast("Feedback submitted: " + feedback);
      } else {
        showToast("Please enter your feedback");
      }
    });

    builder.setNegativeButton("Cancel", null);

    builder.create().show();
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
