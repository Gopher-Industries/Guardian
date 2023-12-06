package deakin.gopher.guardian.view.patient.dailyreport;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.util.Util;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class DailyReportSummaryActivity extends AppCompatActivity {

  private final StringBuilder statuses = new StringBuilder();
  private long dateMs;

  ImageView dailyReportSummaryMenuButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_daily_report_summary);

    final NavigationView navigationView = findViewById(R.id.nav_view);
    dailyReportSummaryMenuButton = findViewById(R.id.menuButton101);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    dailyReportSummaryMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });

    final TextView currentStatusSummary = findViewById(R.id.currentStatusSummary);
    final TextView progressNotesSummary = findViewById(R.id.progressNotesSummary);
    final CalendarView patientReportSummaryCalendarView =
        findViewById(R.id.patientReportSummaryCalendarView);

    final Intent intent = getIntent();
    final String date = intent.getStringExtra(Util.DAILY_REPORT_DATE);
    final String notes = intent.getStringExtra(Util.DAILY_REPORT_STATUS_NOTES);
    final String[] statusList = intent.getStringArrayExtra(Util.DAILY_REPORT_STATUS_LIST);
    if (null != statusList && 0 != statusList.length) {
      for (int i = 0; i < statusList.length - 1; i++) {
        statuses.append(statusList[i]).append("\n");
      }
      statuses.append(statusList[statusList.length - 1]);
    }

    if (android.os.Build.VERSION_CODES.N <= android.os.Build.VERSION.SDK_INT) {
      final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

      try {
        final Date d;
        d = formatter.parse(date);
        dateMs = d.getTime();
      } catch (final ParseException e) {
        e.printStackTrace();
      }
    }

    progressNotesSummary.setText(notes);
    currentStatusSummary.setText(statuses);
    patientReportSummaryCalendarView.setDate(dateMs);
  }
}
