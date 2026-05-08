package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.MainActivity as DoctorDashboardActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.services.EmailPasswordAuthService

class Homepage4doctor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4doctor)

        val openPatientsButton: Button = findViewById(R.id.openPatientsButton_doctor)
        val signOutButton: Button = findViewById(R.id.signOutButton_doctor)

        openPatientsButton.setOnClickListener {
            val intent = Intent(this, DoctorDashboardActivity::class.java)
            intent.putExtra("startRoute", "patient_report")
            startActivity(intent)
        }

        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }
    }
}
