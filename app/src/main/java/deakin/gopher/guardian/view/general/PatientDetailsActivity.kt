package deakin.gopher.guardian.view.general

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.PatientActivityAdapter
import deakin.gopher.guardian.databinding.ActivityPatientDetailsBinding
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.Patient
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
    private lateinit var currentUser: deakin.gopher.guardian.model.register.User
    private lateinit var activitiesAdapter: PatientActivityAdapter
    private lateinit var currentPatient: Patient
    private val nursesFragment = PatientAssignedNursesFragment()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        currentUser =
            try {
                SessionManager.getCurrentUser()
            } catch (exception: Exception) {
                showMessage("Session expired. Please log in again.")
                finish()
                return
            }

        if (currentUser.role == Role.Nurse) {
            binding.toolbar.setBackgroundColor(getColor(R.color.TG_blue))
            binding.containerPatientInfo.setBackgroundColor(getColor(R.color.TG_blue))
        }

        val patient = intent.getSerializableExtra("patient") as? Patient
        if (patient == null) {
            showMessage("Patient details are unavailable.")
            finish()
            return
        }

        currentPatient = patient
        bindPatient(currentPatient)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentAssignedNursesContainer, nursesFragment)
            .commit()

        binding.buttonReassignStaff.visibility =
            if (currentUser.role == Role.Admin) View.VISIBLE else View.GONE
        binding.buttonReassignStaff.setOnClickListener {
            PatientReassignmentDialog(
                activity = this,
                patient = currentPatient,
                onReassigned = {
                    refreshPatientDetails()
                },
            ).show()
        }

        activitiesAdapter = PatientActivityAdapter(emptyList())
        binding.recyclerViewActivities.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewActivities.adapter = activitiesAdapter

        fetchPatientActivities(currentPatient.id)

        if (currentUser.role == Role.Admin) {
            refreshPatientDetails(showLoading = false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindPatient(patient: Patient) {
        currentPatient = patient

        val formattedGender =
            patient.gender.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        val dob = patient.dateOfBirth?.substringBefore("T") ?: "Not available"
        val healthConditionsText =
            if (patient.healthConditions.isNotEmpty()) {
                patient.healthConditions.joinToString(", ") { condition ->
                    condition.split(" ").joinToString(" ") { word ->
                        word.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        }
                    }
                }
            } else {
                "No conditions listed"
            }

        binding.tvName.text = patient.fullname
        binding.tvAge.text = "${patient.age} years"
        binding.tvDob.text = "Date of Birth: $dob"
        binding.tvGender.text = formattedGender
        binding.tvHealthConditions.text = "Health Conditions: $healthConditionsText"
        binding.tvCaretaker.text = "Caretaker: ${patient.caretaker?.name ?: "Not assigned"}"
        binding.tvAssignedDoctor.text =
            "Assigned Doctor: ${patient.assignedDoctor?.name ?: "Not assigned"}"

        Glide.with(this)
            .load(patient.photoUrl)
            .placeholder(R.drawable.profile)
            .error(R.drawable.profile)
            .circleCrop()
            .into(binding.imagePatient)

        nursesFragment.setAssignedNurses(patient.assignedNurses)
    }

    private fun refreshPatientDetails(showLoading: Boolean = true) {
        val token = "Bearer ${SessionManager.getToken()}"
        CoroutineScope(Dispatchers.IO).launch {
            if (showLoading) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }

            val response =
                try {
                    ApiClient.apiService.getPatientOverview(
                        token = token,
                        patientId = currentPatient.id,
                        organizationId = currentUser.organization,
                    )
                } catch (exception: Exception) {
                    null
                }

            withContext(Dispatchers.Main) {
                if (showLoading) {
                    binding.progressBar.visibility = View.GONE
                }

                if (response?.isSuccessful == true) {
                    response.body()?.patient?.let { updatedPatient ->
                        bindPatient(updatedPatient)
                    }
                }
            }
        }
    }

    private fun fetchPatientActivities(patientId: String) {
        val token = "Bearer ${SessionManager.getToken()}"

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvEmptyMessage.visibility = View.GONE
                binding.recyclerViewActivities.visibility = View.VISIBLE
            }

            try {
                val response = ApiClient.apiService.getPatientActivities(token, patientId)

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val activities = response.body()

                        if (!activities.isNullOrEmpty()) {
                            activitiesAdapter.updateData(activities)
                            binding.recyclerViewActivities.visibility = View.VISIBLE
                            binding.tvEmptyMessage.visibility = View.GONE
                        } else {
                            activitiesAdapter.updateData(emptyList())
                            binding.recyclerViewActivities.visibility = View.GONE
                            binding.tvEmptyMessage.visibility = View.VISIBLE
                            binding.tvEmptyMessage.text = "No patient activities found"
                        }
                    } else {
                        val errorResponse = readError(response.errorBody()?.string())
                        binding.recyclerViewActivities.visibility = View.GONE
                        binding.tvEmptyMessage.visibility = View.VISIBLE
                        binding.tvEmptyMessage.text = "Unable to load patient activities"
                        showMessage(errorResponse ?: "Failed to load activities")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewActivities.visibility = View.GONE
                    binding.tvEmptyMessage.visibility = View.VISIBLE
                    binding.tvEmptyMessage.text = "Network error occurred"
                    showMessage("Network error occurred")
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
