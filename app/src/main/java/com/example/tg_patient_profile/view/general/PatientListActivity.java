package com.example.tg_patient_profile.view.general;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tg_patient_profile.adapter.PatientListAdapter;
import com.example.tg_patient_profile.model.Patient;
import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.patient.PatientDashboardActivity;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class PatientListActivity extends AppCompatActivity implements PatientListAdapter.OnPatientListener {
    RecyclerView recyclerView;
    PatientListAdapter patientListAdapter;

    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        recyclerView= findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Patient> options =
                new FirebaseRecyclerOptions.Builder<Patient>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("patients"), Patient.class)
                        .build();
        patientListAdapter =new PatientListAdapter(options, this);
        recyclerView.setAdapter(patientListAdapter);

        floatingActionButton=(FloatingActionButton)findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), AddNewPatientActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        patientListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        patientListAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem item =menu.findItem(R.id.search);
        SearchView searchView= (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                txtSearch(query);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void txtSearch(String str){

        FirebaseRecyclerOptions<Patient> options =
                new FirebaseRecyclerOptions.Builder<Patient>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("patients").orderByChild("name").startAt(str).endAt(str+"~"), Patient.class)
                        .build();

        patientListAdapter =new PatientListAdapter(options, this);
        patientListAdapter.startListening();
        recyclerView.setAdapter(patientListAdapter);
    }


    @Override
    public void onPatientClick(int position) {
        Intent patientDashboardActivityIntent = new Intent(PatientListActivity.this, PatientDashboardActivity.class);
        startActivity(patientDashboardActivityIntent);
    }
}