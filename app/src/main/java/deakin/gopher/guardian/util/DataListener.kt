package deakin.gopher.guardian.util

import deakin.gopher.guardian.model.GP
import deakin.gopher.guardian.model.NextOfKin
import deakin.gopher.guardian.model.PatientOldArchive

interface DataListener {
    fun onDataFilled(
        patient: PatientOldArchive?,
        nextOfKin1: NextOfKin?,
        nextOfKin2: NextOfKin?,
        gp1: GP?,
        gp2: GP?,
    )

    fun onDataFinished(isFinished: Boolean?)
}
