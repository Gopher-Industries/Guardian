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

    TextView currentStatusSummary;
    TextView progressNotesSummary;
    CalendarView patientReportSummaryCalendarView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report_summary);

        final NavigationView navigationView = findViewById(R.id.nav_view);
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        dailyReportSummaryMenuButton = findViewById(R.id.menuButton101);

        currentStatusSummary = findViewById(R.id.currentStatusSummary);
        progressNotesSummary = findViewById(R.id.progressNotesSummary);
        patientReportSummaryCalendarView = findViewById(R.id.patientReportSummaryCalendarView);

        // ADDED: Keep original navigation icon colours
        navigationView.setItemIconTintList(null);

        // Open drawer when menu button is clicked
        dailyReportSummaryMenuButton.setOnClickListener(
                v -> drawerLayout.openDrawer(GravityCompat.START));

        final Intent intent = getIntent();
        final String date = intent.getStringExtra(Util.DAILY_REPORT_DATE);
        final String notes = intent.getStringExtra(Util.DAILY_REPORT_STATUS_NOTES);
        final String[] statusList = intent.getStringArrayExtra(Util.DAILY_REPORT_STATUS_LIST);

        // ADDED: Format selected status values for summary display
        if (statusList != null && statusList.length > 0) {
            for (int i = 0; i < statusList.length - 1; i++) {
                statuses.append(statusList[i]).append("\n");
            }
            statuses.append(statusList[statusList.length - 1]);
        } else {
            // ADDED: Fallback text when no statuses are passed
            statuses.append("No status selected");
        }

        // IMPROVED: Safely parse date before updating CalendarView
        if (date != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            final SimpleDateFormat formatter =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            try {
                final Date parsedDate = formatter.parse(date);
                if (parsedDate != null) {
                    dateMs = parsedDate.getTime();
                    patientReportSummaryCalendarView.setDate(dateMs);
                }
            } catch (final ParseException e) {
                e.printStackTrace();
            }
        }

        // ADDED: Fallback text for missing notes
        if (notes != null && !notes.isEmpty()) {
            progressNotesSummary.setText(notes);
        } else {
            progressNotesSummary.setText("No progress notes available");
        }

        currentStatusSummary.setText(statuses.toString());
    }
}