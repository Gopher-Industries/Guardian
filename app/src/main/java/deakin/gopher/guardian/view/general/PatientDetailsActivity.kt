package deakin.gopher.guardian.view.general

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.PatientActivityAdapter
import deakin.gopher.guardian.adapter.PrescriptionAdapter
import deakin.gopher.guardian.databinding.ActivityPatientDetailsBinding
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.Prescription
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PatientDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityPatientDetailsBinding
    private val currentUser = SessionManager.getCurrentUser()

    private lateinit var patient: Patient
    private lateinit var activitiesAdapter: PatientActivityAdapter
    private lateinit var prescriptionAdapter: PrescriptionAdapter

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val MODE_DEFAULT = "mode_default"
        const val MODE_DOCTOR_PRESCRIPTION = "mode_doctor_prescription"
    }

    private val screenMode: String by lazy {
        intent.getStringExtra(EXTRA_MODE) ?: MODE_DEFAULT
    }

    private val isDoctorPrescriptionMode: Boolean
        get() = screenMode == MODE_DOCTOR_PRESCRIPTION

    private val isCurrentUserDoctor: Boolean
        get() = currentUser.role == Role.Doctor

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (currentUser.role == Role.Nurse) {
            binding.toolbar.setBackgroundColor(getColor(R.color.TG_blue))
            binding.containerPatientInfo.setBackgroundColor(getColor(R.color.TG_blue))
        }

        @Suppress("DEPRECATION") patient = intent.getSerializableExtra("patient") as Patient

        binding.tvName.text = patient.fullname
        binding.tvAge.text = "Age: ${patient.age}"
        binding.tvDob.text = "Date of Birth: ${patient.dateOfBirth?.substringBefore("T")}"
        binding.tvGender.text = "Gender: ${
            patient.gender.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }"

        if (!patient.healthConditions.isNullOrEmpty()) {
            val formattedConditions = patient.healthConditions.joinToString(", ") { condition ->
                condition.split(" ").joinToString(" ") { word ->
                    word.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else it.toString()
                    }
                }
            }
            binding.tvHealthConditions.text = "Health Conditions: $formattedConditions"
        } else {
            binding.tvHealthConditions.text = "Health Conditions: No conditions listed"
        }

        Glide.with(this).load(patient.photoUrl).placeholder(R.drawable.profile).circleCrop()
            .into(binding.imagePatient)

        showAssignedNurses()

        setupPrescriptionSection()
        setupActivityLogSection()

        if (!isDoctorPrescriptionMode) {
            fetchPatientActivities(patient.id)
        }
    }

    override fun onResume() {
        super.onResume()

        if (::patient.isInitialized) {
            fetchPatientPrescriptions(patient.id)
        }
    }

    private fun setupPrescriptionSection() {
        binding.btnAddPrescription.visibility = if (isCurrentUserDoctor) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.btnAddPrescription.setOnClickListener {
            if (!isCurrentUserDoctor) {
                Toast.makeText(
                    this, "Only doctor can add prescription", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            openAddPrescriptionScreen()
        }

        prescriptionAdapter = PrescriptionAdapter(
            prescriptions = emptyList(),
            showEditButton = isCurrentUserDoctor,
            onEditClick = { prescription ->
                if (!isCurrentUserDoctor) {
                    Toast.makeText(
                        this, "Only doctor can edit prescription", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    openEditPrescriptionScreen(prescription)
                }
            })

        binding.recyclerViewPrescriptions.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPrescriptions.adapter = prescriptionAdapter
    }

    private fun showAssignedNurses() {
        val assignedNurses = patient.assignedNurses

        if (assignedNurses.isEmpty()) {
            binding.tvAssignedNursesNames.text = "No nurse assigned"
            return
        }

        val nurseNames = assignedNurses.joinToString(", ") { nurse ->
            nurse.name.ifBlank { nurse.email }
        }

        binding.tvAssignedNursesNames.text = nurseNames
    }

    private fun setupActivityLogSection() {
        activitiesAdapter = PatientActivityAdapter(emptyList())
        binding.recyclerViewActivities.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewActivities.adapter = activitiesAdapter
    }

    private fun openAddPrescriptionScreen() {
        val intent = Intent(this, AddEditPrescriptionActivity::class.java)
        intent.putExtra(AddEditPrescriptionActivity.EXTRA_PATIENT_ID, patient.id)
        intent.putExtra(AddEditPrescriptionActivity.EXTRA_PATIENT_NAME, patient.fullname)
        startActivity(intent)
    }

    private fun openEditPrescriptionScreen(prescription: Prescription) {
        val intent = Intent(this, AddEditPrescriptionActivity::class.java)
        intent.putExtra(AddEditPrescriptionActivity.EXTRA_PATIENT_ID, patient.id)
        intent.putExtra(AddEditPrescriptionActivity.EXTRA_PATIENT_NAME, patient.fullname)
        intent.putExtra(AddEditPrescriptionActivity.EXTRA_PRESCRIPTION, prescription)
        startActivity(intent)
    }

    private fun fetchPatientPrescriptions(patientId: String) {
        val token = "Bearer ${SessionManager.getToken()}"

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.progressBarPrescriptions.visibility = View.VISIBLE
            }

            val response = try {
                ApiClient.apiService.getPatientPrescriptions(
                    token = token, patientId = patientId, page = 1, limit = 20
                )
            } catch (e: Exception) {
                null
            }

            withContext(Dispatchers.Main) {
                binding.progressBarPrescriptions.visibility = View.GONE

                if (response?.isSuccessful == true) {
                    val prescriptions = response.body()?.prescriptions.orEmpty()
                    prescriptionAdapter.updateData(prescriptions)

                    binding.tvEmptyPrescriptions.visibility =
                        if (prescriptions.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    showMessage(getErrorMessage(response, "Failed to load prescriptions"))
                }
            }
        }
    }

    private fun fetchPatientActivities(patientId: String) {
        val token = "Bearer ${SessionManager.getToken()}"

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.VISIBLE
            }

            val response = try {
                ApiClient.apiService.getPatientActivities(
                    token, patientId
                )
            } catch (e: Exception) {
                null
            }

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE

                if (response?.isSuccessful == true) {
                    val activities = response.body()
                    if (!activities.isNullOrEmpty()) {
                        activitiesAdapter.updateData(activities)
                        binding.tvEmptyMessage.visibility = View.GONE
                    } else {
                        binding.tvEmptyMessage.visibility = View.VISIBLE
                    }
                } else {
                    showMessage(getErrorMessage(response, "Failed to load activities"))
                }
            }
        }
    }

    private fun getErrorMessage(response: retrofit2.Response<*>?, fallbackMessage: String): String {
        val errorBody = try {
            response?.errorBody()?.string()
        } catch (e: Exception) {
            null
        }

        val errorResponse = try {
            Gson().fromJson(errorBody, ApiErrorResponse::class.java)
        } catch (ex: Exception) {
            null
        }

        return errorResponse?.apiError?.takeIf { it.isNotBlank() }
            ?: errorBody?.takeIf { it.isNotBlank() } ?: response?.message()
                ?.takeIf { it.isNotBlank() } ?: fallbackMessage
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}