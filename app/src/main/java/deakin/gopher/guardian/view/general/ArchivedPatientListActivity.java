package deakin.gopher.guardian.view.general;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.PatientListAdapter;
import deakin.gopher.guardian.model.Patient;
import deakin.gopher.guardian.services.api.ApiClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("Registered")
public class ArchivedPatientListActivity extends BaseActivity {

  private RecyclerView recyclerView;
  private PatientListAdapter adapter;
  private DrawerLayout drawerLayout;
  private ActionBarDrawerToggle toggle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_archived_patient_list);

    recyclerView = findViewById(R.id.archived_patient_recycler_view);
    drawerLayout = findViewById(R.id.drawer_layout);
    NavigationView navigationView = findViewById(R.id.nav_view);

    setupNavigationDrawer();

    // Initialize the adapter with an empty list
    adapter = new PatientListAdapter(this, new ArrayList<>(), true);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    // Fetch archived patients
    fetchArchivedPatients();
  }

  private void fetchArchivedPatients() {
    ApiClient.INSTANCE
            .getApiService()
            .getPatients()
            .enqueue(
                    new Callback<List<Patient>>() {
                      @Override
                      public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                          List<Patient> archivedPatients = new ArrayList<>();
                          for (Patient patient : response.body()) {
                            if (patient.isArchived()) { // Access the field directly
                              archivedPatients.add(patient);
                            }
                          }
                          adapter.updateData(archivedPatients);
                        } else {
                          Log.e("ArchivedPatients", "Failed to fetch patients");
                        }
                      }

                      @Override
                      public void onFailure(Call<List<Patient>> call, Throwable t) {
                        Log.e("ArchivedPatients", "Error fetching patients", t);
                      }
                    });
  }

  private void setupNavigationDrawer() {
    toggle =
            new ActionBarDrawerToggle(
                    this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    findViewById(R.id.patient_list_menu_button)
            .setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
  }

  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }
}

