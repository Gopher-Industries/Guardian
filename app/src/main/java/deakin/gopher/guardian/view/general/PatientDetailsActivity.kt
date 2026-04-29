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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PatientDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityPatientDetailsBinding
    private val currentUser = SessionManager.getCurrentUser()
    private lateinit var activitiesAdapter: PatientActivityAdapter

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
           // binding.containerPatientInfo.setBackgroundColor(getColor(R.color.TG_blue))
        }

        val patient = intent.getSerializableExtra("patient") as? Patient
        if (patient == null) {
            showMessage("Patient details not available")
            finish()
            return
        }

        bindPatientDetails(patient)

        val nursesFragment = PatientAssignedNursesFragment()
        nursesFragment.setAssignedNurses(patient.assignedNurses ?: emptyList())
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentAssignedNursesContainer, nursesFragment)
            .commit()

        activitiesAdapter = PatientActivityAdapter(emptyList())
        binding.recyclerViewActivities.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewActivities.adapter = activitiesAdapter

        fetchPatientActivities(patient.id)
    }

    @SuppressLint("SetTextI18n")
    private fun bindPatientDetails(patient: Patient) {
        val formattedGender = patient.gender.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        val dob = patient.dateOfBirth?.substringBefore("T") ?: "Not available"

        val healthConditionsText =
            if (!patient.healthConditions.isNullOrEmpty()) {
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

        Glide.with(this)
            .load(patient.photoUrl)
            .placeholder(R.drawable.profile)
            .error(R.drawable.profile)
            .circleCrop()
            .into(binding.imagePatient)
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
                val response = deakin.gopher.guardian.services.api.ApiClient
                    .apiService
                    .getPatientActivities(token, patientId)

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val activities = response.body()

                        if (!activities.isNullOrEmpty()) {
                            activitiesAdapter.updateData(activities)
                            binding.recyclerViewActivities.visibility = View.VISIBLE
                            binding.tvEmptyMessage.visibility = View.GONE
                        } else {
                            binding.recyclerViewActivities.visibility = View.GONE
                            binding.tvEmptyMessage.visibility = View.VISIBLE
                            binding.tvEmptyMessage.text = "No patient activities found"
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()

                        val errorResponse: ApiErrorResponse? =
                            if (!errorBody.isNullOrBlank()) {
                                try {
                                    Gson().fromJson(errorBody, ApiErrorResponse::class.java)
                                } catch (e: Exception) {
                                    null
                                }
                            } else {
                                null
                            }

                        binding.recyclerViewActivities.visibility = View.GONE
                        binding.tvEmptyMessage.visibility = View.VISIBLE
                        binding.tvEmptyMessage.text = "Unable to load patient activities"

                        showMessage(errorResponse?.apiError ?: "Failed to load activities")
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

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}