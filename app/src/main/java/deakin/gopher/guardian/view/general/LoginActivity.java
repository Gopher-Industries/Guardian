package deakin.gopher.guardian.view.general;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import deakin.gopher.guardian.R;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

  EditText mEmail, mPassword;
  Button mLoginBtn;
  TextView mCreateBtn, forgotTextLink;
  ProgressBar progressBar;
  FirebaseAuth Auth;
  RadioGroup role_radioGroup;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    mEmail = findViewById(R.id.Email);
    mPassword = findViewById(R.id.password);
    progressBar = findViewById(R.id.progressBar);
    Auth = FirebaseAuth.getInstance();
    mLoginBtn = findViewById(R.id.loginBtn);
    mCreateBtn = findViewById(R.id.createText);
    forgotTextLink = findViewById(R.id.forgotPassword);
    role_radioGroup = findViewById(R.id.login_role_radioGroup);

    mLoginBtn.setOnClickListener(
        v -> {
          final String email = mEmail.getText().toString().trim();
          final String password = mPassword.getText().toString().trim();
          final String role;
          final int selectedRadioButtonId = role_radioGroup.getCheckedRadioButtonId();
          if (-1 != selectedRadioButtonId) {
            final RadioButton seletedRadioButton = findViewById(selectedRadioButtonId);
            role = seletedRadioButton.getText().toString();
            final SharedPreferences sharedPreferences =
                getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            if (role.equals("Caretaker")) {
              editor.putInt("login_role", 0);

            } else {
              editor.putInt("login_role", 1);
            }
            editor.apply();
          } else {
            Toast.makeText(LoginActivity.this, "please choose a role", Toast.LENGTH_SHORT).show();
            return;
          }

          if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email is Required.");
            return;
          }

          if (TextUtils.isEmpty(password)) {
            mPassword.setError("Password is Required.");
            return;
          }

          if (6 > password.length()) {
            mPassword.setError("Password Must be >= 6 Characters");
            return;
          }

          progressBar.setVisibility(View.VISIBLE);

          // authenticate the user

          Auth.signInWithEmailAndPassword(email, password)
              .addOnCompleteListener(
                  task -> {
                    String role1 = "";
                    final int selectedRadioButtonId1 = role_radioGroup.getCheckedRadioButtonId();
                    if (-1 != selectedRadioButtonId1) {
                      final RadioButton seletedRadioButton = findViewById(selectedRadioButtonId1);
                      role1 = seletedRadioButton.getText().toString();
                    }
                    if (task.isSuccessful()) {
                      Toast.makeText(
                              LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT)
                          .show();
                      if (role1.equals("Caretaker")) {
                        Toast.makeText(
                                LoginActivity.this, "Logged in as CareTaker", Toast.LENGTH_SHORT)
                            .show();
                        startActivity(
                            new Intent(getApplicationContext(), Homepage4caretaker.class));
                      } else {
                        startActivity(new Intent(getApplicationContext(), Homepage4admin.class));
                      }
                    } else {
                      Toast.makeText(
                              LoginActivity.this,
                              "Error ! " + Objects.requireNonNull(task.getException()).getMessage(),
                              Toast.LENGTH_SHORT)
                          .show();
                      progressBar.setVisibility(View.GONE);
                    }
                  });
        });

    mCreateBtn.setOnClickListener(
        v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

    forgotTextLink.setOnClickListener(
        v -> {
          final EditText resetMail = new EditText(v.getContext());
          final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
          passwordResetDialog.setTitle("Reset Password ?");
          passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
          passwordResetDialog.setView(resetMail);

          passwordResetDialog.setPositiveButton(
              "Yes",
              (dialog, which) -> {
                // extract the email and send reset link
                final String mail = resetMail.getText().toString();
                Auth.sendPasswordResetEmail(mail)
                    .addOnSuccessListener(
                        aVoid ->
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Reset Link Sent To Your Email.",
                                    Toast.LENGTH_SHORT)
                                .show())
                    .addOnFailureListener(
                        e ->
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Error ! Reset Link is Not Sent" + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                                .show());
              });

          passwordResetDialog.setNegativeButton(
              "No",
              (dialog, which) -> {
                // close the dialog
              });

          passwordResetDialog.create().show();
        });
  }
}
