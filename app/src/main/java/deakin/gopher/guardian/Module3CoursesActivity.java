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
        setContentView(R.layout.activity_module3_courses);

        TextView btnBack = findViewById(R.id.btnBack);
        TextView inspiringLeadershipEI =
                findViewById(R.id.inspiringLeadershipEI);
        TextView emotionalIntelligenceCommunication =
                findViewById(R.id.emotionalIntelligenceCommunication);

        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        inspiringLeadershipEI.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =
                                new Intent(
                                        Module3CoursesActivity.this,
                                        InspiringLeadershipEIActivity.class
                                );

                        startActivity(intent);
                    }
                });

        emotionalIntelligenceCommunication.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =
                                new Intent(
                                        Module3CoursesActivity.this,
                                        EmotionalIntelligenceCommunicationActivity.class
                                );

                        startActivity(intent);
                    }
                });
    }
}