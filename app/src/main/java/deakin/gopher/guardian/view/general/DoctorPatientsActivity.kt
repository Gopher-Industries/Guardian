package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import deakin.gopher.guardian.adapter.PatientListAdapter
import deakin.gopher.guardian.databinding.ActivityDoctorPatientsBinding
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.model.register.User

class DoctorPatientsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorPatientsBinding
    private lateinit var adapter: PatientListAdapter
//    private var allPatients: MutableList<Patient> = mutableListOf()

    private val dummyCaretaker =
        User(
            id = "0",
            email = "N/A",
            name = TODO(),
            roleName = TODO(),
            photoUrl = TODO(),
            organization = TODO(),
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorPatientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "My Patients"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter =
            PatientListAdapter(
                emptyList(),
                onPatientClick = { patient ->
                },
            )

        binding.recyclerViewPatients.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPatients.adapter = adapter

        loadMyPatients()
    }

    private fun loadMyPatients() {
        val prefs = getSharedPreferences("assignments", MODE_PRIVATE)
        val myDoctorId = SessionManager.getCurrentUser()?.id ?: return

        val results = mutableListOf<Patient>()
        for ((key, value) in prefs.all) {
            if (key.startsWith("doctor_for_") && value == myDoctorId) {
                val patientId = key.removePrefix("doctor_for_")

                // Replace this dummy Patient with a real fetch operation later
                results.add(
                    Patient(
                        id = patientId,
                        fullname = "Patient $patientId",
                        photoUrl = "",
                        dateOfBirth = "",
                        _age = 0,
                        gender = "Unknown",
                        healthConditions = emptyList(),
                        caretaker = dummyCaretaker,
                        assignedNurses = emptyList(),
                    ),
                )

                adapter.updateData(results)
                binding.tvEmptyMessage.visibility =
                    if (results.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
}
