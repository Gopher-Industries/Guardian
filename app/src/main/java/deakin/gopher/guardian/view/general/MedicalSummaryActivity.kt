package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import deakin.gopher.guardian.MedicalSummaryScreen

class MedicalSummaryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val patientId = intent.getStringExtra("patientId") ?: ""
        val patientName = intent.getStringExtra("patientName") ?: ""

        setContent {
            MedicalSummaryScreen(
                patientId = patientId,
                patientName = patientName,
                onAssignNurse = {
                    val assignIntent =
                        Intent(this, AssignNurseActivity::class.java)

                    assignIntent.putExtra(
                        AssignNurseActivity.EXTRA_PATIENT_ID,
                        patientId
                    )

                    assignIntent.putExtra(
                        AssignNurseActivity.EXTRA_PATIENT_NAME,
                        patientName
                    )

                    startActivity(assignIntent)
                },
                onViewActivityLog = {
                }
            )
        }
    }
}