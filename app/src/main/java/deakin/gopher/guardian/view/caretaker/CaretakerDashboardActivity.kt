package deakin.gopher.guardian.view.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.FallDetection.FallAlertActivity
import deakin.gopher.guardian.view.FallDetection.FallDetectionActivity
import deakin.gopher.guardian.view.general.BaseActivity
import deakin.gopher.guardian.view.general.PatientProfileActivity

class CaretakerDashboardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caretaker_dashboard)
    }

    override fun openProfileActivity() {
        val intent = Intent(this, CaretakerProfileActivity::class.java)
        startActivity(intent)
    }

    fun onHealthDataClick(view: View?) {
        startActivity(Intent(this, PatientProfileActivity::class.java))
    }

    fun onNotificationsClick(view: View?) {
        startActivity(Intent(this, FallAlertActivity::class.java))
    }

    fun onMonitorPatientClick(view: View?) {
        startActivity(Intent(this, FallDetectionActivity::class.java))
    }

    fun onProfileClick(view: View?) {
        openProfileActivity()
    }
}
