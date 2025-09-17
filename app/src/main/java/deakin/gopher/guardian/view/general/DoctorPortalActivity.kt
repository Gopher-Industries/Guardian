package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class DoctorPortalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_portal)

        val welcomeTextView: TextView = findViewById(R.id.doctorWelcomeText)
        welcomeTextView.text = "Welcome Doctor!"
    }
}
