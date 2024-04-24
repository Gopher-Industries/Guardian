package deakin.gopher.guardian.view.general;

import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;

public class DrawerActivity extends BaseActivity {
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drawer);

    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

    findViewById(R.id.imageMenu)
        .setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

    final NavigationView navigationView = findViewById(R.id.navigationView);
    navigationView.setItemIconTintList(null);

    navigationView.setNavigationItemSelectedListener(
        menuItem -> {
          final int id = menuItem.getItemId();
          if (R.id.menuProfile == id) {
            // Add intent
          } else if (R.id.menuNofications == id) {
            // Add intent
          }
          return true;
        });
  }
}
