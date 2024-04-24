package deakin.gopher.guardian.view.patient.patientdata.healthdata;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;

public class HealthDataActivity extends AppCompatActivity {

  ImageView healthDataMenuButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.last_fifteen_day);

    final NavigationView navigationView = findViewById(R.id.nav_view);
    healthDataMenuButton = findViewById(R.id.menuButton_a);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    healthDataMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });
  }
}
