package deakin.gopher.guardian.view.general;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.services.EmailPasswordAuthService;

public class NurseRosterActivity extends BaseActivity {
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nurse_roster);

    final NavigationView navigationView = findViewById(R.id.nav_view);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    final ImageView menuButton = findViewById(R.id.menuButton);

    navigationView.setItemIconTintList(null);

    menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

    navigationView.setNavigationItemSelectedListener(
        item -> {
          final int id = item.getItemId();
          if (id == R.id.nav_home) {
            startActivity(new Intent(NurseRosterActivity.this, Homepage4admin.class));
            finish();
          } else if (id == R.id.nav_signout) {
            new AlertDialog.Builder(this)
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
