package com.example.tg_patient_profile.view.patient.patientdata.generalpractitioner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.tg_patient_profile.adapter.GPListAdapter;
import com.example.tg_patient_profile.model.GP;
import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.general.AddNewGPActivity;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class GPListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    GPListAdapter gp_List_adapter;

    FloatingActionButton floatingActionButton1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gp_list);

        recyclerView= findViewById(R.id.rv1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<GP> options =
                new FirebaseRecyclerOptions.Builder<GP>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("gp"), GP.class)
                        .build();
        gp_List_adapter =new GPListAdapter(options);
        recyclerView.setAdapter(gp_List_adapter);

        floatingActionButton1=(FloatingActionButton)findViewById(R.id.floatingActionButton1);
        floatingActionButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), AddNewGPActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        gp_List_adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gp_List_adapter.stopListening();
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

        FirebaseRecyclerOptions<GP> options =
                new FirebaseRecyclerOptions.Builder<GP>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("gp").orderByChild("first_name").startAt(str).endAt(str+"~"), GP.class)
                        .build();

        gp_List_adapter =new GPListAdapter(options);
        gp_List_adapter.startListening();
        recyclerView.setAdapter(gp_List_adapter);
    }
}