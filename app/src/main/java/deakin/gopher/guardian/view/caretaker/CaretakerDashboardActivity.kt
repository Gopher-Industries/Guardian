package deakin.gopher.guardian.view.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.FallDetection.FallAlertActivity
import deakin.gopher.guardian.view.FallDetection.FallDetectionActivity
import deakin.gopher.guardian.view.general.BaseActivity
import deakin.gopher.guardian.view.general.PatientListActivity
import deakin.gopher.guardian.view.general.PatientProfileActivity

class CaretakerDashboardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caretaker_dashboard)
    }

    fun onHealthDataClick(view: View?) {
        val healthDataActivityIntent =
            Intent(this@CaretakerDashboardActivity, PatientProfileActivity::class.java)
        startActivity(healthDataActivityIntent)
    }

    fun onNotificationsClick(view: View?) {
        val medicalDiagnosticsActivityIntent =
            Intent(this@CaretakerDashboardActivity, FallAlertActivity::class.java)
        startActivity(medicalDiagnosticsActivityIntent)
    }

    fun onSelectAPatientClick(view: View?) {
        val patientProfileListIntent =
            Intent(this@CaretakerDashboardActivity, PatientListActivity::class.java)
        startActivity(patientProfileListIntent)
    }

    fun onMonitorPatientClick(view: View?) {
        val patientProfileListIntent =
            Intent(this@CaretakerDashboardActivity, FallDetectionActivity::class.java)
        startActivity(patientProfileListIntent)
    }
}
