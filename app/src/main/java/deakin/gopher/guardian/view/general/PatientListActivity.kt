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
    private val canReassignPatients = currentUser.role == Role.Admin
    private val canAssignNurse = currentUser.role != Role.Nurse && !canReassignPatients
    private val canEditPatients = currentUser.role != Role.Nurse
    private val canDeletePatients = currentUser.role == Role.Admin

    private val patientListAdapter =
        PatientListAdapter(
            emptyList(),
            showReassignAction = canReassignPatients,
            showAssignNurseAction = canAssignNurse,
            showEditAction = canEditPatients,
            showDeleteAction = canDeletePatients,
            onPatientClick = { patient ->
                val intent = Intent(this, PatientDetailsActivity::class.java)
                intent.putExtra("patient", patient)
                startActivity(intent)
            },
            onReassignClick =
                if (canReassignPatients) {
                    { patient ->
                        openReassignmentDialog(patient)
                    }
                } else {
                    null
                },
            onAssignNurseClick =
                if (canAssignNurse) {
                    { patient ->
                        val intent = Intent(this, AssignNurseActivity::class.java)
                        intent.putExtra(AssignNurseActivity.EXTRA_PATIENT_ID, patient.id)
                        intent.putExtra(AssignNurseActivity.EXTRA_PATIENT_NAME, patient.fullname)
                        startActivity(intent)
                    }
                } else {
                    null
                },
            onEditClick =
                if (canEditPatients) {
                    { patient ->
                        val intent = Intent(this, EditPatientActivity::class.java)
                        intent.putExtra(EditPatientActivity.EXTRA_PATIENT, patient)
                        startActivity(intent)
                    }
                } else {
                    null
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

    private fun openReassignmentDialog(patient: Patient) {
        PatientReassignmentDialog(
            activity = this,
            patient = patient,
            onReassigned = {
                fetchPatients()
            },
        ).show()
    }

    private fun confirmDeletePatient(patient: Patient) {
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
                    fetchPatients()
                } else {
                    showMessage("Failed to delete patient")
                }
            }
        }
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

            val adminResponse =
                if (canReassignPatients) {
                    try {
                        ApiClient.apiService.getAdminPatients(token)
                    } catch (exception: Exception) {
                        null
                    }
                } else {
                    null
                }
            val assignedResponse =
                if (adminResponse?.isSuccessful == true) {
                    null
                } else {
                    try {
                        ApiClient.apiService.getAssignedPatients(token)
                    } catch (exception: Exception) {
                        null
                    }
                }

            withContext(Dispatchers.Main) {
                binding.progressBar.hide()

                val patients = adminResponse?.body()?.patients ?: assignedResponse?.body()

                if (!patients.isNullOrEmpty()) {
                    patientListAdapter.updateData(patients)
                    binding.recyclerViewPatients.visibility = View.VISIBLE
                    binding.tvEmptyMessage.visibility = View.GONE
                } else {
                    patientListAdapter.updateData(emptyList())
                    binding.recyclerViewPatients.visibility = View.GONE
                    binding.tvEmptyMessage.visibility = View.VISIBLE
                    binding.tvEmptyMessage.text =
                        if (patients != null) "No patients found" else "Unable to load patients"

                    if (patients == null) {
                        val errorResponse =
                            readError(
                                adminResponse?.errorBody()?.string()
                                    ?: assignedResponse?.errorBody()?.string(),
                            )
                        showMessage(errorResponse ?: "Failed to load patients")
                    }
                }
            }
        }
    }

    private fun readError(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) {
            return null
        }
        return try {
            Gson().fromJson(errorBody, ApiErrorResponse::class.java)?.apiError
        } catch (exception: Exception) {
            null
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
