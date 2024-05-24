package deakin.gopher.guardian.view.patient.dailyreport;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import java.util.Objects;

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

    final String patientNameExtra = getIntent().getStringExtra("patientName");
    final String patientName = patientNameExtra != null ? patientNameExtra.split(" ")[0] : "";

    final TextView usernameTextView = findViewById(R.id.username);

    /*if (null != patientName) {
      usernameTextView.setText(patientName);
    }*/
  }
}
