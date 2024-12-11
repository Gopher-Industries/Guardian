package deakin.gopher.guardian.view.general;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.PatientProfileAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import deakin.gopher.guardian.services.api.ApiService;

public class PatientProfileActivity extends BaseActivity {

    private CustomHeader customHeader;
    private DrawerLayout drawerLayout;
    FloatingActionButton fabDelete;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        final Intent intent = getIntent();

        final TabLayout tabLayout = findViewById(R.id.dataForViewTabLayout);
        final ViewPager2 viewPager2 = findViewById(R.id.dataForViewViewPager);
        customHeader = findViewById(R.id.customHeader);
        drawerLayout = findViewById(R.id.nav_drawer_layout);
        fabDelete = findViewById(R.id.fab_delete);

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

        fabDelete.setOnClickListener(
                view -> {
                    if (patientId != null) {
                        deletePatient(patientId);
                    } else {
                        customHeader.setHeaderText("Care Plan Summary");
                        customHeader.setHeaderTopImage(R.drawable.profile_avatar_men);
                        customHeader.setHeaderTopImageVisibility(View.VISIBLE);
                        Toast.makeText(
                                        PatientProfileActivity.this, "Error: Patient ID is null", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

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
                                        } else if (R.id.nav_signout == id) {
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
    }

    private void deletePatient(String patientId) {
        ApiService apiService = getApiService();
        apiService.deletePatient(patientId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PatientProfileActivity.this, "Patient deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                } else {
                    Toast.makeText(PatientProfileActivity.this, "Failed to delete patient: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PatientProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to initialize Retrofit and ApiService
    private ApiService getApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your-backend-url.com/api/") // Replace with your actual base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiService.class);
    }
}
