package deakin.gopher.guardian.view.general

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.PatientActivityAdapter
import deakin.gopher.guardian.databinding.ActivityPatientDetailsBinding
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import com.google.gson.Gson
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
            binding.containerPatientInfo.setBackgroundColor(getColor(R.color.TG_blue))
        }

        val patient = intent.getSerializableExtra("patient") as Patient

        // Set patient info views
        binding.tvName.text = patient.fullname
        binding.tvAge.text = "Age: ${patient.age}"
        binding.tvDob.text = "Date of Birth: ${patient.dateOfBirth?.substringBefore("T")}"
        binding.tvGender.text = "Gender: ${
            patient.gender.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }"

        if (patient.healthConditions.isNotEmpty()) {
            val formattedConditions = patient.healthConditions.joinToString(", ") { condition ->
                condition.split(" ").joinToString(" ") { word ->
                    word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
            }
            binding.tvHealthConditions.text = "Health Conditions: $formattedConditions"
        } else {
            binding.tvHealthConditions.text = "Health Conditions: No conditions listed"
        }

        Glide.with(this)
            .load(patient.photoUrl)
            .placeholder(R.drawable.profile)
            .circleCrop()
            .into(binding.imagePatient)

        // Load the assigned nurses fragment dynamically and pass the nurses
        val nursesFragment = PatientAssignedNursesFragment()
        nursesFragment.setAssignedNurses(patient.assignedNurses ?: emptyList())
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentAssignedNursesContainer, nursesFragment)
            .commit()

        // Setup RecyclerView for activity logs
        activitiesAdapter = PatientActivityAdapter(emptyList())
        binding.recyclerViewActivities.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewActivities.adapter = activitiesAdapter

        fetchPatientActivities(patient.id)
    }




    private fun fetchPatientActivities(patientId: String) {
        val token = "Bearer ${SessionManager.getToken()}"
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.VISIBLE

            }
            val response = try {
                deakin.gopher.guardian.services.api.ApiClient.apiService.getPatientActivities(token, patientId)
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
                    val errorBody = response?.errorBody()?.string()
                    val errorResponse = try {
                        Gson().fromJson(errorBody, ApiErrorResponse::class.java)
                    } catch (ex: Exception) {
                        null
                    }
                    showMessage(errorResponse?.apiError ?: "Failed to load activities")
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
