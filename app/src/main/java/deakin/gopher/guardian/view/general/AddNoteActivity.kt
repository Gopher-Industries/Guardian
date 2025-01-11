package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class AddNoteActivity : AppCompatActivity() {

    private lateinit var noteTitleInput: EditText
    private lateinit var noteDescriptionInput: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        // Initialize views
        noteTitleInput = findViewById(R.id.inputNoteTitle)
        noteDescriptionInput = findViewById(R.id.inputNoteDescription)
        submitButton = findViewById(R.id.buttonSubmitNote)

        // Set up Submit Button functionality
        submitButton.setOnClickListener {
            val noteTitle = noteTitleInput.text.toString().trim()
            val noteDescription = noteDescriptionInput.text.toString().trim()

            // Validate fields
            if (noteTitle.isEmpty() || noteDescription.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Submit the note (replace with API or database integration if needed)
                Toast.makeText(
                    this,
                    "Note Submitted:\nTitle: $noteTitle\nDescription: $noteDescription",
                    Toast.LENGTH_LONG
                ).show()

                // Clear fields after submission
                noteTitleInput.text.clear()
                noteDescriptionInput.text.clear()
            }
        }
    }
}

