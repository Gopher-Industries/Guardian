package com.example.tg_patient_profile.view.general;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tg_patient_profile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final Button getStartedButton = findViewById(R.id.getStartedButton);

    getStartedButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            final Intent getStartedIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(getStartedIntent);
          }
        });

    FirebaseMessaging.getInstance()
        .getToken()
        .addOnCompleteListener(
            new OnCompleteListener<String>() {
              @Override
              public void onComplete(@NonNull final Task<String> task) {
                if (!task.isSuccessful()) {
                  Log.w("tag", "Fetching FCM registration token failed", task.getException());
                  return;
                }

                // Get new FCM registration token
                final String token = task.getResult();

                // Log and toast
                Log.d("token: ", token);
              }
            });
  }

  public void logout(final View view) {
    FirebaseAuth.getInstance().signOut(); // logout
    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    finish();
  }
}
