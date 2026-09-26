package deakin.gopher.guardian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Module2CoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module2_courses);

        TextView btnBack = findViewById(R.id.btnBack);
        TextView medicationTechnique =
                findViewById(R.id.medicationTechnique);
        TextView medicationSafety =
                findViewById(R.id.medicationSafety);

        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        medicationTechnique.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =
                                new Intent(
                                        Module2CoursesActivity.this,
                                        MedicationTechniqueActivity.class
                                );

                        startActivity(intent);
                    }
                });

        medicationSafety.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =
                                new Intent(
                                        Module2CoursesActivity.this,
                                        MedicationSafetyActivity.class
                                );

                        startActivity(intent);
                    }
                });
    }
}