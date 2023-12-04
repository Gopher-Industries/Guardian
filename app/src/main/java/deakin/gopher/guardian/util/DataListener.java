package deakin.gopher.guardian.util;

import deakin.gopher.guardian.model.GP;
import deakin.gopher.guardian.model.NextOfKin;
import deakin.gopher.guardian.model.Patient;
import deakin.gopher.guardian.model.Task;

public interface DataListener {
  void onDataFilled(Patient patient, NextOfKin nextOfKin1, NextOfKin nextOfKin2, GP gp1, GP gp2);

  void onTaskDataFilled(Patient patient, Task task);

  void onDataFinihsed(Boolean isFinished);

  void onTaskDataFinished(Boolean isFinished);
}
