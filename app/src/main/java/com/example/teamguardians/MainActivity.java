package com.example.teamguardians;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Application;
import com.google.android.material.color.DynamicColors;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getStartedButton = findViewById(R.id.getStartedButton);

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getStartedIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(getStartedIntent);
            }
        });
    }
}