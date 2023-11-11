package com.example.guardian.view.patient.viewactivitydata;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guardian.R;
import com.example.guardian.view.patient.associateradar.ActivitySuggestionActivity;

public class WeeklyActivityProfilingActivity extends AppCompatActivity {
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_weekly_profiling);

    final LinearLayout weeklyActivityProfilingLayout =
        findViewById(R.id.weeklyActivityProfilingLayout);

    final int choicesNum = getResources().getStringArray(R.array.WeeklyActivitySummaryText).length;

    final TypedArray titles =
        getResources().obtainTypedArray(R.array.WeeklyActivitySummaryTitleText);
    final String[] texts = getResources().getStringArray(R.array.WeeklyActivitySummaryText);
    final TypedArray imgs = getResources().obtainTypedArray(R.array.WeeklyActivitySummaryImage);

    for (int i = 0; i < choicesNum; i++) {
      final String text = texts[i];
      final String title = titles.getString(i);
      final int img = imgs.getResourceId(i, -1);

      final View statusView =
          LayoutInflater.from(getApplicationContext())
              .inflate(R.layout.patient_weekly_activity_summary_layout, null);
      final ImageView weeklyActivityImageView =
          statusView.findViewById(R.id.weeklyActivitySummaryIV);
      final TextView weeklyActivitySummaryTitleTextView =
          statusView.findViewById(R.id.weeklyActivitySummaryTitleTV);
      final TextView weeklyActivitySummaryTextView =
          statusView.findViewById(R.id.weeklyActivitySummaryTV);

      weeklyActivityImageView.setImageResource(img);
      weeklyActivitySummaryTitleTextView.setText(title);
      weeklyActivitySummaryTextView.setText(Html.fromHtml(text));

      statusView.setOnClickListener(
          v -> {
            final Intent suggestionIntent =
                new Intent(getApplicationContext(), ActivitySuggestionActivity.class);
            startActivity(suggestionIntent);
          });

      weeklyActivityProfilingLayout.addView(statusView);
    }
  }
}
