package deakin.gopher.guardian.view.general;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import deakin.gopher.guardian.R;

public class Setting extends BaseActivity implements View.OnClickListener {
  ConstraintLayout settingsThemeButton;
  ConstraintLayout settingsNotificationButton;
  ConstraintLayout settingsAppUpdateButton;
  ConstraintLayout settingsFeedbackButton;
  ConstraintLayout settingsSignOutButton;
  Switch notificationSwitch;
  Switch themeSwitch;
  ImageView settingsMenuButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    String userType = getIntent().getStringExtra("userType");

    settingsAppUpdateButton = findViewById(R.id.settings_reset_button);
    settingsThemeButton = findViewById(R.id.settings_theme_button);
    settingsNotificationButton = findViewById(R.id.settings_notification_button);
    settingsFeedbackButton = findViewById(R.id.settings_feedback_button);
    settingsSignOutButton = findViewById(R.id.settings_signout_button);

    settingsSignOutButton.setOnClickListener(this);
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

  @Override
  public void onClick(final View v) {
    if (R.id.settings_feedback_button == v.getId()) {
      showFeedbackDialog();
    }
    if (R.id.settings_reset_button == v.getId()) {
      showResetConfirmationDialog();
    }
    if (R.id.settings_signout_button == v.getId()) { // Add this block
      signOut();
    }
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

  private void showFeedbackDialog() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Provide Feedback");

    // Create a layout for the dialog
    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setPadding(50, 20, 50, 20);

    // Add first name field
    final EditText firstNameEditText = new EditText(this);
    firstNameEditText.setHint("First Name");
    layout.addView(firstNameEditText);

    // Add last name field
    final EditText lastNameEditText = new EditText(this);
    lastNameEditText.setHint("Last Name");
    layout.addView(lastNameEditText);

    // Add email field
    final EditText emailEditText = new EditText(this);
    emailEditText.setHint("Email");
    layout.addView(emailEditText);

    // Add feedback field with word limit
    final EditText feedbackEditText = new EditText(this);
    feedbackEditText.setHint("Your Feedback (max 100 words)");
    layout.addView(feedbackEditText);

    // Add word counter
    final TextView wordCounter = new TextView(this);
    wordCounter.setText("0/100 words");
    wordCounter.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
    layout.addView(wordCounter);

    // Add text watcher for word count
    feedbackEditText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString();
            int words = text.trim().isEmpty() ? 0 : text.split("\\s+").length;
            wordCounter.setText(words + "/100 words");
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });

    builder.setView(layout);

    builder.setPositiveButton(
        "Submit",
        (dialog, which) -> {
          final String firstName = firstNameEditText.getText().toString();
          final String lastName = lastNameEditText.getText().toString();
          final String email = emailEditText.getText().toString();
          final String feedback = feedbackEditText.getText().toString();

          // Check word count
          int wordCount = feedback.trim().isEmpty() ? 0 : feedback.split("\\s+").length;

          if (!firstName.isEmpty()
              && !lastName.isEmpty()
              && !email.isEmpty()
              && !feedback.isEmpty()) {
            if (wordCount <= 100) {
              showToast("Feedback submitted");
            } else {
              showToast("Feedback must be 100 words or less");
            }
          } else {
            showToast("Please fill all fields");
          }
        });

    builder.setNegativeButton("Cancel", null);

    final AlertDialog dialog = builder.create();
    dialog.show();
  }

  private void showToast(final CharSequence message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  private void showResetConfirmationDialog() {
    new AlertDialog.Builder(this)
        .setTitle("Reset Training Module")
        .setMessage(
            "This will reset all the training modules in your profile. This action cannot be reversed.")
        .setPositiveButton(
            "OK",
            (dialog, which) -> {
              // Add your reset logic here
              showToast("Training modules reset");
            })
        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
        .create()
        .show();
  }

  private void signOut() {
    new AlertDialog.Builder(this)
        .setTitle("Sign Out")
        .setMessage("Are you sure you want to sign out?")
        .setPositiveButton(
            "Yes",
            (dialog, which) -> {
              FirebaseAuth.getInstance().signOut();
              startActivity(new Intent(Setting.this, LoginActivity.class));
              finish();
            })
        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
        .create()
        .show();
  }

  @Override
  protected void onResume() {
    super.onResume();
    initializeSwitchStates();
  }
}
