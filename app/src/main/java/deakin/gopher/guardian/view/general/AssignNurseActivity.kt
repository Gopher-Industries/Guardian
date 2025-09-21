@file:Suppress("ktlint")

package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.user.Nurse
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssignNurseActivity : AppCompatActivity() {

    private lateinit var spinnerNurses: Spinner
    private lateinit var btnAssign: Button
    private lateinit var progressBar: ProgressBar


    private var nursesList: MutableList<Nurse> = mutableListOf()
    private var assignedNurseIds: MutableSet<String> = mutableSetOf()
    private var selectedNurse: Nurse? = null
    private var patientId: String = ""
    private lateinit var patientName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_nurse)

        spinnerNurses = findViewById(R.id.spinnerNurses)
        btnAssign = findViewById(R.id.btnAssign)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        // Get patientId from intent extras
        patientId = intent.getStringExtra("patientId") ?: ""

        fetchNurses()

        spinnerNurses.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedNurse = if (position > 0) nursesList[position - 1] else null
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnAssign.setOnClickListener {
            if (selectedNurse != null) {
                assignNurse(selectedNurse!!)
            } else {
                Toast.makeText(this, "Please select a nurse", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchNurses() {
        val token = "Bearer ${SessionManager.getToken()}"
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getAllNurses(token)
                if (response.isSuccessful && response.body() != null) {
                    val allNurses = response.body()!!.nurses
                    // Filter out nurses already assigned to this patient
                    nursesList.clear()
                    nursesList.addAll(allNurses.filter { it._id !in assignedNurseIds })

                    withContext(Dispatchers.Main) {
                        populateSpinner()
                        progressBar.visibility = View.GONE
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@AssignNurseActivity, "Failed to fetch nurses", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@AssignNurseActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateSpinner() {
        val names = mutableListOf("Select a nurse")
        names.addAll(nursesList.map { it.fullname })
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNurses.adapter = adapter
    }

    private fun assignNurse(nurse: Nurse) {
        val token = "Bearer ${SessionManager.getToken()}"
        val body = mapOf(
            "nurseId" to nurse._id,
            "patientId" to patientId
        )

        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.assignNurse(token, body)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AssignNurseActivity,
                            "Assigned nurse: ${nurse.fullname}",
                            Toast.LENGTH_SHORT
                        ).show()

                        sendAssignmentNotification(nurse, patientName = patientName )
                        nursesList.remove(nurse)
                        populateSpinner()
                    } else {
                        Toast.makeText(this@AssignNurseActivity, "Failed to assign nurse", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@AssignNurseActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendAssignmentNotification(nurse: Nurse, patientName: String) {
        val token = "Bearer ${SessionManager.getToken()}"
        val body = mapOf(
            "userId" to nurse._id,
            "title" to "New Patient Assigned",
            "message" to "Patient has been assigned to you"
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = ApiClient.apiService.createNotification(token, body)
                if (!resp.isSuccessful) {
                    Log.e("AssignNurse", "Notification API failed: ${resp.code()}")
                }
            } catch (e: Exception) {
                Log.e("AssignNurse", "Notification API error", e)
            }
        }
    }

}