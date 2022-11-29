package com.example.tg_patient_profile;

import java.util.Set;

public class Util {

    //variables for shared preferences
    public static final String DAILY_REPORT_LODGED = "daily_report_lodged";
    public static final String SHARED_PREF_DATA = "shared_pref_data";
    public static final String DAILY_REPORT_DATE = "daily_report_date";
    public static final String DAILY_REPORT_STATUS_LIST = "daily_report_status";
    public static final String DAILY_REPORT_STATUS_NOTES = "daily_report_notes";

    public static final String[] setToArray(Set<String> hashSet) {
        String arr[] = new String[hashSet.size()];

        int i = 0;

        // iterating over the hashset
        for (String ele : hashSet) {
            arr[i++] = ele;
        }
        return arr;
    }
}
