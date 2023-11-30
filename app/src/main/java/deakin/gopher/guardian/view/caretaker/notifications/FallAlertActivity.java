package deakin.gopher.guardian.view.caretaker.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.view.caretaker.notifications.confirmincident.ConfirmIncidentActivity;
import deakin.gopher.guardian.view.caretaker.notifications.falsealarm.FalseAlertConfirmedActivity;
import deakin.gopher.guardian.view.general.BaseActivity;

public class FallAlertActivity extends BaseActivity {

  ImageView fallAlertMenuButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fall_alert);

    final NavigationView navigationView = findViewById(R.id.nav_view);
    fallAlertMenuButton = findViewById(R.id.menuButton);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    fallAlertMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });

    final ImageButton confirmIncidentButton = findViewById(R.id.confirmIncidentButton);
    final ImageButton falseAlarmButton = findViewById(R.id.falseAlarmButton);

    confirmIncidentButton.setOnClickListener(
        v -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(FallAlertActivity.this, ConfirmIncidentActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });

    falseAlarmButton.setOnClickListener(
        v -> {
          final Intent medicalDiagnosticsActivityIntent =
              new Intent(FallAlertActivity.this, FalseAlertConfirmedActivity.class);
          startActivity(medicalDiagnosticsActivityIntent);
        });
  }
}
