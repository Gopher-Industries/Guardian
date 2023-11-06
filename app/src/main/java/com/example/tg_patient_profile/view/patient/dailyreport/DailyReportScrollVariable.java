package com.example.tg_patient_profile.view.patient.dailyreport;

public class DailyReportScrollVariable {
    private static DailyReportScrollVariable single_instance;
    private boolean scroll;
    private ChangeListener listener;

    private DailyReportScrollVariable() {

    }

    public static DailyReportScrollVariable getInstance() {
        if (null == single_instance)
            single_instance = new DailyReportScrollVariable();

        return single_instance;
    }

    public boolean getScroll() {
        return scroll;
    }

    public void setScroll(final boolean scroll) {
        this.scroll = scroll;
        if (null != listener) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(final ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}
