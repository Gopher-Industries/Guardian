package deakin.gopher.guardian.view.patient.patientdata.healthdata;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import deakin.gopher.guardian.R;

public class HealthDataActivity extends AppCompatActivity {
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.last_fifteen_day);
  }
}
