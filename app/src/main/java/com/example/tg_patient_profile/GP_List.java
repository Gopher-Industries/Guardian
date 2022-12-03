package com.example.tg_patient_profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class GP_List extends AppCompatActivity {
    RecyclerView recyclerView;
    GP_Adapter gp_adapter;

    FloatingActionButton floatingActionButton1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gp_list);

        recyclerView= findViewById(R.id.rv1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<GP_Model> options =
                new FirebaseRecyclerOptions.Builder<GP_Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("gp"), GP_Model.class)
                        .build();
        gp_adapter=new GP_Adapter(options);
        recyclerView.setAdapter(gp_adapter);

        floatingActionButton1=(FloatingActionButton)findViewById(R.id.floatingActionButton1);
        floatingActionButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(),GP_Add.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        gp_adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gp_adapter.stopListening();
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

        FirebaseRecyclerOptions<GP_Model> options =
                new FirebaseRecyclerOptions.Builder<GP_Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("gp").orderByChild("first_name").startAt(str).endAt(str+"~"), GP_Model.class)
                        .build();

        gp_adapter=new GP_Adapter(options);
        gp_adapter.startListening();
        recyclerView.setAdapter(gp_adapter);
    }
}