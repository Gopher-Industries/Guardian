package deakin.gopher.guardian.view.patient

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import deakin.gopher.guardian.adapter.PatientLogAdapter
import deakin.gopher.guardian.databinding.ActivityPatientLogsBinding
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.PatientLog
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.view.general.AddPatientLogActivity
import kotlinx.coroutines.launch

class PatientLogsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientLogsBinding
    private lateinit var adapter: PatientLogAdapter

    private var patients: List<Patient> = emptyList()
    private var patientId: String = ""
    private val api = ApiClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Patient Logs"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter =
            PatientLogAdapter(emptyList()) { log ->

                AlertDialog.Builder(this)
                    .setTitle("Delete Log")
                    .setMessage("Are you sure you want to delete this log?")
                    .setPositiveButton("Yes") { _, _ ->
                        deleteLog(log)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        fetchAssignedPatients()

        binding.addLogBtn.setOnClickListener {
            val intent = Intent(this, AddPatientLogActivity::class.java)
            intent.putExtra("patientId", patientId)
            startActivity(intent)
        }
    }

    private fun fetchAssignedPatients() {
        lifecycleScope.launch {
            try {
                val response =
                    api.getAssignedPatients(
                        "Bearer ${SessionManager.getToken()}",
                    )

                if (response.isSuccessful) {
                    patients = response.body() ?: emptyList()

                    val patientNames = patients.map { it.fullname }

                    val spinnerAdapter =
                        ArrayAdapter(
                            this@PatientLogsActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            patientNames,
                        )

                    binding.patientSpinner.adapter = spinnerAdapter

                    binding.patientSpinner.setOnItemSelectedListener(
                        object : android.widget.AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: android.widget.AdapterView<*>?,
                                view: android.view.View?,
                                position: Int,
                                id: Long,
                            ) {
                                patientId = patients[position].id
                                fetchLogs()
                            }

                            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
                        },
                    )
                } else {
                    Toast.makeText(
                        this@PatientLogsActivity,
                        "Failed to load patients",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@PatientLogsActivity,
                    e.message,
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (patientId.isNotEmpty()) {
            fetchLogs()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun fetchLogs() {
        binding.progressBar.visibility = View.VISIBLE
        binding.emptyText.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response =
                    api.getPatientLogs(
                        "Bearer ${SessionManager.getToken()}",
                        patientId,
                    )

                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val logs = response.body() ?: emptyList()

                    adapter.updateData(logs)

                    if (logs.isEmpty()) {
                        binding.emptyText.visibility = View.VISIBLE
                    } else {
                        binding.emptyText.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(
                        this@PatientLogsActivity,
                        "Failed to load logs",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE

                Toast.makeText(
                    this@PatientLogsActivity,
                    "Something went wrong while loading logs",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }

    private fun deleteLog(log: PatientLog) {
        lifecycleScope.launch {
            try {
                val response =
                    api.deletePatientLog(
                        "Bearer ${SessionManager.getToken()}",
                        log._id,
                    )

                if (response.isSuccessful) {
                    Toast.makeText(this@PatientLogsActivity, "Log deleted successfully", Toast.LENGTH_SHORT).show()
                    fetchLogs()
                } else {
                    Toast.makeText(this@PatientLogsActivity, "Delete failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PatientLogsActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
