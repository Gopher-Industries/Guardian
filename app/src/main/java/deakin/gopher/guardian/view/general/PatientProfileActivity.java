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

public class PatientProfileActivity extends AppCompatActivity {

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
              switch (position) {
                case 0:
                  tab.setText("Patient");
                  break;
                case 1:
                  tab.setText("Next of Kin");
                  break;
                case 2:
                  tab.setText("General Practitioner");
                  break;
                case 3:
                  tab.setText("Health Details");
                  break;
                case 4:
                  tab.setText("Health & Welfare Det.");
                  break;
                case 5:
                  tab.setText("Heart Rate");
                  break;
                default:
                  tab.setText("Care Plan");
                  break;
              }
            })
        .attach();

    tabLayout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabSelected(final TabLayout.Tab tab) {
            final int position = tab.getPosition();
            switch (position) {
              case 0 -> {
                customHeader.setHeaderText("Patient Profile");
                customHeader.setHeaderTopImageVisibility(View.VISIBLE);
                customHeader.setHeaderTopImage(R.drawable.profile_avatar_women);
              }
              case 1 -> {
                customHeader.setHeaderText("Next Of Kin Contact");
                customHeader.setHeaderTopImage(R.drawable.profile_avatar_men);
                customHeader.setHeaderTopImageVisibility(View.VISIBLE);
              }
              case 2 -> {
                customHeader.setHeaderText("GP Details");
                customHeader.setHeaderTopImage(R.drawable.profile_avatar_men2);
                customHeader.setHeaderTopImageVisibility(View.VISIBLE);
              }
              case 3 -> {
                customHeader.setHeaderText("Health Details");
                customHeader.setHeaderTopImageVisibility(View.GONE);
              }
              case 4 -> {
                customHeader.setHeaderText("Patient Details");
                customHeader.setHeaderTopImageVisibility(View.GONE);
              }
              case 5 -> {
                customHeader.setHeaderText("Heart Rate");
                customHeader.setHeaderTopImageVisibility(View.GONE);
              }
              default -> {
                customHeader.setHeaderText("Care Plan");
                customHeader.setHeaderTopImage(R.drawable.profile_avatar_men);
                customHeader.setHeaderTopImageVisibility(View.VISIBLE);
              }
            }
          }

          @Override
          public void onTabUnselected(final TabLayout.Tab tab) {}

          @Override
          public void onTabReselected(final TabLayout.Tab tab) {}
        });
  }
}
