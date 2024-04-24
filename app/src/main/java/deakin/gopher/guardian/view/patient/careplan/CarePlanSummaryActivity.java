package deakin.gopher.guardian.view.patient.careplan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;

public class CarePlanSummaryActivity extends AppCompatActivity {
  Button prevButton;
  ImageView carePlanSummaryMenuButton;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_care_plan_summary);
    prevButton = findViewById(R.id.carePlanPrevButton);

    prevButton.setOnClickListener(
        view -> startActivity(new Intent(CarePlanSummaryActivity.this, CarePlanActivity.class)));

    final NavigationView navigationView = findViewById(R.id.nav_view);
    carePlanSummaryMenuButton = findViewById(R.id.menuButton);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    carePlanSummaryMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });
  }
}
