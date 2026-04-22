package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    private val patientListAdapter =
        PatientListAdapter(
            emptyList(),
            onPatientClick = { patient ->
                val intent = Intent(this, PatientDetailsActivity::class.java)
                intent.putExtra("patient", patient)
                startActivity(intent)
            },
            onAssignNurseClick = { patient ->
//            val intent = Intent(this, AssignNurseActivity::class.java)
//            intent.putExtra("patientId", patient.id)
//            startActivity(intent)
            },
            onDeleteClick = { patient ->
                confirmDeletePatient(patient)
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

    private val currentUser = SessionManager.getCurrentUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
            withContext(Dispatchers.Main) {
                if (patientListAdapter.itemCount <= 0) {
                    binding.progressBar.show()
                }
                binding.tvEmptyMessage.visibility = View.GONE
                binding.recyclerViewPatients.visibility = View.VISIBLE
            }

            try {
                val response = ApiClient.apiService.getAssignedPatients(token)

                withContext(Dispatchers.Main) {
                    binding.progressBar.hide()

                    if (response.isSuccessful) {
                        val patients = response.body()

                        if (!patients.isNullOrEmpty()) {
                            patientListAdapter.updateData(patients)
                            binding.recyclerViewPatients.visibility = View.VISIBLE
                            binding.tvEmptyMessage.visibility = View.GONE
                        } else {
                            patientListAdapter.updateData(emptyList())
                            binding.recyclerViewPatients.visibility = View.GONE
                            binding.tvEmptyMessage.visibility = View.VISIBLE
                            binding.tvEmptyMessage.text = "No patients found"
                        }
                    } else {
                        patientListAdapter.updateData(emptyList())
                        binding.recyclerViewPatients.visibility = View.GONE
                        binding.tvEmptyMessage.visibility = View.VISIBLE
                        binding.tvEmptyMessage.text = "Unable to load patients"

                        val errorBody = response.errorBody()?.string()
                        val errorResponse =
                            if (!errorBody.isNullOrBlank()) {
                                try {
                                    Gson().fromJson(errorBody, ApiErrorResponse::class.java)
                                } catch (e: Exception) {
                                    null
                                }
                            } else {
                                null
                            }

                        showMessage(errorResponse?.apiError ?: response.message())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.hide()
                    patientListAdapter.updateData(emptyList())
                    binding.recyclerViewPatients.visibility = View.GONE
                    binding.tvEmptyMessage.visibility = View.VISIBLE
                    binding.tvEmptyMessage.text = "Network error occurred"
                    showMessage("Network error occurred")
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (currentUser.organization != null) {
            return false
        }
        menuInflater.inflate(R.menu.menu_patient_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add_patient) {
            startActivity(Intent(this, AddNewPatientActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
