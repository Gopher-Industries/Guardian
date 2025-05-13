package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Priority
import deakin.gopher.guardian.model.Task
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*

class TaskAddActivity : AppCompatActivity() {
    private lateinit var taskDescriptionEditText: TextInputEditText
    private lateinit var patientIdEditText: TextInputEditText
    private lateinit var assignedNurseEditText: TextInputEditText
    private lateinit var dueDateEditText: TextInputEditText
    private lateinit var taskTitleEditText: TextInputEditText
    private lateinit var priorityRadioGroup: RadioGroup
    private var taskPriority: Priority = Priority.MEDIUM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)

        // Initialize input fields
        taskTitleEditText = findViewById(R.id.taskTitleEditText)
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText)
        patientIdEditText = findViewById(R.id.taskPatientIdEditText)
        assignedNurseEditText = findViewById(R.id.taskAssignedToEditText)
        dueDateEditText = findViewById(R.id.taskDueDateEditText)
        priorityRadioGroup = findViewById(R.id.priorityRadioGroup) // Ensure this exists in XML

        // Set up the DatePicker when the field is clicked
        dueDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog.newInstance(
                { _, year, monthOfYear, dayOfMonth ->
                    val date = "$year-${monthOfYear + 1}-$dayOfMonth"
                    dueDateEditText.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            // Customize the date picker dialog
            datePickerDialog.setThemeDark(true) // Optional dark theme
            datePickerDialog.show(supportFragmentManager, "DatePicker")
        }

        priorityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            taskPriority = when (checkedId) {
                R.id.priorityHigh -> Priority.HIGH
                R.id.priorityLow -> Priority.LOW
                else -> Priority.MEDIUM
            }
        }

        val submitButton: Button = findViewById(R.id.newTaskSubmitButton)
        submitButton.setOnClickListener {
            showSaveDialog()
        }

        val customHeader: CustomHeader = findViewById(R.id.taskCustomHeader)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        customHeader.setHeaderHeight(450)
        customHeader.setHeaderText(getString(R.string.add_task))
        customHeader.setHeaderTopImageVisibility(View.VISIBLE)
        customHeader.setHeaderTopImage(R.drawable.add_image_button)

        customHeader.menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.saving_changes))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ -> saveInFirebase() }
        builder.setNegativeButton(getString(R.string.no), null)
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorGreen, theme))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorRed, theme))
        }
        dialog.show()
    }

    private fun saveInFirebase() {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val caretakerTaskRef = databaseRef.child("caretaker_tasks")

        val taskTitle = taskTitleEditText.text.toString().trim()
        val taskDescription = taskDescriptionEditText.text.toString().trim()
        val patientId = patientIdEditText.text.toString().trim()
        val assignedNurse = assignedNurseEditText.text.toString().trim()

        // Optional: Input validation
        if (taskTitle.isEmpty() || taskDescription.isEmpty() || patientId.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val newTask = Task(
            taskId = "",
            title = taskTitle,
            description = taskDescription,
            assignedNurse = assignedNurse,
            priority = taskPriority,
            patientId = patientId,
            dueDate = dueDateEditText.text.toString().trim(),
            completed = false
        )

        val taskId = caretakerTaskRef.push().key ?: return
        newTask.taskId = taskId

        caretakerTaskRef.child(taskId).setValue(newTask).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updatePatientTasks(taskId)
                finish()
            } else {
                Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show()
            }
        }

        databaseRef.child("nurse_tasks").child(taskId).setValue(newTask)
    }

    private fun updatePatientTasks(taskId: String) {
        FirebaseDatabase.getInstance().reference
            .child("patient_tasks")
            .child(taskId)
            .setValue(true)
    }
}
