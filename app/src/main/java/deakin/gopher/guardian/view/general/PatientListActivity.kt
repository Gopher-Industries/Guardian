package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.PatientListAdapter
import deakin.gopher.guardian.databinding.ActivityPatientListBinding
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.view.hide
import deakin.gopher.guardian.view.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientListActivity : BaseActivity() {
    private lateinit var binding: ActivityPatientListBinding
    private val currentUser = SessionManager.getCurrentUser()
    private val canAddPatients = currentUser.role == Role.Admin
    private val canDeletePatients = currentUser.role == Role.Admin
    private val patientListAdapter =
        PatientListAdapter(
            emptyList(),
            showDeleteAction = canDeletePatients,
            onPatientClick = { patient ->
                val intent = Intent(this, PatientDetailsActivity::class.java)
                intent.putExtra("patient", patient)
                startActivity(intent)
            },
            onDeleteClick =
                if (canDeletePatients) {
                    { patient ->
                        confirmDeletePatient(patient)
                    }
                } else {
                    null
                },
        )

    private fun confirmDeletePatient(patient: Patient) {
        // Optional: show a confirmation dialog before deleting
        deletePatient(patient)
    }

    private fun deletePatient(patient: Patient) {
        val token = "Bearer ${SessionManager.getToken()}"
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                try {
                    ApiClient.apiService.deletePatient(token, patient.id)
                } catch (e: Exception) {
                    null
                }
            withContext(Dispatchers.Main) {
                if (response?.isSuccessful == true) {
                    showMessage("Patient deleted")
                    fetchPatients() // Refresh the patient list
                } else {
                    showMessage("Failed to delete patient")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.menu.findItem(R.id.action_add_patient).isVisible = canAddPatients
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_add_patient && canAddPatients) {
                startActivity(Intent(this, AddNewPatientActivity::class.java))
                true
            } else {
                false
            }
        }

        if (currentUser.role == Role.Nurse) {
            binding.toolbar.setBackgroundColor(getColor(R.color.TG_blue))
        }

        binding.recyclerViewPatients.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPatients.adapter = patientListAdapter
    }

    override fun onResume() {
        super.onResume()
        fetchPatients()
    }

    private fun fetchPatients() {
        val token = "Bearer ${SessionManager.getToken()}"
        CoroutineScope(Dispatchers.IO).launch {
            if (patientListAdapter.itemCount <= 0) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.show()
                }
            }
            val response = ApiClient.apiService.getAssignedPatients(token)
            withContext(Dispatchers.Main) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.hide()
                }
                if (response.isSuccessful) {
                    if (!response.body().isNullOrEmpty()) {
                        patientListAdapter.updateData(response.body()!!)
                        withContext(Dispatchers.Main) {
                            binding.tvEmptyMessage.visibility = View.GONE
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            binding.tvEmptyMessage.visibility = View.VISIBLE
                        }
                    }
                } else {
                    // Handle error
                    val errorResponse =
                        Gson().fromJson(
                            response.errorBody()?.string(),
                            ApiErrorResponse::class.java,
                        )
                    showMessage(errorResponse.apiError ?: response.message())
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
