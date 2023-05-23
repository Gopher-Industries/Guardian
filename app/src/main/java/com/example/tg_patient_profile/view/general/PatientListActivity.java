package com.example.tg_patient_profile.view.general;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tg_patient_profile.adapter.PatientListAdapter;
import com.example.tg_patient_profile.model.Patient;
import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.patient.PatientDashboardActivity;
import com.example.tg_patient_profile.view.patient.careplan.CarePlanActivity;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PatientListActivity extends AppCompatActivity{
    RecyclerView patient_list_recyclerView;
    PatientListAdapter patientListAdapter;
    Query query;
    CardView overview_cardview;
    SearchView patient_searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);
        patient_list_recyclerView = findViewById(R.id.patient_list_recycleView);
        overview_cardview = findViewById(R.id.patient_list_patient_overview);
        patient_searchView = findViewById(R.id.patient_list_searchView);
        //this clicker is for test:
        overview_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientListActivity.this, CarePlanActivity.class));
            }
        });


        Query all_query = FirebaseDatabase.getInstance().getReference().child("patient_profile");

        FirebaseRecyclerOptions<Patient> all_options=
                new FirebaseRecyclerOptions.Builder<Patient>()
                        .setQuery(all_query, new SnapshotParser<Patient>() {
                            @NonNull
                            @Override
                            public Patient parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Patient patient = new Patient(snapshot.child("first_name").getValue().toString(),
                                        snapshot.child("middle_name").getValue().toString(),
                                        snapshot.child("last_name").getValue().toString()
                                );
                                return patient;
                            }
                        })
                        .build();
        PatientListAdapter patientListAdapter_default = new PatientListAdapter(PatientListActivity.this,all_options);
        patient_list_recyclerView.setLayoutManager(new GridLayoutManager(PatientListActivity.this, 1));
        patient_list_recyclerView.setAdapter(patientListAdapter_default);
        patientListAdapter_default.startListening();
        patient_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.isEmpty()){
                    query = FirebaseDatabase.getInstance().getReference().child("patient_profile");
                }else{
                    query = FirebaseDatabase.getInstance().getReference().child("patient_profile")
                            .orderByChild("first_name")
                            .startAt(s)
                            .endAt(s+"\uf8ff")
                            .limitToFirst(10);

                }
                FirebaseRecyclerOptions<Patient> options=
                        new FirebaseRecyclerOptions.Builder<Patient>()
                                .setQuery(query, new SnapshotParser<Patient>() {
                                    @NonNull
                                    @Override
                                    public Patient parseSnapshot(@NonNull DataSnapshot snapshot) {
                                        Patient patient = new Patient(snapshot.child("first_name").getValue().toString(),
                                                snapshot.child("middle_name").getValue().toString(),
                                                snapshot.child("last_name").getValue().toString()
                                        );
                                        return patient;
                                    }
                                })
                                .build();
                patientListAdapter = new PatientListAdapter(PatientListActivity.this,options);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            patientListAdapter = new PatientListAdapter(PatientListActivity.this, options);
                            patient_list_recyclerView.setLayoutManager(new GridLayoutManager(PatientListActivity.this, 1));
                            patient_list_recyclerView.setAdapter(patientListAdapter);
                            patientListAdapter.startListening();
                        }else{
                            patient_list_recyclerView.setLayoutManager(new GridLayoutManager(PatientListActivity.this, 1));
                            patient_list_recyclerView.setAdapter(new PatientListAdapter(PatientListActivity.this, options));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                return true;
            }
        });




    }
    @Override
    protected void onStart() {
        super.onStart();
        //patientListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //patientListAdapter.stopListening();
    }
}