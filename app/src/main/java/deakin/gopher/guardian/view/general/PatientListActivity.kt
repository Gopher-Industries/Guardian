package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val MODE_DEFAULT = "mode_default"
        const val MODE_DOCTOR_PRESCRIPTION = "mode_doctor_prescription"
    }

    private lateinit var binding: ActivityPatientListBinding
    private lateinit var patientListAdapter: PatientListAdapter

    private val currentUser = SessionManager.getCurrentUser()

    private val screenMode: String by lazy {
        intent.getStringExtra(EXTRA_MODE) ?: MODE_DEFAULT
    }

    private val isDoctorPrescriptionMode: Boolean
        get() = screenMode == MODE_DOCTOR_PRESCRIPTION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.toolbar.title = if (isDoctorPrescriptionMode) {
            "Select Patient"
        } else {
            "Patients"
        }

        if (currentUser.role == Role.Nurse) {
            binding.toolbar.setBackgroundColor(getColor(R.color.TG_blue))
        }

        setupPatientList()
    }

    override fun onResume() {
        super.onResume()
        fetchPatients()
    }

    private fun setupPatientList() {
        patientListAdapter = PatientListAdapter(
            emptyList(), onPatientClick = { patient ->
            val intent = Intent(this, PatientDetailsActivity::class.java)
            intent.putExtra("patient", patient)

            intent.putExtra(
                PatientDetailsActivity.EXTRA_MODE, if (isDoctorPrescriptionMode) {
                    PatientDetailsActivity.MODE_DOCTOR_PRESCRIPTION
                } else {
                    PatientDetailsActivity.MODE_DEFAULT
                }
            )

            startActivity(intent)
        }, onAssignNurseClick = { patient ->
            if (currentUser.role == Role.Nurse) {
                Toast.makeText(
                    this, "Only caretaker can assign nurse to the patient", Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this, AssignNurseActivity::class.java)
                intent.putExtra(AssignNurseActivity.EXTRA_PATIENT_ID, patient.id)
                intent.putExtra(AssignNurseActivity.EXTRA_PATIENT_NAME, patient.fullname)
                startActivity(intent)
            }
        }, onEditClick = { patient ->
            if (currentUser.role == Role.Nurse) {
                Toast.makeText(
                    this, "Only caretaker can edit patient info", Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this, EditPatientActivity::class.java)
                intent.putExtra(EditPatientActivity.EXTRA_PATIENT, patient)
                startActivity(intent)
            }
        }, onDeleteClick = { patient ->
            confirmDeletePatient(patient)
        }, showMoreMenu = !isDoctorPrescriptionMode
        )

        binding.recyclerViewPatients.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPatients.adapter = patientListAdapter
    }

    private fun confirmDeletePatient(patient: Patient) {
        deletePatient(patient)
    }

    private fun deletePatient(patient: Patient) {
        val token = "Bearer ${SessionManager.getToken()}"

        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
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
            if (patientListAdapter.itemCount <= 0) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.show()
                }
            }

            try {
                if (isDoctorPrescriptionMode) {
                    val response = ApiClient.apiService.getAllPatientsForDoctor(token)

                    withContext(Dispatchers.Main) {
                        binding.progressBar.hide()

                        if (response.isSuccessful) {
                            val patients = response.body()?.patients.orEmpty()

                            if (patients.isNotEmpty()) {
                                patientListAdapter.updateData(patients)
                                binding.tvEmptyMessage.visibility = android.view.View.GONE
                            } else {
                                binding.tvEmptyMessage.visibility = android.view.View.VISIBLE
                            }
                        } else {
                            showMessage(getErrorMessage(response, "Failed to load patients"))
                        }
                    }
                } else {
                    val response = ApiClient.apiService.getAssignedPatients(token)

                    withContext(Dispatchers.Main) {
                        binding.progressBar.hide()

                        if (response.isSuccessful) {
                            val patients = response.body().orEmpty()

                            if (patients.isNotEmpty()) {
                                patientListAdapter.updateData(patients)
                                binding.tvEmptyMessage.visibility = android.view.View.GONE
                            } else {
                                binding.tvEmptyMessage.visibility = android.view.View.VISIBLE
                            }
                        } else {
                            showMessage(getErrorMessage(response, "Failed to load patients"))
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.hide()
                    showMessage("Failed to load patients: ${e.message}")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isDoctorPrescriptionMode) {
            return false
        }

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

    private fun getErrorMessage(
        response: retrofit2.Response<*>?, fallbackMessage: String
    ): String {
        val rawErrorBody = try {
            response?.errorBody()?.string()
        } catch (e: Exception) {
            null
        }

        val errorResponse = try {
            Gson().fromJson(rawErrorBody, ApiErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }

        return errorResponse?.apiError?.takeIf { it.isNotBlank() }
            ?: rawErrorBody?.takeIf { it.isNotBlank() } ?: response?.message()
                ?.takeIf { it.isNotBlank() } ?: fallbackMessage
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}