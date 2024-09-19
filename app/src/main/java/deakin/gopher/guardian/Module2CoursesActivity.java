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
        setContentView(R.layout.activity_module2_courses);  // Link to the layout file

        // Find TextViews for each course
        TextView medicationTechnique = findViewById(R.id.medicationTechnique);
        TextView medicationSafety = findViewById(R.id.medicationSafety);

        // Set onClickListeners for each course

        // Introduction to Medication Administration Technique
        medicationTechnique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Module2CoursesActivity.this, MedicationTechniqueActivity.class);
                startActivity(intent);
            }
        });

        // Medication Safety and Special Consideration
        medicationSafety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Module2CoursesActivity.this, MedicationSafetyActivity.class);
                startActivity(intent);
            }
        });
    }
}

