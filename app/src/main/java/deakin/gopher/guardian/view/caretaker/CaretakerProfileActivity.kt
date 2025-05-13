package deakin.gopher.guardian.view.caretaker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import deakin.gopher.guardian.R

class CaretakerProfileActivity : AppCompatActivity() {

    private lateinit var caretakerImage: ImageView
    private lateinit var photoIcon: ImageView
    private lateinit var editButton: FloatingActionButton

    private lateinit var addressField: TextInputEditText
    private lateinit var dobField: TextInputEditText
    private lateinit var phoneField: TextInputEditText
    private lateinit var wardField: TextInputEditText
    private lateinit var medicareField: TextInputEditText

    private var isEditable = false
    private val PICK_IMAGE_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caretakerprofile)

        // Image components
        caretakerImage = findViewById(R.id.caretakerImage)
        photoIcon = findViewById(R.id.photoIcon)

        // Edit button
        editButton = findViewById(R.id.editButton)

        // Form fields
        addressField = findViewById(R.id.addressField)
        dobField = findViewById(R.id.dobField)
        phoneField = findViewById(R.id.phoneField)
        wardField = findViewById(R.id.wardField)
        medicareField = findViewById(R.id.medicareField)

        // Handle photo change
        photoIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Toggle edit mode
        editButton.setOnClickListener {
            toggleEditableFields()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            caretakerImage.setImageURI(imageUri)
        }
    }

    private fun toggleEditableFields() {
        isEditable = !isEditable

        val fields = listOf(addressField, dobField, phoneField, wardField, medicareField)
        fields.forEach { it.isEnabled = isEditable }

        if (isEditable) {
            editButton.setImageResource(android.R.drawable.ic_menu_save)
            Toast.makeText(this, "You can now edit the fields", Toast.LENGTH_SHORT).show()
        } else {
            editButton.setImageResource(android.R.drawable.ic_menu_edit)
            Toast.makeText(this, "Changes saved (placeholder)", Toast.LENGTH_SHORT).show()

            // TODO: Save to Firebase or local storage here
            val address = addressField.text.toString()
            val dob = dobField.text.toString()
            val phone = phoneField.text.toString()
            val ward = wardField.text.toString()
            val medicare = medicareField.text.toString()

            // You can add validation and Firebase logic here
        }
    }
}
