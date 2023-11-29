package deakin.gopher.guardian.view.general;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.PatientListAdapter;
import deakin.gopher.guardian.model.Patient;
import deakin.gopher.guardian.view.patient.careplan.CarePlanActivity;

public class PatientListActivity extends AppCompatActivity {
  RecyclerView patient_list_recyclerView;
  PatientListAdapter patientListAdapter;
  Query query;
  CardView overview_cardview;
  SearchView patient_searchView;
  ImageView patientListMenuButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_patient_list);
    patient_list_recyclerView = findViewById(R.id.patient_list_recycleView);
    overview_cardview = findViewById(R.id.patient_list_patient_overview);
    patient_searchView = findViewById(R.id.patient_list_searchView);
    // this clicker is for test:
    overview_cardview.setOnClickListener(
        view -> startActivity(new Intent(PatientListActivity.this, CarePlanActivity.class)));
    final NavigationView navigationView = findViewById(R.id.nav_view);
    patientListMenuButton = findViewById(R.id.patient_list_menu_button);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    patientListMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });

    final Query all_query = FirebaseDatabase.getInstance().getReference().child("patient_profile");
    final FirebaseRecyclerOptions<Patient> all_options =
        new FirebaseRecyclerOptions.Builder<Patient>()
            .setQuery(
                all_query,
                snapshot -> {
                  final String firstname =
                      null == snapshot.child("first_name").getValue()
                          ? ""
                          : snapshot.child("first_name").getValue().toString();
                  final String middlename =
                      null == snapshot.child("middle_name").getValue()
                          ? ""
                          : snapshot.child("middle_name").getValue().toString();
                  final String lastname =
                      null == snapshot.child("last_name").getValue()
                          ? ""
                          : snapshot.child("last_name").getValue().toString();

                  final Patient patient = new Patient(snapshot.getKey(), firstname, lastname);

                  if ("" != middlename) patient.setMiddleName(middlename);

                  return patient;
                })
            .build();
    final PatientListAdapter patientListAdapter_default =
        new PatientListAdapter(PatientListActivity.this, all_options);
    patient_list_recyclerView.setLayoutManager(new GridLayoutManager(PatientListActivity.this, 1));
    patient_list_recyclerView.setAdapter(patientListAdapter_default);
    patientListAdapter_default.startListening();
    patient_searchView.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(final String s) {
            return false;
          }

          @Override
          public boolean onQueryTextChange(final String s) {
            if (s.isEmpty()) {
              query = FirebaseDatabase.getInstance().getReference().child("patient_profile");
            } else {
              query =
                  FirebaseDatabase.getInstance()
                      .getReference()
                      .child("patient_profile")
                      .orderByChild("first_name")
                      .startAt(s)
                      .endAt(s + "\uf8ff")
                      .limitToFirst(10);
            }
            final FirebaseRecyclerOptions<Patient> options =
                new FirebaseRecyclerOptions.Builder<Patient>()
                    .setQuery(
                        query,
                        snapshot -> {
                          final Patient patient =
                              new Patient(
                                  snapshot.getKey(),
                                  snapshot.child("first_name").getValue().toString(),
                                  snapshot.child("last_name").getValue().toString());
                          final Object middle_name = snapshot.child("middle_name").getValue();
                          if (null != middle_name) patient.setMiddleName(middle_name.toString());
                          return patient;
                        })
                    .build();
            patientListAdapter = new PatientListAdapter(PatientListActivity.this, options);
            query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull final DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                      patientListAdapter =
                          new PatientListAdapter(PatientListActivity.this, options);
                      patient_list_recyclerView.setLayoutManager(
                          new GridLayoutManager(PatientListActivity.this, 1));
                      patient_list_recyclerView.setAdapter(patientListAdapter);
                      patientListAdapter.startListening();
                    } else {
                      patient_list_recyclerView.setLayoutManager(
                          new GridLayoutManager(PatientListActivity.this, 1));
                      patient_list_recyclerView.setAdapter(
                          new PatientListAdapter(PatientListActivity.this, options));
                    }
                  }

                  @Override
                  public void onCancelled(@NonNull final DatabaseError error) {}
                });

            return true;
          }
        });
  }
}
