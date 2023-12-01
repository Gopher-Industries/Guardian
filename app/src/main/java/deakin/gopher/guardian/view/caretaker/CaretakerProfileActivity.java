package deakin.gopher.guardian.view.caretaker;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.view.general.BaseActivity;

public class CaretakerProfileActivity extends BaseActivity {

  ImageView caretakerProfileMenuButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_caretakerprofile);

    final NavigationView navigationView = findViewById(R.id.nav_view);
    caretakerProfileMenuButton = findViewById(R.id.patient_list_menu_button);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    caretakerProfileMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });
  }
}
