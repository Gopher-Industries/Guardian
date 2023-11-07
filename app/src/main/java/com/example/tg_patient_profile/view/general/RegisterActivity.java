package com.example.tg_patient_profile.view.general;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tg_patient_profile.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
  public static final String TAG = "TAG";
  EditText mEmail, mPassword;
  Button mRegisterBtn;
  TextView mLoginBtn;
  FirebaseAuth Auth;
  ProgressBar progressBar;
  FirebaseFirestore fStore;
  String userID;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

      final Button backToLoginButton = findViewById(R.id.backToLoginButton);

    backToLoginButton.setOnClickListener(
            view -> {
              final Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
              startActivity(loginIntent);
            });

    mEmail = findViewById(R.id.Email);
    mPassword = findViewById(R.id.password);
    mRegisterBtn = findViewById(R.id.registerBtn);
    mLoginBtn = findViewById(R.id.createText);

    Auth = FirebaseAuth.getInstance();
    fStore = FirebaseFirestore.getInstance();
    progressBar = findViewById(R.id.progressBar);

    mRegisterBtn.setOnClickListener(
            v -> {
              final String email = mEmail.getText().toString().trim();
              final String password = mPassword.getText().toString().trim();

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

              // register the user in firebase

              Auth.createUserWithEmailAndPassword(email, password)
                  .addOnCompleteListener(
                          task -> {
                              if (task.isSuccessful()) {

                                  // send verification link

                                  final FirebaseUser fuser = Auth.getCurrentUser();
                                  fuser
                                          .sendEmailVerification()
                                          .addOnSuccessListener(
                                                  aVoid -> Toast.makeText(
                                                                  RegisterActivity.this,
                                                                  "Verification Email Has been Sent.",
                                                                  Toast.LENGTH_SHORT)
                                                          .show())
                                          .addOnFailureListener(
                                                  e -> Log.d(TAG, "onFailure: Email not sent " + e.getMessage()));

                                  Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT)
                                          .show();
                                  userID = Auth.getCurrentUser().getUid();
                                  final DocumentReference documentReference =
                                          fStore.collection("users").document(userID);
                                  final Map<String, Object> user = new HashMap<>();

                                  user.put("email", email);

                                  documentReference
                                          .set(user)
                                          .addOnSuccessListener(
                                                  aVoid -> Log.d(
                                                          TAG, "onSuccess: user Profile is created for " + userID))
                                          .addOnFailureListener(
                                                  e -> Log.d(TAG, "onFailure: " + e));
                                  startActivity(new Intent(getApplicationContext(), MainActivity.class));

                              } else {
                                  Toast.makeText(
                                                  RegisterActivity.this,
                                                  "Error ! " + task.getException().getMessage(),
                                                  Toast.LENGTH_SHORT)
                                          .show();
                                  progressBar.setVisibility(View.GONE);
                              }
                          });
            });

    mLoginBtn.setOnClickListener(
            v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
  }
}
