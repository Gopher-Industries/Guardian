package deakin.gopher.guardian.view.general;

import android.os.Bundle;
import android.widget.Button;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import deakin.gopher.guardian.R;

public class MainPageWithMenuActivity extends BaseActivity {

  DrawerLayout menu_DrawerLayout;
  Button menu_button;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_page_with_menu);
    menu_DrawerLayout = findViewById(R.id.menu_drawerLayout);
    menu_button = findViewById(R.id.menu_button_main_page);

    menu_button.setOnClickListener(
        view -> {
          if (menu_DrawerLayout.isDrawerOpen(GravityCompat.START)) {
            menu_DrawerLayout.closeDrawer(GravityCompat.START);
          } else {
            menu_DrawerLayout.openDrawer(GravityCompat.START);
          }
        });
  }
}
