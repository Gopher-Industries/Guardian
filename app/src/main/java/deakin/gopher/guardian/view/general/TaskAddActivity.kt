package deakin.gopher.guardian.view.general

import android.content.ClipDescription
import android.content.DialogInterface
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
import deakin.gopher.guardian.model.GP
import deakin.gopher.guardian.model.NextOfKin
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.Priority
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.util.DataListener

class TaskAddActivity : AppCompatActivity() {
    private lateinit var taskDescriptionEditText: EditText
    private lateinit var patientIdEditText: EditText
    private lateinit var taskSubDesc : EditText
    private var patient: Patient? = null
    private lateinit var assignedNurseEditText: EditText
    private lateinit var priorityRadioGroup: RadioGroup
    private lateinit var taskPriority: Priority
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)

        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText)
        taskSubDesc = findViewById(R.id.tasksubDescEditText)
        patientIdEditText = findViewById(R.id.taskPatientIdEditText)
        assignedNurseEditText = findViewById(R.id.taskAssignedNurseEditText)
        priorityRadioGroup = findViewById(R.id.taskPriorityRadioGroup)
        priorityRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            taskPriority = when (checkedId) {
                R.id.taskPriorityRadioButtonLow -> Priority.LOW
                R.id.taskPriorityRadioButtonMedium -> Priority.MEDIUM
                R.id.taskPriorityRadioButtonHigh -> Priority.HIGH
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
            drawerLayout?.openDrawer(
                GravityCompat.START,
            )
        }
    }

    fun onTaskDataFilled(
        task: Task,
    ) {
        if (null != task) {
            this.task = task
        }
    }

    fun onTaskDataFinished(isFinished: Boolean?) {
        showSaveDialog()
    }

    fun showSaveDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.saving_changes))
        builder.setPositiveButton(
            getString(R.string.yes),
        ) { _: DialogInterface?, _: Int -> saveInFirebase() }
        builder.setNegativeButton(getString(R.string.no), null)
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog
                .getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.colorGreen))
            dialog
                .getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.colorRed))
        }
        dialog.show()
    }

    private fun saveInFirebase() {

        val databaseRef = FirebaseDatabase.getInstance().reference
        val caretakerTaskRef = databaseRef.child("caretaker_tasks")

        val patientId = patientIdEditText.text.toString().trim()
        val taskDescription = taskDescriptionEditText.text.toString().trim()
        val taskSubDesc = taskSubDesc.text.toString().trim()

        val newTask = Task(
            taskId = "",
            description = taskDescription,
            subDescription = taskSubDesc,
            patientId = patientId
        )

        val taskId = caretakerTaskRef.push().key ?: ""
        newTask.taskId = taskId

        //saving under care taker tasks
        caretakerTaskRef.child(taskId).setValue(newTask).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updatePatientTasks(patientId, taskId)
                finish()
            } else {
                // Handle the error
            }
        }

        //This for nurse tasks // TODO : need to operate for both caretaker and nurse
//        val databaseRef = FirebaseDatabase.getInstance().reference
//        val caretakerTaskRef = databaseRef.child("caretaker_tasks")
//        val patientRef = databaseRef.child("patient_profile")
//        val taskRef = databaseRef.child("tasks")
//
//        val patientId = patientIdEditText.text.toString().trim()
//
//        val newTask =
//            Task(
//                "",
//                taskDescriptionEditText.text.toString().trim(),
//                patient?.patientId ?: "",
//            )
//
//        val taskId = taskRef.push().key ?: ""
//        newTask.taskId = taskId
//
//        patient?.let { caretakerTaskRef.child(taskId!!).setValue(newTask) }
//
//        updatePatientTasks(patientId, taskId)
//
//        finish()
        val taskRef = databaseRef.child("nurse-tasks")

        val taskId = taskRef.push().key ?: ""

        val newTask =
            Task(
                taskId,
                taskDescriptionEditText.text.toString().trim(),
                assignedNurseEditText.text.toString().trim(),
                taskPriority,
            )

        taskRef.child(taskId).setValue(newTask)

        finish()
    }

    private fun updatePatientTasks(
        taskId: String,
    ) {
        val patientTasksRef = FirebaseDatabase.getInstance().reference.child("nurse-tasks")
        patientTasksRef.child(taskId).setValue(true)
    }
}
