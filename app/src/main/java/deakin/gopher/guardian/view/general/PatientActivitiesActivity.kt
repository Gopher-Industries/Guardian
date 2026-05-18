package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.PatientActivity
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.launch

class PatientActivitiesActivity : AppCompatActivity() {

    private lateinit var logActivityButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingTextView: TextView
    private lateinit var emptyStateTextView: TextView
    private lateinit var adapter: PatientActivitiesAdapter

    private var selectedPatientId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_activities)

        logActivityButton = findViewById(R.id.logActivityButton)
        recyclerView = findViewById(R.id.activitiesRecyclerView)
        loadingTextView = findViewById(R.id.loadingTextView)
        emptyStateTextView = findViewById(R.id.emptyStateTextView)

        adapter = PatientActivitiesAdapter(emptyList()) { activity ->
            showDeleteConfirmation(activity.id)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        logActivityButton.setOnClickListener {
            startActivity(Intent(this, LogPatientActivityActivity::class.java))
        }

        loadFirstAssignedPatientAndFetchActivities()
    }

    override fun onResume() {
        super.onResume()
        if (selectedPatientId != null) {
            fetchPatientActivities()
        }
    }

    private fun loadFirstAssignedPatientAndFetchActivities() {
        showLoadingState()

        lifecycleScope.launch {
            try {
                val token = SessionManager.getToken()

                val response = ApiClient.apiService.getAssignedPatients(
                    token = "Bearer $token"
                )

                if (response.isSuccessful) {
                    val patients: List<Patient> = response.body().orEmpty()

                    if (patients.isEmpty()) {
                        showEmptyState("No assigned patients found")
                    } else {
                        selectedPatientId = patients.first().id
                        fetchPatientActivities()
                    }
                } else {
                    showErrorState("Failed to load assigned patients")
                }
            } catch (e: Exception) {
                showErrorState("Error loading patients: ${e.message}")
            }
        }
    }

    private fun fetchPatientActivities() {
        val patientId = selectedPatientId

        if (patientId.isNullOrEmpty()) {
            showEmptyState("Please select a patient")
            return
        }

        showLoadingState()

        lifecycleScope.launch {
            try {
                val token = SessionManager.getToken()

                val response = ApiClient.apiService.getPatientActivities(
                    token = "Bearer $token",
                    patientId = patientId
                )

                if (response.isSuccessful) {
                    val activities: List<PatientActivity> =
                        response.body() ?: emptyList()

                    if (activities.isEmpty()) {
                        adapter.updateActivities(emptyList())
                        showEmptyState("No activities found")
                    } else {
                        adapter.updateActivities(activities)
                        showContentState()
                    }
                } else {
                    showErrorState("Failed to fetch activities")
                }
            } catch (e: Exception) {
                showErrorState("Error: ${e.message}")
            }
        }
    }

    private fun showDeleteConfirmation(entryId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Activity")
            .setMessage("Are you sure you want to delete this activity entry?")
            .setPositiveButton("Delete") { _, _ ->
                deleteActivity(entryId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteActivity(entryId: String) {
        lifecycleScope.launch {
            try {
                val token = SessionManager.getToken()

                val response = ApiClient.apiService.deletePatientActivity(
                    token = "Bearer $token",
                    entryId = entryId
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@PatientActivitiesActivity,
                        "Activity deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    fetchPatientActivities()
                } else {
                    showErrorState("Failed to delete activity")
                }
            } catch (e: Exception) {
                showErrorState("Error deleting activity: ${e.message}")
            }
        }
    }

    private fun showLoadingState() {
        loadingTextView.visibility = android.view.View.VISIBLE
        emptyStateTextView.visibility = android.view.View.GONE
        recyclerView.visibility = android.view.View.GONE
    }

    private fun showContentState() {
        loadingTextView.visibility = android.view.View.GONE
        emptyStateTextView.visibility = android.view.View.GONE
        recyclerView.visibility = android.view.View.VISIBLE
    }

    private fun showEmptyState(message: String) {
        loadingTextView.visibility = android.view.View.GONE
        emptyStateTextView.visibility = android.view.View.VISIBLE
        emptyStateTextView.text = message
        recyclerView.visibility = android.view.View.GONE
    }

    private fun showErrorState(message: String) {
        loadingTextView.visibility = android.view.View.GONE
        emptyStateTextView.visibility = android.view.View.VISIBLE
        emptyStateTextView.text = message
        recyclerView.visibility = android.view.View.GONE

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}