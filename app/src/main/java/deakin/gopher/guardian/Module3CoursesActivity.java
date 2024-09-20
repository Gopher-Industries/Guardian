package deakin.gopher.guardian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Module3CoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module3_courses);  // Link to the layout file

        // Find TextViews for each course
        TextView inspiringLeadershipEI = findViewById(R.id.inspiringLeadershipEI);
        TextView emotionalIntelligenceCommunication = findViewById(R.id.emotionalIntelligenceCommunication);

        // Set onClickListeners for each course

        // Inspiring Leadership through Emotional Intelligence
        inspiringLeadershipEI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Module3CoursesActivity.this, InspiringLeadershipEIActivity.class);
                startActivity(intent);
            }
        });

        // Emotional Intelligence and Communication
        emotionalIntelligenceCommunication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Module3CoursesActivity.this, EmotionalIntelligenceCommunicationActivity.class);
                startActivity(intent);
            }
        });
    }
}
