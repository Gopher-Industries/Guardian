package com.example.guardian.util;

import com.example.guardian.model.GP;
import com.example.guardian.model.NextOfKin;
import com.example.guardian.model.Patient;

public interface DataListener {
  void onDataFilled(Patient patient, NextOfKin nextOfKin1, NextOfKin nextOfKin2, GP gp1, GP gp2);

  void onDataFinihsed(Boolean isFinished);
}
