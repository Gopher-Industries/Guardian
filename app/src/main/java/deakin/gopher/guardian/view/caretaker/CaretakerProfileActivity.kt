package deakin.gopher.guardian.view.caretaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.general.BaseActivity
import deakin.gopher.guardian.view.general.Homepage4caretaker

class CaretakerProfileActivity : BaseActivity() {
    private lateinit var backButton: Button
    private lateinit var editButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caretakerprofile)

        backButton = findViewById(R.id.backBtn)
        editButton = findViewById(R.id.editButton)

        backButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this, Homepage4caretaker::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        editButton.setOnClickListener {
            val medicalDiagnosticsActivityIntent =
                Intent(this, EditCaretakerProfileActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }
    }
}
