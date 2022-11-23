package com.example.tg_patient_profile;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyReportFragment extends Fragment {

    private TextView progressTextCountTV;
    private EditText progressEditText;
    private ImageView expandArrowImageView;
    IArrowClick arrowClick;
    Boolean expanded = false;

    public ImageView getExpandArrowImageView() {
        return expandArrowImageView;
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            progressTextCountTV.setText(String.valueOf(s.length()) + "/500");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public DailyReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailyReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyReportFragment newInstance(String param1, String param2) {
        DailyReportFragment fragment = new DailyReportFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_report, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        progressEditText = (EditText) view.findViewById(R.id.progressNotesTextarea);
        progressEditText.addTextChangedListener(mTextEditorWatcher);
        progressTextCountTV = (TextView) view.findViewById(R.id.progressTextCount);
        expandArrowImageView = (ImageView) view.findViewById(R.id.dailyReportArrow);

        expandArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrowClick.arrowClicked(v);

                expanded = !expanded;

                if (expanded) {
                    expandArrowImageView.setImageResource(R.drawable.arrow_up);
                } else {
                    expandArrowImageView.setImageResource(R.drawable.arrow_down);
                }
            }
        });
    }

    public void setInterface(IArrowClick arrowClick) {
        this.arrowClick = arrowClick;
    }
}