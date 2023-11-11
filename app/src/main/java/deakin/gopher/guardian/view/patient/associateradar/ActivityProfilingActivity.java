package deakin.gopher.guardian.view.patient.associateradar;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import deakin.gopher.guardian.R;

public class ActivityProfilingActivity extends AppCompatActivity {
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profiling);
    final LinearLayout activityProfilingLayout = findViewById(R.id.activityProfilingLayout);

    final int choicesNum = getResources().getStringArray(R.array.ActivityStatusText).length;

    final TypedArray texts = getResources().obtainTypedArray(R.array.ActivityStatusText);
    final TypedArray imgs = getResources().obtainTypedArray(R.array.ActivityStatusImage);

    for (int i = 0; i < choicesNum; i++) {
      final String text = texts.getString(i);
      final int img = imgs.getResourceId(i, -1);

      final View statusView =
          LayoutInflater.from(getApplicationContext())
              .inflate(R.layout.activity_status_layout, null);
      final ConstraintLayout activityStatusBG = statusView.findViewById(R.id.activityStatusBG);
      final ImageView statusImageView = statusView.findViewById(R.id.activityStatusIV);
      final TextView statusTextView = statusView.findViewById(R.id.activityStatusTV);
      final TextView statusOKTV = statusView.findViewById(R.id.statusOKTV);
      final ImageView expandImageView = statusView.findViewById(R.id.activityStatusExpandIV);
      final CardView expandedCardView = statusView.findViewById(R.id.activityExpandedCV);
      statusImageView.setImageResource(img);
      statusTextView.setText(text);
      statusView.setTag(i);
      expandImageView.setVisibility(View.VISIBLE);

      statusView.setOnClickListener(
          new View.OnClickListener() {

            boolean clicked;

            @Override
            public void onClick(final View v) {
              clicked = !clicked;

              if (clicked) {
                activityStatusBG.setBackground(
                    getResources().getDrawable(R.drawable.blue_rectangle));
                statusTextView.setTextColor(getResources().getColor(R.color.white));
                statusOKTV.setText(getResources().getText(R.string.status_ok_white));
                expandedCardView.setVisibility(View.VISIBLE);
                expandImageView.setVisibility(View.GONE);
              } else {
                activityStatusBG.setBackground(getResources().getDrawable(R.drawable.rectangle));
                statusTextView.setTextColor(getResources().getColor(R.color.black));
                statusOKTV.setText(getResources().getText(R.string.status_ok));
                expandedCardView.setVisibility(View.GONE);
                expandImageView.setVisibility(View.VISIBLE);
              }
            }
          });

      activityProfilingLayout.addView(statusView);
    }
  }
}
