package deakin.gopher.guardian.view.patient.dailyreport;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import deakin.gopher.guardian.R;
import java.util.Objects;

public class DailyReportActivity extends AppCompatActivity {
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_daily_report);

    final String patientName =
        Objects.requireNonNull(getIntent().getStringExtra("patientName")).split(" ")[0];

    final TextView usernameTextView = findViewById(R.id.username);

    if (null != patientName) {
      usernameTextView.setText(patientName);
    }
  }
}
