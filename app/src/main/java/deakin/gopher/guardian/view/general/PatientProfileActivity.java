package deakin.gopher.guardian.view.general;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.PatientProfileAdapter;

public class PatientProfileActivity extends BaseActivity {

  private CustomHeader customHeader;

  private DrawerLayout drawerLayout;

  private FirebaseAuth mAuth;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_patient_profile);
    final Intent intent = getIntent();

    final TabLayout tabLayout = findViewById(R.id.dataForViewTabLayout);
    final ViewPager2 viewPager2 = findViewById(R.id.dataForViewViewPager);
    customHeader = findViewById(R.id.customHeader);
    drawerLayout = findViewById(R.id.nav_drawer_layout);
    final NavigationView navigationView = findViewById(R.id.nav_view);
    final String patientId = intent.getStringExtra("patientId");
    Log.d("PatientProfileActivity", "Patient ID: " + patientId);

    assert patientId != null;
    final PatientProfileAdapter viewPagerAdapter =
        new PatientProfileAdapter(patientId, getSupportFragmentManager(), getLifecycle());
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

              navigationView.setNavigationItemSelectedListener(
                  menuItem -> {
                    final int id = menuItem.getItemId();
                    if (R.id.nav_home == id) {
                      startActivity(new Intent(PatientProfileActivity.this, Homepage4admin.class));
                    } else if (R.id.nav_admin == id) {
                      startActivity(new Intent(PatientProfileActivity.this, Homepage4admin.class));
                    } else if (R.id.nav_settings == id) {
                      startActivity(new Intent(PatientProfileActivity.this, Setting.class));
                    } else if (R.id.nav_signout == id) {
                      mAuth.getInstance().signOut();
                      startActivity(new Intent(PatientProfileActivity.this, LoginActivity.class));
                      finish();
                    }
                    return true;
                  });
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
              customHeader.setHeaderTopImage(R.drawable.avatar_icon);
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
