package com.example.tg_patient_profile.view.general;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.tg_patient_profile.R;
import com.google.android.material.navigation.NavigationView;

public class DrawerActivity extends AppCompatActivity {
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drawer);

    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

    findViewById(R.id.imageMenu)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(final View view) {
                drawerLayout.openDrawer(GravityCompat.START);
              }
            });

    final NavigationView navigationView = findViewById(R.id.navigationView);
    navigationView.setItemIconTintList(null);

    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
            final int id = menuItem.getItemId();
            if (R.id.menuProfile == id) {
              // Add intent
            } else if (R.id.menuNofications == id) {
              // Add intent
            }
            return true;
          }
        });
  }
}
