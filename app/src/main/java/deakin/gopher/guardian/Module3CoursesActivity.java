package deakin.gopher.guardian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Module3CoursesActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_module3_courses);

    TextView btnBack = findViewById(R.id.btnBack);
    TextView inspiringLeadershipEI = findViewById(R.id.inspiringLeadershipEI);
    TextView emotionalIntelligenceCommunication =
        findViewById(R.id.emotionalIntelligenceCommunication);

    if (btnBack != null) {
      btnBack.setOnClickListener(v -> finish());
    }

    inspiringLeadershipEI.setOnClickListener(
        v -> {
          Intent intent =
              new Intent(Module3CoursesActivity.this, InspiringLeadershipEIActivity.class);
          startActivity(intent);
        });

    emotionalIntelligenceCommunication.setOnClickListener(
        v -> {
          Intent intent =
              new Intent(
                  Module3CoursesActivity.this, EmotionalIntelligenceCommunicationActivity.class);
          startActivity(intent);
        });
  }
}