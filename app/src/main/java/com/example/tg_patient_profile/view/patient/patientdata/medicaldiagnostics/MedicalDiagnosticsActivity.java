package com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MedicalDiagnosticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_diagnostics);

        TabLayout tabLayout = findViewById(R.id.medicalDiagnosticsTabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.medicalDiagnosticsViewPager);


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 0) {
                            tab.setText("Current");
                        }
                        else {
                            tab.setText("Past");
                        }
                    }
                }).attach();
    }
}