package deakin.gopher.guardian.view.general;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.login.Role;
import deakin.gopher.guardian.model.login.SessionManager;
import deakin.gopher.guardian.services.NavigationService;

public class NurseRosterActivity extends BaseActivity {
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nurse_roster);

    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    final NavigationView navigationView = findViewById(R.id.nav_view);
    final ImageView menuButton = findViewById(R.id.menuButton);
    final NavigationService navigationService = new NavigationService(this);
    final boolean canAddTasks =
        SessionManager.INSTANCE.getCurrentUser().getRole() instanceof Role.Caretaker;

    navigationView.setItemIconTintList(null);
    navigationView.getMenu().findItem(R.id.add_task).setVisible(canAddTasks);

    menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

    navigationView.setNavigationItemSelectedListener(
        menuItem -> {
          final int id = menuItem.getItemId();
          if (R.id.nav_home == id) {
            navigationService.toHomeScreenForRole(
                SessionManager.INSTANCE.getCurrentUser().getRole());
          } else if (R.id.add_task == id && canAddTasks) {
            navigationService.onLaunchTaskCreator();
          } else if (R.id.nav_signout == id) {
            navigationService.onSignOut();
            finish();
          }
          drawerLayout.closeDrawer(GravityCompat.START);
          return true;
        });
  }
}
