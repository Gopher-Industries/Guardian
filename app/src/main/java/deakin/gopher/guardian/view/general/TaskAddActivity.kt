package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.textfield.TextInputEditText
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Priority
import deakin.gopher.guardian.model.Task
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat // for colors
import java.util.*

class TaskAddActivity : AppCompatActivity() {

    private lateinit var taskDescriptionEditText: TextInputEditText
    private lateinit var patientIdEditText: TextInputEditText
    private lateinit var assignedNurseEditText: TextInputEditText
    private lateinit var dueDateEditText: TextInputEditText
    private lateinit var taskTitleEditText: TextInputEditText
    private lateinit var priorityRadioGroup: RadioGroup
    private var taskPriority: Priority = Priority.MEDIUM

    private val taskApiService = ApiClient.retrofit.create(TaskApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)

        // Initialize input fields
        taskTitleEditText = findViewById(R.id.taskTitleEditText)
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText)
        patientIdEditText = findViewById(R.id.taskPatientIdEditText)
        assignedNurseEditText = findViewById(R.id.taskAssignedToEditText)
        dueDateEditText = findViewById(R.id.taskDueDateEditText)
        priorityRadioGroup = findViewById(R.id.priorityRadioGroup)

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
            datePickerDialog.setThemeDark(true)
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
        builder.setPositiveButton(getString(R.string.yes)) { _, _ -> saveTaskViaApi() }
        builder.setNegativeButton(getString(R.string.no), null)
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorGreen, theme))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorRed, theme))
        }
        dialog.show()
    }

    private fun saveTaskViaApi() {
        val taskTitle = taskTitleEditText.text.toString().trim()
        val taskDescription = taskDescriptionEditText.text.toString().trim()
        val patientId = patientIdEditText.text.toString().trim()
        val assignedNurse = assignedNurseEditText.text.toString().trim()
        val dueDate = dueDateEditText.text.toString().trim()

        if (taskTitle.isEmpty() || taskDescription.isEmpty() || patientId.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val newTask = Task(
            taskId = null, // DO NOT include it, MongoDB will generate it
            title = taskTitle,
            description = taskDescription,
            assignedNurse = assignedNurse,
            priority = taskPriority,
            patientId = patientId,
            dueDate = dueDate,
            completed = false
        )


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = taskApiService.createTask(newTask)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val drawerLayout = findViewById<View>(R.id.drawer_layout)

                        Snackbar.make(drawerLayout, "Task saved successfully", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(ContextCompat.getColor(this@TaskAddActivity, R.color.colorGreen))
                            .setTextColor(ContextCompat.getColor(this@TaskAddActivity, android.R.color.white))
                            .addCallback(object : Snackbar.Callback() {
                                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                    super.onDismissed(transientBottomBar, event)
                                    finish()
                                }
                            })
                            .show()

                    } else {
                        Snackbar.make(findViewById<View>(R.id.drawer_layout), "Failed to save task: ${response.code()}", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(ContextCompat.getColor(this@TaskAddActivity, R.color.colorError))  // define colorError or use red
                            .setTextColor(ContextCompat.getColor(this@TaskAddActivity, android.R.color.white))
                            .show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val drawerLayout = findViewById<View>(R.id.drawer_layout)
                    Snackbar.make(drawerLayout, "Error: ${e.localizedMessage}", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(ContextCompat.getColor(this@TaskAddActivity, R.color.colorError))
                        .setTextColor(ContextCompat.getColor(this@TaskAddActivity, android.R.color.white))
                        .show()
                }
            }
        }

    }
}
