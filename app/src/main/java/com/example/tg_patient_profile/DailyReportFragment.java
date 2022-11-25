package com.example.tg_patient_profile;

import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyReportFragment extends Fragment implements ItemClickListener{

    private TextView progressTextCountTV;
    private EditText progressEditText;
    private ImageView expandArrowImageView;
    private ScrollView dailyReportScrollView;
    private RecyclerView patientStatusRecyclerView;
    ArrayList<PatientStatusChoice> patientStatusChoiceList = new ArrayList<>();
    IArrowClick arrowClick;
    Boolean expanded = false;
//    Boolean expandedTwice = false;
    private int scrolledDistance = 0;
    DailyReportScrollVariable scrolled;
    int var = 0;

    public ImageView getExpandArrowImageView() {
        return expandArrowImageView;
    }

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
        // Required empty public constructor
    }

    public static DailyReportFragment newInstance(String param1, String param2) {
        DailyReportFragment fragment = new DailyReportFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PatientStatusChoice one = new PatientStatusChoice(R.drawable.hospital_patient_status, "test1");
        PatientStatusChoice two = new PatientStatusChoice(R.drawable.urgent_patient_status, "test2");

        patientStatusChoiceList.add(one); patientStatusChoiceList.add(two);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_daily_report, container, false);

//        patientStatusRecyclerView = (RecyclerView) view.findViewById(R.id.patientStatusRecyclerView);
//
//        PatientStatusRecyclerViewAdapter recyclerViewAdapter = new PatientStatusRecyclerViewAdapter(patientStatusChoiceList, getActivity());
//        //create and set a new adapter to link recycler view to the associated data
//        patientStatusRecyclerView.setAdapter(recyclerViewAdapter);
//
//        // set on click listeners for the adapter
//        // listener for item view
////        recyclerViewAdapter.setClickListener(this);
//
//        //create and set a layout manager to manage the layout of the view
//        patientStatusRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        progressEditText = (EditText) view.findViewById(R.id.progressNotesTextarea);
        progressEditText.addTextChangedListener(mTextEditorWatcher);
        progressTextCountTV = (TextView) view.findViewById(R.id.progressTextCount);
        expandArrowImageView = (ImageView) view.findViewById(R.id.dailyReportArrow);
        dailyReportScrollView = (ScrollView) view.findViewById(R.id.dailyReportScrollView);
        LinearLayout layout = view.findViewById(R.id.patientStatusLayout);

        layout.removeAllViews();

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
                    } else {
                        statusView.setBackground(getActivity().getResources().getDrawable(R.drawable.white_rectangle));
                        statusTextView.setTextColor(getActivity().getResources().getColor(R.color.default_text));
                    }
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
//                if (dailyReportScrollView.getScrollY() != 0) {
                    if (DailyReportScrollVariable.getInstance().getScroll() && dailyReportScrollView.getScrollY() <= 0) {
                        DailyReportScrollVariable.getInstance().setScroll(false);
                    } else if (!DailyReportScrollVariable.getInstance().getScroll() && dailyReportScrollView.getScrollY() > 0) {
                        DailyReportScrollVariable.getInstance().setScroll(true);
                    }
                }
//            }
        });
    }

    public void setClickInterface(IArrowClick arrowClick) {
        this.arrowClick = arrowClick;
    }

    @Override
    public void onClick(View view, int position) {
        Toast.makeText(getActivity(),"Hello from " + position,Toast.LENGTH_SHORT).show();
    }
}