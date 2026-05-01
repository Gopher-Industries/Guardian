package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Priority
import deakin.gopher.guardian.model.Task
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.NavigationService

class TaskAddActivity : AppCompatActivity() {
    private lateinit var taskDescriptionEditText: EditText
    private lateinit var patientIdEditText: EditText
    private lateinit var taskSubDescEditText: EditText
    private var taskPriority: Priority = Priority.MEDIUM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)

        if (SessionManager.getCurrentUser().role != Role.Caretaker) {
            Toast.makeText(this, "Only caretakers can add tasks", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText)
        taskSubDescEditText = findViewById(R.id.tasksubDescEditText)
        patientIdEditText = findViewById(R.id.taskPatientIdEditText)

        val submitButton: Button = findViewById(R.id.newTaskSubmitButton)
        submitButton.setOnClickListener {
            showSaveDialog()
        }

        val customHeader: CustomHeader = findViewById(R.id.taskCustomHeader)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val navigationService = NavigationService(this)

        customHeader.setHeaderHeight(450)
        customHeader.setHeaderText(getString(R.string.add_task))
        customHeader.setHeaderTopImageVisibility(View.VISIBLE)
        customHeader.setHeaderTopImage(R.drawable.add_image_button)
        navigationView.setItemIconTintList(null)
        navigationView.menu.findItem(R.id.add_task).isVisible = false

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navigationService.toHomeScreenForRole(SessionManager.getCurrentUser().role)
                R.id.nav_signout -> {
                    navigationService.onSignOut()
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

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
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorGreen))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.colorRed))
        }
        dialog.show()
    }

    private fun saveInFirebase() {
        val databaseRef = FirebaseDatabase.getInstance().reference

        val patientId = patientIdEditText.text.toString().trim()
        val taskDescription = taskDescriptionEditText.text.toString().trim()
        if (taskDescription.isEmpty() || patientId.isEmpty()) {
            Toast.makeText(this, "Enter a task description and patient ID", Toast.LENGTH_SHORT).show()
            return
        }

        val newTask =
            Task(
                taskId = "",
                description = taskDescription,
                assignedNurse = "",
                priority = taskPriority,
                patientId = patientId,
            )

        val taskId = databaseRef.child("caretaker_tasks").push().key ?: ""
        newTask.taskId = taskId

        val updates =
            hashMapOf<String, Any?>(
                "caretaker_tasks/$taskId" to newTask,
                "nurse_tasks/$taskId" to newTask,
                "patient_tasks/$taskId" to true,
            )

        databaseRef.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                finish()
            } else {
                Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
