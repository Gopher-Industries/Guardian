package com.example.tg_patient_profile.view.patient.dailyreport;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.util.Util;

import java.util.HashSet;
import java.util.Set;

public class DailyReportFragment extends Fragment{

    private Set<String> chosenPatientStatus = new HashSet<>();
    private String progressNotes;
    public String dailyReportDate;


    private TextView progressTextCountTV;
    private EditText progressEditText;
    private ImageView expandArrowImageView;
    private ScrollView dailyReportScrollView;
    private Button submitButton;
    IArrowClick arrowClick;
    Boolean expanded = false;


    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            progressTextCountTV.setText(String.valueOf(s.length()) + "/500");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public DailyReportFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_report, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        progressEditText = (EditText) view.findViewById(R.id.progressNotesTextarea);
        progressEditText.addTextChangedListener(mTextEditorWatcher);
        progressTextCountTV = (TextView) view.findViewById(R.id.progressTextCount);
        expandArrowImageView = (ImageView) view.findViewById(R.id.dailyReportArrow);
        dailyReportScrollView = (ScrollView) view.findViewById(R.id.dailyReportScrollView);
        submitButton = (Button) view.findViewById(R.id.submitButton);
        LinearLayout layout = view.findViewById(R.id.patientStatusLayout);

        int choicesNum = getResources().getStringArray(R.array.PatientStatusText).length;

        TypedArray texts = getResources().obtainTypedArray(R.array.PatientStatusText);
        TypedArray imgs = getResources().obtainTypedArray(R.array.PatientStatusImage);

        for (int i = 0; i < choicesNum; i++) {
            String text = texts.getString(i);
            int img = imgs.getResourceId(i, -1);

            View statusView = LayoutInflater.from(getActivity()).inflate(R.layout.patient_status_layout,null);
            ImageView statusImageView = statusView.findViewById(R.id.patientStatusImageView);
            TextView statusTextView = statusView.findViewById(R.id.patientStatusTextView);
            statusImageView.setImageResource(img);
            statusTextView.setText(text);
            statusView.setTag(i);


            statusView.setOnClickListener(new View.OnClickListener() {

                boolean clicked = false;
                @Override
                public void onClick(View v) {
                    clicked = !clicked;

                    if (clicked) {
                        statusView.setBackground(getActivity().getResources().getDrawable(R.drawable.blue_rectangle));
                        statusTextView.setTextColor(getActivity().getResources().getColor(R.color.white));

                        chosenPatientStatus.add(statusTextView.getText().toString());
                    } else {
                        statusView.setBackground(getActivity().getResources().getDrawable(R.drawable.white_rectangle));
                        statusTextView.setTextColor(getActivity().getResources().getColor(R.color.default_text));
                        chosenPatientStatus.remove(statusTextView.getText().toString());
                    }
                    Toast.makeText(getContext(), chosenPatientStatus.toString() + " ", Toast.LENGTH_SHORT).show();
                    Log.i("suo", Util.setToArray(chosenPatientStatus).toString());
                }
            });
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                    int width = layout.getWidth();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 30, 0,0);
                    statusView.setLayoutParams(params);
                }
            });


            layout.addView(statusView);
        }

        expandArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrowClick.arrowClicked(v);
                expanded = !expanded;
                if (expanded) {
                    expandArrowImageView.setImageResource(R.drawable.arrow_down);
                } else {
                    expandArrowImageView.setImageResource(R.drawable.arrow_up);
                }
            }
        });

        dailyReportScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                Log.i("tes", dailyReportScrollView.getScrollY() + "") ;
                    if (DailyReportScrollVariable.getInstance().getScroll() && dailyReportScrollView.getScrollY() <= 0) {
                        DailyReportScrollVariable.getInstance().setScroll(false);
                    } else if (!DailyReportScrollVariable.getInstance().getScroll() && dailyReportScrollView.getScrollY() > 0) {
                        DailyReportScrollVariable.getInstance().setScroll(true);
                    }
                }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressNotes = progressEditText.getText().toString();
                if (progressNotes.length() == 0) {
                    Toast.makeText(getActivity(),"Progress notes cannot be empty!",Toast.LENGTH_SHORT).show();
                } else {
                    // push daily report to shared preferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE); // access hard drive through shared preferences object
                    SharedPreferences.Editor editor = sharedPreferences.edit(); // variable to let shared preferences editable
                    editor.putBoolean(Util.DAILY_REPORT_LODGED, true); // key value pair for username
                    editor.putString(Util.DAILY_REPORT_STATUS_NOTES, progressNotes);
                    editor.putStringSet(Util.DAILY_REPORT_STATUS_LIST, chosenPatientStatus);
                    editor.putString(Util.DAILY_REPORT_DATE, dailyReportDate);
                    editor.apply(); // apply changes

                    SharedPreferences prefs = getActivity().getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
                    Intent dailyReportSummaryIntent = new Intent(getActivity(), DailyReportSummaryActivity.class);
                    dailyReportSummaryIntent.putExtra(Util.DAILY_REPORT_DATE, dailyReportDate);
                    dailyReportSummaryIntent.putExtra(Util.DAILY_REPORT_STATUS_NOTES, progressNotes);
                    dailyReportSummaryIntent.putExtra(Util.DAILY_REPORT_STATUS_LIST, Util.setToArray(chosenPatientStatus));
                    Log.i("ada ga seh", "fragment: " + Util.setToArray(chosenPatientStatus).length);
                    startActivity(dailyReportSummaryIntent);
                    getActivity().finish();
                }
            }
        });
    }

    public void setClickInterface(IArrowClick arrowClick) {
        this.arrowClick = arrowClick;
    }

}