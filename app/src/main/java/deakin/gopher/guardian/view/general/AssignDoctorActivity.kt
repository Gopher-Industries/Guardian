package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import deakin.gopher.guardian.databinding.ActivityAssignDoctorBinding
import deakin.gopher.guardian.model.Doctor
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.view.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssignDoctorActivity : BaseActivity() {
    private lateinit var binding: ActivityAssignDoctorBinding

//    private lateinit var adapter: DoctorListAdapter
    private var allDoctors: List<Doctor> = emptyList()
    private var searchJob: Job? = null
    private val patientId: String by lazy {
        // Expecting "patientId" putExtra from PatientListActivity
        intent.getStringExtra("patientId") ?: ""
    }

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
        supportActionBar?.title = "Assign Doctor"

//        adapter = DoctorListAdapter(
//            doctors = emptyList(),
//            onSelect = { doctor ->
//                assignDoctor(doctor)
//            }
//        )

        binding.recyclerViewDoctors.layoutManager = LinearLayoutManager(this)
//        binding.recyclerViewDoctors.adapter = adapter

        // Search (debounced)
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
                    // local filter; if you prefer server-side search, call fetchDoctors(query)
                    filterLocal(s?.toString().orEmpty())
                }
            },
        )

//        fetchDoctors()
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
//        adapter.updateData(filtered)
        binding.tvEmptyMessage.text = if (filtered.isEmpty()) "No matching doctors" else ""
        binding.tvEmptyMessage.visibility = if (filtered.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
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

    private fun assignDoctor(doctor: Doctor) {
        val token = "Bearer ${SessionManager.getToken()}"
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) { binding.progressBar.show() }
            val res =
                try {
//                ApiClient.apiService.assignDoctor(
//                    token = token,
//                    patientId = patientId,
//                    body = AssignDoctorRequest(doctorId = doctor.id)
//                )
                } catch (e: Exception) {
                    null
                }
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
        }
    }
}
// }
