package deakin.gopher.guardian.view.general;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;

public class NurseRosterActivity extends BaseActivity {
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nurse_roster);

    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    final NavigationView navigationView = findViewById(R.id.nav_view);
    final ImageView menuButton = findViewById(R.id.menuButton);

    DrawerNavigationHelper.bindStandardDrawer(this, drawerLayout, navigationView, menuButton);
  }
}
