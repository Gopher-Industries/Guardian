package deakin.gopher.guardian.view.general;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import deakin.gopher.guardian.model.login.RoleName;
import deakin.gopher.guardian.model.login.SessionManager;
import com.google.firebase.messaging.FirebaseMessaging;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.view.general.Homepage4caretaker;
import deakin.gopher.guardian.services.NavigationService;

public class MainActivity extends BaseActivity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    setContentView(R.layout.activity_main);

    final Button getStartedButton = findViewById(R.id.getStartedButton);

    getStartedButton.setOnClickListener(
        this::onClick);

    FirebaseMessaging.getInstance()
        .getToken()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                Log.w("tag", "Fetching FCM registration token failed", task.getException());
                return;
              }

              // Get new FCM registration token
              final String token = task.getResult();

              // Log and toast
              Log.d("token: ", token);
            });
  }

  public void logout(final View view) {
      final SessionManager sessionManager = new SessionManager(getApplicationContext());
      sessionManager.logoutUser();
      FirebaseAuth.getInstance().signOut(); // logout
      startActivity(new Intent(getApplicationContext(), LoginActivity.class));
      finish();
  }

    private void onClick(final View view) {
        final SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            final Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }else{
            final Intent getStartedIntent = new Intent(MainActivity.this, Homepage4caretaker.class);
            startActivity(getStartedIntent);
        }


    }
}
