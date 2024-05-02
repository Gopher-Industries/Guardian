package deakin.gopher.guardian.view.general

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.database.FirebaseDatabase
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.GP
import deakin.gopher.guardian.model.NextOfKin
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.util.DataListener

class TaskAddActivity : AppCompatActivity(), DataListener {
    private lateinit var taskDescriptionEditText: EditText
    private lateinit var patientIdEditText: EditText
    private var patient: Patient? = null
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)

        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText)
        patientIdEditText = findViewById(R.id.taskPatientIdEditText)
        val submitButton: Button = findViewById(R.id.newTaskSubmitButton)
        submitButton.setOnClickListener {
          //  showSaveDialog()
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

    override fun onDataFilled(
        patient: Patient?,
        nextOfKin1: NextOfKin?,
        nextOfKin2: NextOfKin?,
        gp1: GP?,
        gp2: GP?,
    ) { }

    override fun onDataFinished(isFinished: Boolean?) {
    }

    fun onTaskDataFilled(
        patient: Patient,
        task: Task,
    ) {
        if (null != patient) {
            this.patient = patient
        }
        if (null != task) {
            this.task = task
        }
    }

    fun onDataFinihsed(isFinished: Boolean?) { }

    fun onTaskDataFinished(isFinished: Boolean?) {
       // showSaveDialog()
    }




    private fun updatePatientTasks(
        patientId: String,
        taskId: String,
    ) {
        val patientTasksRef = FirebaseDatabase.getInstance().getReference("patients").child(patientId).child("tasks")
        patientTasksRef.child(taskId).setValue(true)
    }
}
