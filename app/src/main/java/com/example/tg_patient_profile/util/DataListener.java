package com.example.tg_patient_profile.util;

import com.example.tg_patient_profile.model.GP;
import com.example.tg_patient_profile.model.NextOfKin;
import com.example.tg_patient_profile.model.Patient;

public interface DataListener {
  void onDataFilled(Patient patient, NextOfKin nextOfKin1, NextOfKin nextOfKin2, GP gp1, GP gp2);

  void onDataFinihsed(Boolean isFinished);
}
