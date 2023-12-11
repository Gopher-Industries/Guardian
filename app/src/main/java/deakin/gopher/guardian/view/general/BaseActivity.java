package deakin.gopher.guardian.view.general;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import deakin.gopher.guardian.model.login.SessionManager;

public abstract class BaseActivity extends AppCompatActivity {

  @Override
  protected void onResume() {
    super.onResume();

    // Check for user session expiry and timeout
    final SessionManager sessionManager = new SessionManager(this);
    if (sessionManager.isLoggedIn()) {
      final long lastActiveTime = sessionManager.getLastActiveTime();
      final long currentTime = System.currentTimeMillis();
      final long expiryTime = 30 * 60 * 1000;

      if (expiryTime < currentTime - lastActiveTime) {
        sessionManager.logoutUser();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
      } else {
        sessionManager.updateLastActiveTime();
      }
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    new SessionManager(this).updateLastActiveTime();
  }
}
