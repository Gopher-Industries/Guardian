package deakin.gopher.guardian.view.general;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.SimpleArchivedPatientAdapter;
import deakin.gopher.guardian.model.Patient;
import deakin.gopher.guardian.model.PatientStatus;

public class ArchivedPatientListActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_archived_patient_list);

    RecyclerView recyclerView = findViewById(R.id.archived_patient_recycler_view);

    // Create mock data for archived patients
    final List<Patient> archivedPatients = new ArrayList<>();

    // Create Patient objects using setters
    Patient patient1 = new Patient();
    patient1.setPatientId("1");
    patient1.setAddress("123 Main St");
    patient1.setDob("1990-01-01");
    patient1.setFirstName("John");
    patient1.setLastName("Doe");
    patient1.setStatus(PatientStatus.REQUIRES_ASSISTANCE);
    patient1.setArchived(true);
    archivedPatients.add(patient1);

    Patient patient2 = new Patient();
    patient2.setPatientId("2");
    patient2.setAddress("456 Elm St");
    patient2.setDob("1985-05-05");
    patient2.setFirstName("Jane");
    patient2.setLastName("Doe");
    patient2.setStatus(PatientStatus.REQUIRES_ASSISTANCE);
    patient2.setArchived(true);
    archivedPatients.add(patient2);

    // Set up the RecyclerView with the adapter
    SimpleArchivedPatientAdapter adapter = new SimpleArchivedPatientAdapter(archivedPatients);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    // Set up Navigation Drawer
    NavigationView navigationView = findViewById(R.id.nav_view);
    DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    } else {
      Log.e("ActionBar", "The action bar is not available.");
    }

    findViewById(R.id.patient_list_menu_button).setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

    navigationView.setNavigationItemSelectedListener(item -> {
      drawerLayout.closeDrawer(GravityCompat.START);
      return true;
    });
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }
}


