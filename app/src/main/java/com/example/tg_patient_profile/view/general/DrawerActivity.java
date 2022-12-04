package com.example.tg_patient_profile.view.general;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.view.patient.careplan.Careplan1;
import com.example.tg_patient_profile.view.patient.careplan.Careplan2;
import com.google.android.material.navigation.NavigationView;

public class DrawerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);

        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menuProfile) {
                    Intent newIntent = new Intent(DrawerActivity.this, Careplan1.class);
                    startActivity(newIntent);
                } else if (id == R.id.menuNofications) {
                    Intent newIntent = new Intent(DrawerActivity.this, Careplan2.class);
                    startActivity(newIntent);
                }
                return true;
            }
        });
    }

}