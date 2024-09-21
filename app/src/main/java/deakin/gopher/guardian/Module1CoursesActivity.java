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
    setContentView(R.layout.activity_module1_courses); // Link to the layout file you just created

    // Find TextViews for each course
    TextView homecareInduction = findViewById(R.id.homecareInduction);
    TextView infectionControl = findViewById(R.id.infectionControl);
    TextView supportingMealtimes = findViewById(R.id.supportingMealtimes);

    // Set onClickListeners for each course

    // Homecare Induction
    homecareInduction.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent =
                new Intent(Module1CoursesActivity.this, HomecareInductionActivity.class);
            startActivity(intent);
          }
        });

    // Infection Control
    infectionControl.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(Module1CoursesActivity.this, InfectionControlActivity.class);
            startActivity(intent);
          }
        });

    // Supporting Mealtimes
    supportingMealtimes.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent =
                new Intent(Module1CoursesActivity.this, SupportingMealtimesActivity.class);
            startActivity(intent);
          }
        });
  }
}
