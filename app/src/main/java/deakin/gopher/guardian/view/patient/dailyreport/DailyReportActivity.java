package deakin.gopher.guardian.view.patient.dailyreport;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import deakin.gopher.guardian.view.general.Homepage4admin;
import deakin.gopher.guardian.view.general.Homepage4caretaker;
import deakin.gopher.guardian.view.general.LoginActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;

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

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    Intent intent = null;
                    final int itemId = menuItem.getItemId();
                    if (itemId == R.id.nav_home) {
                        final boolean isAdmin = deakin.gopher.guardian.model.login.SessionManager.INSTANCE.getCurrentUser().getRole() instanceof deakin.gopher.guardian.model.login.Role.Admin;
                        intent = new Intent(DailyReportActivity.this, isAdmin ? Homepage4admin.class : Homepage4caretaker.class);
                    } else if (itemId == R.id.nav_signout) {
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                        intent = new Intent(DailyReportActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    }

                    if (intent != null) {
                        startActivity(intent);
                    }

                    if (null != drawerLayout) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                    return true;
                });

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