package com.example.tg_patient_profile.view.patient.dailyreport;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.util.Util;
import java.text.ParseException;
import java.util.Date;

public class DailyReportSummaryActivity extends AppCompatActivity {

  private final StringBuilder statuses = new StringBuilder();
  private String date;
  private String notes;
  private String[] statusList;
  private long dateMs;

  private TextView currentStatusSummary;
  private TextView progressNotesSummary;
  private CalendarView patientReportSummaryCalendarView;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_daily_report_summary);

    currentStatusSummary = findViewById(R.id.currentStatusSummary);
    progressNotesSummary = findViewById(R.id.progressNotesSummary);
    patientReportSummaryCalendarView = findViewById(R.id.patientReportSummaryCalendarView);

    final Intent intent = getIntent();
    date = intent.getStringExtra(Util.DAILY_REPORT_DATE);
    notes = intent.getStringExtra(Util.DAILY_REPORT_STATUS_NOTES);
    statusList = intent.getStringArrayExtra(Util.DAILY_REPORT_STATUS_LIST);
    if (0 != statusList.length) {
      for (int i = 0; i < statusList.length - 1; i++) {
        statuses.append(statusList[i] + "\n");
      }
      statuses.append(statusList[statusList.length - 1]);
    }

    if (android.os.Build.VERSION_CODES.N <= android.os.Build.VERSION.SDK_INT) {
      final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

      try {
        Date d = null;
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
