package com.example.tg_patient_profile.view.general;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.adapter.PatientProfileAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PatientProfileActivity extends AppCompatActivity {

    private CustomHeader customHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        TabLayout tabLayout = findViewById(R.id.dataForViewTabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.dataForViewViewPager);
        customHeader = findViewById(R.id.customHeader);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        PatientProfileAdapter viewPagerAdapter = new PatientProfileAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(viewPagerAdapter);

        customHeader.setHeaderHeight(450);
        customHeader.setHeaderText("Patient Profile");
        customHeader.setHeaderTopImageVisibility(View.VISIBLE);

        navigationView.setItemIconTintList(null);

        if (customHeader != null) {
            customHeader.menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawerLayout != null) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
        }

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 0) {
                            tab.setText("Patient");
                        }
                        else if (position == 1){
                            tab.setText("Next of Kin");
                        }
                        else if (position == 2){
                            tab.setText("General Practitioner");
                        }
                        else if (position == 3){
                            tab.setText("Health Details");
                        }
                        else {
                            tab.setText("Health & Welfare Det.");
                        }
                    }
                }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    customHeader.setHeaderText("Patient Profile");
                    customHeader.setHeaderTopImageVisibility(View.VISIBLE);
                    customHeader.setHeaderTopImage(R.drawable.profile_avatar_women);
                } else if (position == 1) {
                    customHeader.setHeaderText("Next Of Kin Contact");
                    customHeader.setHeaderTopImage(R.drawable.profile_avatar_men);
                    customHeader.setHeaderTopImageVisibility(View.VISIBLE);
                } else if (position == 2) {
                    customHeader.setHeaderText("GP Details");
                    customHeader.setHeaderTopImage(R.drawable.profile_avatar_men2);
                    customHeader.setHeaderTopImageVisibility(View.VISIBLE);
                } else if (position == 3) {
                    customHeader.setHeaderText("Health Details");
                    customHeader.setHeaderTopImageVisibility(View.GONE);
                } else {
                    customHeader.setHeaderText("Patient Details");
                    customHeader.setHeaderTopImageVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

}


