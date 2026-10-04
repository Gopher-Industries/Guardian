package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.services.EmailPasswordAuthService

class Homepage4doctor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4doctor)

        val signOutButton: Button = findViewById(R.id.signOutButton_doctor)
        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }

        val doctorsListButton: Button = findViewById(R.id.doctorsListButton_doctor)

        doctorsListButton.setOnClickListener {
            startActivity(
                Intent(this, DoctorListActivity::class.java)
            )
        }

        val assignPatientButton: Button =
            findViewById(R.id.assignPatientButton_doctor)

        assignPatientButton.setOnClickListener {
            startActivity(
                Intent(this, AssignNurseActivity::class.java)
            )
        }

        val myPatientsButton: Button =
            findViewById(R.id.myPatientsButton_doctor)

        myPatientsButton.setOnClickListener {
            startActivity(
                Intent(this, PatientListActivity::class.java)
            )
        }

        val healthRecordsButton: Button =
            findViewById(R.id.healthRecordsButton_doctor)

        healthRecordsButton.setOnClickListener {
            val intent = Intent(this, MedicalSummaryActivity::class.java)

            intent.putExtra(
                "patientId",
                ""
            )

            intent.putExtra(
                "patientName",
                "William"
            )

            startActivity(intent)
        }
        val profileButton: Button = findViewById(R.id.profileButton_doctor)

        profileButton.setOnClickListener {
            startActivity(
                Intent(this, DoctorProfileActivity::class.java)
            )
        }
    }
}
