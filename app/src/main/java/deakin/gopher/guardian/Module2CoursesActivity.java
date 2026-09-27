package deakin.gopher.guardian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Module2CoursesActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_module2_courses);

    TextView btnBack = findViewById(R.id.btnBack);
    TextView medicationTechnique = findViewById(R.id.medicationTechnique);
    TextView medicationSafety = findViewById(R.id.medicationSafety);

    if (btnBack != null) {
      btnBack.setOnClickListener(v -> finish());
    }

    medicationTechnique.setOnClickListener(
        v -> {
          Intent intent =
              new Intent(Module2CoursesActivity.this, MedicationTechniqueActivity.class);
          startActivity(intent);
        });

    medicationSafety.setOnClickListener(
        v -> {
          Intent intent =
              new Intent(Module2CoursesActivity.this, MedicationSafetyActivity.class);
          startActivity(intent);
        });
  }
}