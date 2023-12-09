package deakin.gopher.guardian.view.caretaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.general.BaseActivity

class EditCaretakerProfileActivity : BaseActivity() {
    private lateinit var saveButton: Button
    val emojiCodePoint = 0x1F97A
    val emojiString = String(Character.toChars(emojiCodePoint))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_caretakerprofile)

        saveButton = findViewById(R.id.btnSave)

        saveButton.setOnClickListener {
            Toast.makeText(this, "Why Firebase not working? $emojiString", Toast.LENGTH_LONG).show()
            val medicalDiagnosticsActivityIntent =
                Intent(this, CaretakerProfileActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }
    }
}
