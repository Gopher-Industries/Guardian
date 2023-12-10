package deakin.gopher.guardian.view.general;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import deakin.gopher.guardian.R;

public class MainPageWithMenuActivity extends BaseActivity {

  private DrawerLayout menu_DrawerLayout;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_page_with_menu);
    CustomHeaderForMenuActivity customHeaderForMenuActivity =
        findViewById(R.id.custom_header_menu_activity);
    menu_DrawerLayout = findViewById(R.id.nav_drawer_layout_profile_list_activity);
    final NavigationView navigationView = findViewById(R.id.nav_view_profile_list_activity);

    if (null == savedInstanceState) {
      getSupportFragmentManager()
          .beginTransaction()
          .setReorderingAllowed(true)
          .add(R.id.patient_list_fragment_view, PatientListFragment.class, null)
          .commit();
    }

    customHeaderForMenuActivity.setHeaderHeight(450);
    customHeaderForMenuActivity.setHeaderText("Patient Profile");

    navigationView.setItemIconTintList(null);

    if (null != customHeaderForMenuActivity) {
      customHeaderForMenuActivity.menuButton.setOnClickListener(
          v -> {
            if (null != menu_DrawerLayout) {
              menu_DrawerLayout.openDrawer(GravityCompat.START);

              navigationView.setNavigationItemSelectedListener(
                  menuItem -> {
                    final int id = menuItem.getItemId();
                    if (R.id.nav_home == id) {
                      startActivity(
                          new Intent(MainPageWithMenuActivity.this, Homepage4caretaker.class));
                    } else if (R.id.nav_admin == id) {
                      startActivity(
                          new Intent(MainPageWithMenuActivity.this, Homepage4caretaker.class));
                    } else if (R.id.nav_settings == id) {
                      startActivity(new Intent(MainPageWithMenuActivity.this, Setting.class));
                    } else if (R.id.nav_signout == id) {
                      FirebaseAuth.getInstance().signOut();
                      startActivity(new Intent(MainPageWithMenuActivity.this, LoginActivity.class));
                      finish();
                    }
                    return true;
                  });
            }
          });
    }
  }
}
