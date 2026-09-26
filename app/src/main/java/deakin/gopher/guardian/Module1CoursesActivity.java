package deakin.gopher.guardian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Module1CoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module1_courses);

        TextView btnBack = findViewById(R.id.btnBack);

        TextView homecareInduction =
                findViewById(R.id.homecareInduction);

        TextView infectionControl =
                findViewById(R.id.infectionControl);

        TextView supportingMealtimes =
                findViewById(R.id.supportingMealtimes);

        // Functional back button
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        // Training 1
        homecareInduction.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =
                                new Intent(
                                        Module1CoursesActivity.this,
                                        HomecareInductionActivity.class
                                );

                        startActivity(intent);
                    }
                });

        // Training 2
        infectionControl.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =
                                new Intent(
                                        Module1CoursesActivity.this,
                                        InfectionControlActivity.class
                                );

                        startActivity(intent);
                    }
                });

        // Training 3
        supportingMealtimes.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =
                                new Intent(
                                        Module1CoursesActivity.this,
                                        SupportingMealtimesActivity.class
                                );

                        startActivity(intent);
                    }
                });
    }
}