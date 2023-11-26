package deakin.gopher.guardian.view.general;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.PatientProfileAdapter;

public class PatientProfileActivity extends BaseActivity {

  private CustomHeader customHeader;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_patient_profile);

    final TabLayout tabLayout = findViewById(R.id.dataForViewTabLayout);
    final ViewPager2 viewPager2 = findViewById(R.id.dataForViewViewPager);
    customHeader = findViewById(R.id.customHeader);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    final NavigationView navigationView = findViewById(R.id.nav_view);
    final String patient_id = getIntent().getStringExtra("id");

    final PatientProfileAdapter viewPagerAdapter =
        new PatientProfileAdapter(patient_id, getSupportFragmentManager(), getLifecycle());
    viewPager2.setAdapter(viewPagerAdapter);

    customHeader.setHeaderHeight(450);
    customHeader.setHeaderText("Patient Profile");
    customHeader.setHeaderTopImageVisibility(View.VISIBLE);

    navigationView.setItemIconTintList(null);

    if (null != customHeader) {
      customHeader.menuButton.setOnClickListener(
          v -> {
            if (null != drawerLayout) {
              drawerLayout.openDrawer(GravityCompat.START);
            }
          });
    }

    new TabLayoutMediator(
            tabLayout,
            viewPager2,
            (tab, position) -> {
              if (0 == position) {
                tab.setText("Patient");
              } else if (1 == position) {
                tab.setText("Next of Kin");
              } else if (2 == position) {
                tab.setText("General Practitioner");
              } else if (3 == position) {
                tab.setText("Health Details");
              } else if (4 == position) {
                tab.setText("Health & Welfare Det.");
              } else {
                tab.setText("Care Plan");
              }
            })
        .attach();

    tabLayout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabSelected(final TabLayout.Tab tab) {
            final int position = tab.getPosition();
            if (0 == position) {
              customHeader.setHeaderText("Patient Profile");
              customHeader.setHeaderTopImageVisibility(View.VISIBLE);
              customHeader.setHeaderTopImage(R.drawable.profile_avatar_women);
            } else if (1 == position) {
              customHeader.setHeaderText("Next Of Kin Contact");
              customHeader.setHeaderTopImage(R.drawable.profile_avatar_men);
              customHeader.setHeaderTopImageVisibility(View.VISIBLE);
            } else if (2 == position) {
              customHeader.setHeaderText("GP Details");
              customHeader.setHeaderTopImage(R.drawable.profile_avatar_men2);
              customHeader.setHeaderTopImageVisibility(View.VISIBLE);
            } else if (3 == position) {
              customHeader.setHeaderText("Health Details");
              customHeader.setHeaderTopImageVisibility(View.GONE);
            } else if (4 == position) {
              customHeader.setHeaderText("Patient Details");
              customHeader.setHeaderTopImageVisibility(View.GONE);
            } else {
              customHeader.setHeaderText("Care Plan");
              customHeader.setHeaderTopImage(R.drawable.profile_avatar_men);
              customHeader.setHeaderTopImageVisibility(View.VISIBLE);
            }
          }

          @Override
          public void onTabUnselected(final TabLayout.Tab tab) {}

          @Override
          public void onTabReselected(final TabLayout.Tab tab) {}
        });
  }
}
