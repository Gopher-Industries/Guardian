package com.gopher.guardian.util;

import com.gopher.guardian.model.GP;
import com.gopher.guardian.model.NextOfKin;
import com.gopher.guardian.model.Patient;

public interface DataListener {
  void onDataFilled(Patient patient, NextOfKin nextOfKin1, NextOfKin nextOfKin2, GP gp1, GP gp2);

  void onDataFinihsed(Boolean isFinished);
}
