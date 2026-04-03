package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.NurseListAdapter
import deakin.gopher.guardian.databinding.ActivityAssignNurseBinding
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.model.register.User
import deakin.gopher.guardian.services.api.ApiClient
//import deakin.gopher.guardian.services.api.AssignNurseRequest
import deakin.gopher.guardian.view.hide
import deakin.gopher.guardian.view.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssignNurseActivity : BaseActivity() {
    private lateinit var binding: ActivityAssignNurseBinding
    private lateinit var nurseListAdapter: NurseListAdapter

    private var patientId: String = ""
    private var patientName: String = ""

    private val currentUser = SessionManager.getCurrentUser()

    companion object {
        const val EXTRA_PATIENT_ID = "patientId"
        const val EXTRA_PATIENT_NAME = "patientName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssignNurseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patientId = intent.getStringExtra(EXTRA_PATIENT_ID).orEmpty()
        patientName = intent.getStringExtra(EXTRA_PATIENT_NAME).orEmpty()

        if (patientId.isBlank()) {
            showMessage("Patient information is missing")
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        setupUi()
//        fetchNurses()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (currentUser.role == Role.Nurse) {
            binding.toolbar.setBackgroundColor(getColor(R.color.TG_blue))
        }
    }

    private fun setupUi() {
        binding.tvSelectedPatient.text = if (patientName.isNotBlank()) {
            "Assign a nurse to $patientName"
        } else {
            "Assign a nurse"
        }
    }

    private fun setupRecyclerView() {
        nurseListAdapter = NurseListAdapter(emptyList()) { nurse ->
//                showAssignConfirmation(nurse)
        }

        binding.recyclerViewNurses.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNurses.adapter = nurseListAdapter
    }

//    private fun fetchNurses() {
//        val token = "Bearer ${SessionManager.getToken()}"
//
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.Main) {
//                binding.progressBar.show()
//            }
//
//            val response =
//                try {
////                    ApiClient.apiService.getAllNurses(token)
//                } catch (e: Exception) {
//                    null
//                }
//
//            withContext(Dispatchers.Main) {
//                binding.progressBar.hide()
//
//                if (response?.isSuccessful == true) {
//                    val nurses = response.body().orEmpty()
//
//                    if (nurses.isNotEmpty()) {
//                        nurseListAdapter.updateData(nurses)
//                        binding.tvEmptyMessage.visibility = android.view.View.GONE
//                    } else {
//                        binding.tvEmptyMessage.visibility = android.view.View.VISIBLE
//                    }
//                } else {
//                    val errorResponse =
//                        try {
//                            Gson().fromJson(
//                                response?.errorBody()?.string(),
//                                ApiErrorResponse::class.java,
//                            )
//                        } catch (e: Exception) {
//                            null
//                        }
//
//                    showMessage(errorResponse?.apiError ?: "Failed to load nurses")
//                }
//            }
//        }
//    }

//    private fun showAssignConfirmation(nurse: User) {
//        AlertDialog.Builder(this)
//            .setTitle("Assign Nurse")
//            .setMessage("Assign ${nurse.name} to ${patientName.ifBlank { "this patient" }}?")
//            .setPositiveButton("Assign") { _, _ ->
//                assignNurseToPatient(nurse)
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }

//    private fun assignNurseToPatient(nurse: User) {
//        val token = "Bearer ${SessionManager.getToken()}"
//
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.Main) {
//                binding.progressBar.show()
//            }
//
//            val response =
//                try {
//                    ApiClient.apiService.assignNurseToPatient(
//                        token = token,
//                        patientId = patientId,
//                        request = AssignNurseRequest(nurse.id),
//                    )
//                } catch (e: Exception) {
//                    null
//                }
//
//            withContext(Dispatchers.Main) {
//                binding.progressBar.hide()
//
//                if (response?.isSuccessful == true) {
//                    showMessage("Nurse assigned successfully")
//                    setResult(RESULT_OK)
//                    finish()
//                } else {
//                    val errorResponse =
//                        try {
//                            Gson().fromJson(
//                                response?.errorBody()?.string(),
//                                ApiErrorResponse::class.java,
//                            )
//                        } catch (e: Exception) {
//                            null
//                        }
//
//                    showMessage(errorResponse?.apiError ?: "Failed to assign nurse")
//                }
//            }
//        }
//    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}