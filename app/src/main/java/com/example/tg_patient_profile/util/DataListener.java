package com.example.tg_patient_profile.util;

import com.example.tg_patient_profile.model.GP;
import com.example.tg_patient_profile.model.NextofKin;
import com.example.tg_patient_profile.model.Patient;

public interface DataListener {
    void onDataFilled(Patient patient, NextofKin nextofKin1, NextofKin nextofKin2, GP gp1, GP gp2);

    void onDataFinihsed(Boolean isFinished);
}
