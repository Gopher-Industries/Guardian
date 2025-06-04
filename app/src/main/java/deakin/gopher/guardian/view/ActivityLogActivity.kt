package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class ActivityLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val activitySpinner: Spinner = findViewById(R.id.activitySpinner)
        val customActivity: EditText = findViewById(R.id.customActivity)
        val comment: EditText = findViewById(R.id.comment)
        val submitBtn: Button = findViewById(R.id.submitBtn)

        val activities = arrayOf("Vitals Check", "Medication", "Mobility Assistance", "Meal Support")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, activities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activitySpinner.adapter = adapter

        submitBtn.setOnClickListener {
            val selected = activitySpinner.selectedItem.toString()
            val custom = customActivity.text.toString().trim()
            val comments = comment.text.toString().trim()

            val logMessage = StringBuilder()
            logMessage.append("Activity: ").append(if (custom.isNotEmpty()) custom else selected)
            if (comments.isNotEmpty()) logMessage.append("\nComments: ").append(comments)

            Toast.makeText(this, logMessage.toString(), Toast.LENGTH_LONG).show()
        }
    }
}
