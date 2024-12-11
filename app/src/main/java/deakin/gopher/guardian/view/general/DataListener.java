package deakin.gopher.guardian.view.general;

import deakin.gopher.guardian.model.Patient;
import deakin.gopher.guardian.model.NextOfKin;
import deakin.gopher.guardian.model.GP;

public interface DataListener {
    void onDataFilled(Patient patient, NextOfKin nextOfKin1, NextOfKin nextOfKin2, GP gp1, GP gp2);
    void onDataFinished(Boolean isFinished);
}
