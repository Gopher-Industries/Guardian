package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.database.FirebaseDatabase
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Priority
import deakin.gopher.guardian.model.Task

class TaskAddActivity : AppCompatActivity() {
    private lateinit var taskDescriptionEditText: EditText
    private lateinit var patientIdEditText: EditText
    private lateinit var taskSubDescEditText: EditText
    private lateinit var assignedNurseEditText: EditText
    private lateinit var priorityRadioGroup: RadioGroup
    private var taskPriority: Priority = Priority.MEDIUM
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)

        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText)
        taskSubDescEditText = findViewById(R.id.tasksubDescEditText)
        patientIdEditText = findViewById(R.id.taskPatientIdEditText)

        priorityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            taskPriority =
                when (checkedId) {
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
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorGreen))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorRed))
        }
        dialog.show()
    }

    private fun saveInFirebase() {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val caretakerTaskRef = databaseRef.child("caretaker_tasks")

        val patientId = patientIdEditText.text.toString().trim()
        val taskDescription = taskDescriptionEditText.text.toString().trim()
        val taskSubDesc = taskSubDescEditText.text.toString().trim()
        val assignedNurse = assignedNurseEditText.text.toString().trim()

        val newTask =
            Task(
                taskId = "",
                description = taskDescription,
                assignedNurse = assignedNurse,
                priority = taskPriority,
                patientId = patientId,
            )

        val taskId = caretakerTaskRef.push().key ?: ""
        newTask.taskId = taskId

        caretakerTaskRef.child(taskId).setValue(newTask).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updatePatientTasks(taskId)
                finish()
            } else {
                // Handle the error
            }
        }

        // Save under nurse tasks as well
        val nurseTaskRef = databaseRef.child("nurse_tasks")
        nurseTaskRef.child(taskId).setValue(newTask)
    }

    private fun updatePatientTasks(taskId: String) {
        val patientTasksRef = FirebaseDatabase.getInstance().reference.child("patient_tasks")
        patientTasksRef.child(taskId).setValue(true)
    }
}
