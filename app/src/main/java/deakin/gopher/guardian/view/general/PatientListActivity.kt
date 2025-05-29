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
        )

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (currentUser.role == Role.Nurse) {
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
