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
                val intent = Intent(this, AssignNurseActivity::class.java)
                intent.putExtra("patientId", patient.id)
                intent.putExtra("patientName", patient.fullname)
                startActivity(intent)
            },
            onEditClick = { patient ->
                val intent = Intent(this, EditPatientActivity::class.java)
                intent.putExtra("patient", patient)
                startActivity(intent)
            },
            onDeleteClick = { patient ->
                confirmDeletePatient(patient)
            },
        )

    private fun confirmDeletePatient(patient: Patient) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Delete Patient")
        builder.setMessage("Are you sure you want to delete?")

        builder.setPositiveButton("Delete") { dialog, _ ->
            deletePatient(patient)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_patient -> {
                val intent = Intent(this, AddNewPatientActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.assign_nurse -> {
                val intent = Intent(this, AssignNurseActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
            if (patientListAdapter.itemCount <= 0) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.show()
                }
            }

            val response = ApiClient.apiService.getAssignedPatients(token)

            withContext(Dispatchers.Main) {
                binding.progressBar.hide()

                if (response.isSuccessful) {
                    val patients =
                        response.body()?.filter { patient ->
                            patient.isDeleted != true
                        }

                    if (!patients.isNullOrEmpty()) {
                        patientListAdapter.updateData(patients)
                        binding.tvEmptyMessage.visibility = View.GONE
                    } else {
                        binding.tvEmptyMessage.visibility = View.VISIBLE
                    }
                } else {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_patient, menu)
        return true
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
