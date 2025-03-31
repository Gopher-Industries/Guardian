package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.Priority
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.services.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        // Initialize views
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText)
        taskSubDescEditText = findViewById(R.id.tasksubDescEditText)
        patientIdEditText = findViewById(R.id.taskPatientIdEditText)
        assignedNurseEditText = findViewById(R.id.assignedNurseEditText)
        priorityRadioGroup = findViewById(R.id.priorityRadioGroup)

        // Handle editing existing task
        taskId = intent.getStringExtra("taskId")
        if (taskId != null) {
            loadTaskForEditing(taskId!!)
        }

        // Handle priority changes
        priorityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            taskPriority =
                when (checkedId) {
                    R.id.radio_high -> Priority.HIGH
                    R.id.radio_low -> Priority.LOW
                    else -> Priority.MEDIUM
                }
        }

        // Save button click listener
        findViewById<Button>(R.id.newTaskSubmitButton).setOnClickListener {
            showSaveDialog()
        }
    }

    private fun loadTaskForEditing(taskId: String) {
        ApiClient.apiService.getTaskById(taskId).enqueue(
            object : Callback<BaseModel<Task>> {
                override fun onResponse(
                    call: Call<BaseModel<Task>>,
                    response: Response<BaseModel<Task>>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.data?.let { task ->
                            taskDescriptionEditText.setText(task.description)
                            patientIdEditText.setText(task.patientId)
                            taskSubDescEditText.setText(task.taskSubDesc)
                            assignedNurseEditText.setText(task.assignedNurse)
                            taskPriority = task.priority
                        }
                    } else {
                        Toast.makeText(this@TaskAddActivity, "Failed to load task", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(
                    call: Call<BaseModel<Task>>,
                    t: Throwable,
                ) {
                    Toast.makeText(this@TaskAddActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            },
        )
    }

    private fun showSaveDialog() {
        AlertDialog.Builder(this)
            .setTitle("Save Task")
            .setPositiveButton("Yes") { _, _ -> saveTask() }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    private fun saveTask() {
        val task =
            Task(
                taskId = taskId ?: "",
                description = taskDescriptionEditText.text.toString().trim(),
                assignedNurse = assignedNurseEditText.text.toString().trim(),
                priority = taskPriority,
                patientId = patientIdEditText.text.toString().trim(),
                taskSubDesc = taskSubDescEditText.text.toString().trim(),
            )

        val apiCall: Call<BaseModel<Task>> =
            if (taskId != null) {
                ApiClient.apiService.updateTask(taskId!!, task)
            } else {
                ApiClient.apiService.createTask(task)
            }

        apiCall.enqueue(
            object : Callback<BaseModel<Task>> {
                override fun onResponse(
                    call: Call<BaseModel<Task>>,
                    response: Response<BaseModel<Task>>,
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@TaskAddActivity, "Task saved successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@TaskAddActivity, TasksListActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@TaskAddActivity, "Failed to save task", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(
                    call: Call<BaseModel<Task>>,
                    t: Throwable,
                ) {
                    Toast.makeText(this@TaskAddActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            },
        )
    }
}
