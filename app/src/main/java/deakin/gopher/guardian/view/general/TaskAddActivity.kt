package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
    private var taskId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)

        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText)
        taskSubDescEditText = findViewById(R.id.tasksubDescEditText)
        patientIdEditText = findViewById(R.id.taskPatientIdEditText)

        taskId = intent.getStringExtra("taskId")
        if (taskId != null) {
            loadTaskForEditing(taskId!!)
        }

        priorityRadioGroup = findViewById(R.id.priorityRadioGroup)
        priorityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            taskPriority = when (checkedId) {
                R.id.radio_high -> Priority.HIGH
                R.id.radio_low -> Priority.LOW
                else -> Priority.MEDIUM
            }
        }

        val submitButton: Button = findViewById(R.id.newTaskSubmitButton)
        submitButton.setOnClickListener {
            showSaveDialog()
        }
    }

    private fun loadTaskForEditing(taskId: String) {
        val taskRef = FirebaseDatabase.getInstance().reference.child("caretaker_tasks").child(taskId)
        taskRef.get().addOnSuccessListener { snapshot ->
            val task = snapshot.getValue(Task::class.java)
            if (task != null) {
                taskDescriptionEditText.setText(task.description)
                patientIdEditText.setText(task.patientId)
                taskSubDescEditText.setText(task.taskSubDesc)
                assignedNurseEditText.setText(task.assignedNurse)
                taskPriority = task.priority
            }
        }
    }

    private fun showSaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Save Changes")
        builder.setPositiveButton("Yes") { _, _ -> saveInFirebase() }
        builder.setNegativeButton("No", null)
        builder.create().show()
    }

    private fun saveInFirebase() {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val caretakerTaskRef = databaseRef.child("caretaker_tasks")

        val patientId = patientIdEditText.text.toString().trim()
        val taskDescription = taskDescriptionEditText.text.toString().trim()
        val taskSubDesc = taskSubDescEditText.text.toString().trim()
        val assignedNurse = assignedNurseEditText.text.toString().trim()

        val task = Task(
            taskId = taskId ?: caretakerTaskRef.push().key!!,
            description = taskDescription,
            assignedNurse = assignedNurse,
            priority = taskPriority,
            patientId = patientId,
            taskSubDesc = taskSubDesc
        )

        caretakerTaskRef.child(task.taskId).setValue(task).addOnCompleteListener {
            finish()
        }
    }
}
