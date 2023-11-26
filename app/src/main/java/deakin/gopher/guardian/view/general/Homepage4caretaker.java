package deakin.gopher.guardian.view.general;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.login.SessionManager;

public class Homepage4caretaker extends BaseActivity {

  Button patientListButton, settingsButton, signOutButton;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_homepage4caretaker);

    patientListButton = findViewById(R.id.patientListButton);
    settingsButton = findViewById(R.id.settingsButton3);
    signOutButton = findViewById(R.id.sighOutButton);

    // patient list button
    patientListButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4caretaker.this, PatientListActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });

    // settings button
    settingsButton.setOnClickListener(
        view -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(Homepage4caretaker.this, Setting.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });

    // sign out button
    signOutButton.setOnClickListener(
        view -> {
            final SessionManager sessionManager = new SessionManager(getApplicationContext());
            sessionManager.logoutUser();
            FirebaseAuth.getInstance().signOut(); // logout
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
  }
}
