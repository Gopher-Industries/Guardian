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
import deakin.gopher.guardian.services.EmailPasswordAuthService;
import deakin.gopher.guardian.view.general.Homepage4admin;

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

    navigationView.setNavigationItemSelectedListener(
        item -> {
          int id = item.getItemId();
          if (id == R.id.nav_home) {
            startActivity(new Intent(CarePlanSummaryActivity.this, Homepage4admin.class));
            finish();
          } else if (id == R.id.nav_signout) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.sign_out)
                .setMessage(R.string.sign_out_confirmation_message)
                .setPositiveButton(
                    R.string.sign_out,
                    (dialog, which) -> {
                      EmailPasswordAuthService.signOut(this);
                      finish();
                    })
                .setNegativeButton(R.string.stay_in, null)
                .show();
          }
          drawerLayout.closeDrawer(GravityCompat.START);
          return true;
        });
  }
}
