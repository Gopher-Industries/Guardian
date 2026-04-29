package deakin.gopher.guardian.view.patient.dailyreport;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.services.EmailPasswordAuthService;
import deakin.gopher.guardian.view.general.Homepage4admin;

public class DailyReportActivity extends AppCompatActivity {

  ImageView dailyReportMenuButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_daily_report);

    final NavigationView navigationView = findViewById(R.id.nav_view);
    dailyReportMenuButton = findViewById(R.id.menuButton11);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    dailyReportMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });

    navigationView.setNavigationItemSelectedListener(
        item -> {
          int id = item.getItemId();
          if (id == R.id.nav_home) {
            startActivity(new Intent(DailyReportActivity.this, Homepage4admin.class));
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

    final String patientNameExtra = getIntent().getStringExtra("patientName");
    final String patientName = patientNameExtra != null ? patientNameExtra.split(" ")[0] : "";

    final TextView usernameTextView = findViewById(R.id.username);

    /*if (null != patientName) {
      usernameTextView.setText(patientName);
    }*/
  }
}
