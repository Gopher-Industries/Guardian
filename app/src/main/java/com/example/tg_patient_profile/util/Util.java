package com.example.tg_patient_profile.util;

import java.util.Set;

public class Util {

    //variables for shared preferences
    public static final String DAILY_REPORT_LODGED = "daily_report_lodged";
    public static final String SHARED_PREF_DATA = "shared_pref_data";
    public static final String DAILY_REPORT_DATE = "daily_report_date";
    public static final String DAILY_REPORT_STATUS_LIST = "daily_report_status";
    public static final String DAILY_REPORT_STATUS_NOTES = "daily_report_notes";


    // care plan
    public static final String CARE_PLAN_TYPE = "care_plan_type";
    public static final String NUTRITION_HYDRATION_TYPE = "nutrition_hydration_type";
    public static final String SUPPORT_REQUIREMENTS = "support_requirements";
    public static final String DIET_TIMING = "diet_timing";
    public static final String DRINK_LIKES = "drink_likes";
    public static final String SLEEP_PATTERN = "sleep_pattern";
    public static final String PAIN = "pain";
    public static final String PAIN_SCORE = "pain_score";
    public static final String BEHAVIOUR_MANAGEMENT = "behaviour_management";










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
