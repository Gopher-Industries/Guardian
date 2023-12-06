package deakin.gopher.guardian.util;

import deakin.gopher.guardian.model.GP;
import deakin.gopher.guardian.model.NextOfKin;
import deakin.gopher.guardian.model.Patient;

public interface DataListener {
  void onDataFilled(Patient patient, NextOfKin nextOfKin1, NextOfKin nextOfKin2, GP gp1, GP gp2);

  void onDataFinished(Boolean isFinished);
}
