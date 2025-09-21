package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import deakin.gopher.guardian.adapter.DoctorListAdapter
import deakin.gopher.guardian.databinding.ActivityAssignDoctorBinding
import deakin.gopher.guardian.model.Doctor

class AssignDoctorActivity : BaseActivity() {
    private lateinit var binding: ActivityAssignDoctorBinding
    private val patientId: String by lazy { intent.getStringExtra("patientId") ?: "" }

    private lateinit var adapter: DoctorListAdapter
    private var allDoctors: List<Doctor> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (patientId.isBlank()) {
            Toast.makeText(this, "Invalid patient", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding = ActivityAssignDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        supportActionBar?.title = "Assign / Request Doctor"

        adapter =
            DoctorListAdapter(
                doctors = emptyList(),
                onAssign = { d -> assignDoctorLocal(d) },
                onRequest = { d -> sendDoctorRequestLocal(d) },
                onUnassign = { d -> unassignDoctorLocal(d) },
                onCancelRequest = { d -> cancelDoctorRequestLocal(d) },
            )

        binding.recyclerViewDoctors.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewDoctors.adapter = adapter

        loadDummyDoctors()

        refreshAdapterState()

        binding.etSearch.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun afterTextChanged(s: Editable?) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    filterLocal(s?.toString().orEmpty())
                }
            },
        )
    }

    private fun prefs() = getSharedPreferences("assignments", MODE_PRIVATE)

    private fun getAssignedDoctorId(): String? = prefs().getString("doctor_for_$patientId", null)

    private fun isRequestAlreadySent(doctorId: String): Boolean = prefs().contains("request_${doctorId}_$patientId")

    private fun refreshAdapterState() {
        val assignedId = getAssignedDoctorId()
        val requestedIds =
            allDoctors
                .map { it.id }
                .filter { isRequestAlreadySent(it) }
                .toSet()
        adapter.updateState(assignedDoctorId = assignedId, requestedDoctorIds = requestedIds)
    }

    private fun filterLocal(query: String) {
        val q = query.trim().lowercase()
        val filtered =
            if (q.isEmpty()) {
                allDoctors
            } else {
                allDoctors.filter {
                    it.fullname.lowercase().contains(q) ||
                        (it.specialty?.lowercase()?.contains(q) == true)
                }
            }

        adapter.updateData(filtered)
        binding.tvEmptyMessage.text = if (filtered.isEmpty()) "No matching doctors" else ""
        binding.tvEmptyMessage.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE

        refreshAdapterState()
    }

    private fun loadDummyDoctors() {
        allDoctors =
            listOf(
                Doctor("d1", "Dr. Priya Sharma", "Cardiology"),
                Doctor("d2", "Dr. Karan Malhotra", "Orthopedics"),
                Doctor("d3", "Dr. Aditi Rao", "Pediatrics"),
                Doctor("d4", "Dr. Vikram Singh", "Neurology"),
                Doctor("d5", "Dr. Meera Iyer", "Dermatology"),
            )
        adapter.updateData(allDoctors)
        binding.tvEmptyMessage.visibility = View.GONE
    }

    private fun assignDoctorLocal(doctor: Doctor) {
        val currentAssigned = getAssignedDoctorId()
        if (currentAssigned != null) {
            val name = allDoctors.firstOrNull { it.id == currentAssigned }?.fullname ?: "another doctor"
            Toast.makeText(this, "Already assigned to $name", Toast.LENGTH_SHORT).show()
            return
        }
        prefs().edit()
            .putString("doctor_for_$patientId", doctor.id)
            .apply()
        Toast.makeText(this, "Assigned ${doctor.fullname}", Toast.LENGTH_SHORT).show()

        finish()
    }

    private fun sendDoctorRequestLocal(doctor: Doctor) {
        val currentAssigned = getAssignedDoctorId()
        if (currentAssigned != null) {
            val name = allDoctors.firstOrNull { it.id == currentAssigned }?.fullname ?: "the assigned doctor"
            Toast.makeText(this, "Patient already assigned to $name", Toast.LENGTH_SHORT).show()
            return
        }

        if (isRequestAlreadySent(doctor.id)) {
            Toast.makeText(this, "Request already sent to ${doctor.fullname}", Toast.LENGTH_SHORT).show()
            return
        }

        val key = "request_${doctor.id}_$patientId"
        val payload =
            """{"patientId":"$patientId","doctorId":"${doctor.id}","at":${System.currentTimeMillis()}}"""
        prefs().edit().putString(key, payload).apply()

        Toast.makeText(this, "Request sent to ${doctor.fullname}", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun unassignDoctorLocal(doctor: Doctor) {
        val key = "doctor_for_$patientId"
        if (prefs().getString(key, null) == null) {
            Toast.makeText(this, "No doctor assigned", Toast.LENGTH_SHORT).show()
            return
        }
        prefs().edit().remove(key).apply()
        Toast.makeText(this, "Unassigned ${doctor.fullname}", Toast.LENGTH_SHORT).show()
        refreshAdapterState() // stay and reflect new state
    }

    private fun cancelDoctorRequestLocal(doctor: Doctor) {
        val key = "request_${doctor.id}_$patientId"
        if (!prefs().contains(key)) {
            Toast.makeText(this, "No request to cancel", Toast.LENGTH_SHORT).show()
            return
        }
        prefs().edit().remove(key).apply()
        Toast.makeText(this, "Cancelled request to ${doctor.fullname}", Toast.LENGTH_SHORT).show()
        refreshAdapterState() // stay and reflect new state
    }
}

//    private fun fetchDoctors() {
//        val token = "Bearer ${SessionManager.getToken()}"
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.Main) { binding.progressBar.show() }
//            val response = try {
//                ApiClient.apiService.getDoctors(token)
//            } catch (e: Exception) {
//                null
//            }
//            withContext(Dispatchers.Main) {
//                binding.progressBar.hide()
//                if (response?.isSuccessful == true) {
//                    val list = response.body().orEmpty()
//                    allDoctors = list
//                    adapter.updateData(list)
//                    binding.tvEmptyMessage.visibility = if (list.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
//                    binding.tvEmptyMessage.text = if (list.isEmpty()) "No doctors available" else ""
//                } else {
//                    val error = response?.errorBody()?.string()
//                    val parsed = try { Gson().fromJson(error, ApiErrorResponse::class.java) } catch (_: Exception) { null }
//                    Toast.makeText(this@AssignDoctorActivity, parsed?.apiError ?: "Failed to load doctors", Toast.LENGTH_SHORT).show()
//                    binding.tvEmptyMessage.visibility = android.view.View.VISIBLE
//                    binding.tvEmptyMessage.text = "Failed to load doctors"
//                }
//            }
//        }
//    }

//    private fun assignDoctor(doctor: Doctor) {
//        val token = "Bearer ${SessionManager.getToken()}"
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.Main) { binding.progressBar.show() }
//            val res =
//                try {
//                ApiClient.apiService.assignDoctor(
//                    token = token,
//                    patientId = patientId,
//                    body = AssignDoctorRequest(doctorId = doctor.id)
//                )
//                } catch (e: Exception) {
//                    null
//                }
//            withContext(Dispatchers.Main) {
//                binding.progressBar.hide()
//                if (res?.isSuccessful == true) {
//                    Toast.makeText(this@AssignDoctorActivity, "Doctor assigned", Toast.LENGTH_SHORT).show()
//                    finish() // PatientListActivity refreshes in onResume()
//                } else {
//                    val error = res?.errorBody()?.string()
//                    val parsed = try { Gson().fromJson(error, ApiErrorResponse::class.java) } catch (_: Exception) { null }
//                    Toast.makeText(this@AssignDoctorActivity, parsed?.apiError ?: "Failed to assign doctor", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
// }
// }
