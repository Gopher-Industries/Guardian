package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.adapter.PatientListAdapter
import deakin.gopher.guardian.BuildConfig
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.MockPatientData
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.EmailPasswordAuthService
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Homepage4doctor : BaseActivity() {
    private lateinit var patientListAdapter: PatientListAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage4doctor)

        progressBar = findViewById(R.id.progressBarPatients)
        emptyText = findViewById(R.id.tvEmptyPatients)

        patientListAdapter =
            PatientListAdapter(
                emptyList(),
                onPatientClick = { patient ->
                    val intent = Intent(this, PatientOverviewActivity::class.java)
                    intent.putExtra(PatientOverviewActivity.EXTRA_PATIENT_ID, patient.id)
                    startActivity(intent)
                },
            )

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewDoctorPatients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = patientListAdapter

        val signOutButton: Button = findViewById(R.id.signOutButton_doctor)
        signOutButton.setOnClickListener {
            EmailPasswordAuthService.signOut(this)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchPatients()
    }

    private fun fetchPatients() {
        if (BuildConfig.USE_MOCK_PATIENT_FLOW) {
            progressBar.visibility = android.view.View.GONE
            val mocks = MockPatientData.patients
            patientListAdapter.updateData(mocks)
            emptyText.visibility =
                if (mocks.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            return
        }

        val token = "Bearer ${SessionManager.getToken()}"
        val doctorId = SessionManager.getCurrentUser().id
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                progressBar.visibility = android.view.View.VISIBLE
                emptyText.visibility = android.view.View.GONE
            }

            val response =
                try {
                    ApiClient.apiService.getDoctorPatients(token, doctorId)
                } catch (e: Exception) {
                    null
                }

            withContext(Dispatchers.Main) {
                progressBar.visibility = android.view.View.GONE
                if (response?.isSuccessful == true) {
                    val patients = response.body().orEmpty()
                    patientListAdapter.updateData(patients)
                    emptyText.visibility =
                        if (patients.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                } else {
                    emptyText.visibility = android.view.View.VISIBLE
                    Toast.makeText(
                        this@Homepage4doctor,
                        "Failed to load patients",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }
}
