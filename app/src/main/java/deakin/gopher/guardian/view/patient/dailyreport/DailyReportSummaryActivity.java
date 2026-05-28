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
import deakin.gopher.guardian.services.EmailPasswordAuthService;
import deakin.gopher.guardian.util.Util;
import deakin.gopher.guardian.view.general.Homepage4admin;
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

        navigationView.setItemIconTintList(null);

        dailyReportSummaryMenuButton.setOnClickListener(
                v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(
                item -> {
                    int id = item.getItemId();
                    if (id == R.id.nav_home) {
                        startActivity(new Intent(DailyReportSummaryActivity.this, Homepage4admin.class));
                        finish();
                    } else if (id == R.id.nav_signout) {
                        new androidx.appcompat.app.AlertDialog.Builder(this)
                                .setTitle(R.string.sign_out)
                                .setMessage(R.string.sign_out_confirmation_message)
                                .setPositiveButton(
                                        R.string.sign_out,
                                        (dialog, which) -> {
                                            EmailPasswordAuthService.signOut(this);
                                            finish();
                                        })
                                .setNegativeButton(R.string.stay_in, null)
                                .show();
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                });

        final Intent intent = getIntent();
        final String date = intent.getStringExtra(Util.DAILY_REPORT_DATE);
        final String notes = intent.getStringExtra(Util.DAILY_REPORT_STATUS_NOTES);
        final String[] statusList = intent.getStringArrayExtra(Util.DAILY_REPORT_STATUS_LIST);

        if (statusList != null && statusList.length > 0) {
            for (int i = 0; i < statusList.length - 1; i++) {
                statuses.append(statusList[i]).append("\n");
            }
            statuses.append(statusList[statusList.length - 1]);
        } else {
            statuses.append("No status selected");
        }

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

        if (notes != null && !notes.isEmpty()) {
            progressNotesSummary.setText(notes);
        } else {
            progressNotesSummary.setText("No progress notes available");
        }

        currentStatusSummary.setText(statuses.toString());
    }
}