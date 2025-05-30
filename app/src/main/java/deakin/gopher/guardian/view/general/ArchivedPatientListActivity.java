package deakin.gopher.guardian.view.general;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.ArchivedPatientListAdapter;
import deakin.gopher.guardian.adapter.SimpleArchivedPatientAdapter;
import deakin.gopher.guardian.model.PatientOldArchive;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("Registered")
public class ArchivedPatientListActivity extends BaseActivity {

  private RecyclerView recyclerView;
  private ArchivedPatientListAdapter adapter;
  private DrawerLayout drawerLayout;
  private ActionBarDrawerToggle toggle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_archived_patient_list);

    recyclerView = findViewById(R.id.archived_patient_recycler_view);

    final List<PatientOldArchive> archivedPatients = new ArrayList<>();
    archivedPatients.add(new PatientOldArchive("1", "John", "Doe"));
    archivedPatients.add(new PatientOldArchive("2", "Jane", "Doe"));

    DatabaseReference patientRef = FirebaseDatabase.getInstance().getReference().child("patients");
    Query archivedQuery = patientRef.orderByChild("is_Archived").equalTo(true);
    archivedQuery.addListenerForSingleValueEvent(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
              Log.d("FirebaseTest", "Archived patients found: " + dataSnapshot.getChildrenCount());
            } else {
              Log.d("FirebaseTest", "No archived patients found.");
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("FirebaseTest", "Error fetching data", databaseError.toException());
          }
        });
    final FirebaseRecyclerOptions<PatientOldArchive> options =
        new FirebaseRecyclerOptions.Builder<PatientOldArchive>()
            .setQuery(archivedQuery, PatientOldArchive.class)
            .build();

    SimpleArchivedPatientAdapter adapter = new SimpleArchivedPatientAdapter(archivedPatients);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    NavigationView navigationView = findViewById(R.id.nav_view);
    drawerLayout = findViewById(R.id.drawer_layout);
    toggle =
        new ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    } else {
      Log.e("ActionBar", "The action bar is not available.");
    }

    findViewById(R.id.patient_list_menu_button)
        .setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

    navigationView.setNavigationItemSelectedListener(
        item -> {
          int id = item.getItemId();
          //
          //      if (id == R.id.nav_home) {
          //      }
          //
          //      if (id == R.id.nav_admin) {
          //      }
          //      if (id == R.id.nav_settings) {
          //      }
          //      if (id == R.id.nav_signout) {
          //      }

          drawerLayout.closeDrawer(GravityCompat.START);
          return true;
        });
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (adapter != null) {
      adapter.startListening();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (adapter != null) {
      adapter.stopListening();
    }
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
