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

        val prescriptionButton: Button = findViewById(R.id.prescriptionButton)
        val signOutButton: Button = findViewById(R.id.signOutButton_doctor)

        prescriptionButton.setOnClickListener {
            val intent = Intent(this, PrescriptionActivity::class.java)
            startActivity(intent)
        }

        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }
    }
}