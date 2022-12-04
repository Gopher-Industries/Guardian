package com.example.tg_patient_profile.view.patient.dailyreport;

public class DailyReportScrollVariable {
    private boolean scroll = false;
    private ChangeListener listener;

    private static DailyReportScrollVariable single_instance = null;

    private DailyReportScrollVariable() {

    }

    public static DailyReportScrollVariable getInstance()
    {
        if (single_instance == null)
            single_instance = new DailyReportScrollVariable();

        return single_instance;
    }

    public void setScroll(boolean scroll) {
        this.scroll = scroll;
        if (listener != null) listener.onChange();
    }

    public boolean getScroll() {
        return scroll;
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}
