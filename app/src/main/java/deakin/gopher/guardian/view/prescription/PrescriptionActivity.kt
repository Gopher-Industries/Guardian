// package deakin.gopher.guardian.view.prescription
//
// import android.content.Intent
// import android.os.Bundle
// import android.view.View
// import android.widget.TextView
// import androidx.appcompat.app.AppCompatActivity
// import androidx.recyclerview.widget.LinearLayoutManager
// import androidx.recyclerview.widget.RecyclerView
// import com.google.android.material.floatingactionbutton.FloatingActionButton
// import deakin.gopher.guardian.R
// import deakin.gopher.guardian.model.Prescription
// import deakin.gopher.guardian.model.login.SessionManager
// import deakin.gopher.guardian.services.api.ApiClient
// import deakin.gopher.guardian.services.api.ApiService
// import deakin.gopher.guardian.view.general.PrescriptionAdapter
// import retrofit2.Call
// import retrofit2.Callback
// import retrofit2.Response
//
// class PrescriptionActivity : AppCompatActivity() {
//
//    private lateinit var fabAddPrescription: FloatingActionButton
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var tvEmptyMessage: TextView
//    private lateinit var adapter: PrescriptionAdapter
//    private val prescriptionList = mutableListOf<Prescription>()
//
//    private val apiService: ApiService by lazy { ApiClient.apiService }
//    private val patientId = "68950c034af33273204ee634"
//    private val bearerToken: String
//        get() = "Bearer ${SessionManager.getToken()}"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_prescription)
//
//        fabAddPrescription = findViewById(R.id.btnPrescription)
//        recyclerView = findViewById(R.id.recyclerViewPrescriptions)
//        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)
//
//        adapter = PrescriptionAdapter(prescriptionList)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = adapter
//
//        fabAddPrescription.setOnClickListener {
//            val intent = Intent(this, IssuePrescriptionActivity::class.java)
//            startActivityForResult(intent, 100)
//        }
//
//        fetchPrescriptions()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // Always fetch latest prescriptions whenever activity comes into view
//        fetchPrescriptions()
//    }
//
//    private fun fetchPrescriptions() {
//        apiService.getPrescriptionsForPatient(bearerToken, patientId)
//            .enqueue(object : Callback<List<Prescription>> {
//                override fun onResponse(
//                    call: Call<List<Prescription>>,
//                    response: Response<List<Prescription>>
//                ) {
//                    if (response.isSuccessful) {
//                        val prescriptions = response.body() ?: emptyList()
//                        prescriptionList.clear()
//                        prescriptionList.addAll(prescriptions)
//                        updateUI()
//                    } else {
//                        tvEmptyMessage.text = "Error loading prescriptions"
//                        tvEmptyMessage.visibility = View.VISIBLE
//                        recyclerView.visibility = View.GONE
//                    }
//                }
//
//                override fun onFailure(call: Call<List<Prescription>>, t: Throwable) {
//                    tvEmptyMessage.text = "Failed to connect to server"
//                    tvEmptyMessage.visibility = View.VISIBLE
//                    recyclerView.visibility = View.GONE
//                }
//            })
//    }
//
//    private fun updateUI() {
//        if (prescriptionList.isEmpty()) {
//            tvEmptyMessage.visibility = View.VISIBLE
//            recyclerView.visibility = View.GONE
//        } else {
//            tvEmptyMessage.visibility = View.GONE
//            recyclerView.visibility = View.VISIBLE
//        }
//        adapter.notifyDataSetChanged()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 100 && resultCode == RESULT_OK) {
//            // Instead of adding locally, re-fetch from backend
//            fetchPrescriptions()
//        }
//    }
// }

package deakin.gopher.guardian.view.prescription

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Prescription
import deakin.gopher.guardian.model.PrescriptionResponse
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.services.api.ApiService
import deakin.gopher.guardian.view.general.PrescriptionAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrescriptionActivity : AppCompatActivity() {
    private lateinit var fabAddPrescription: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyMessage: TextView
    private lateinit var adapter: PrescriptionAdapter
    private val prescriptionList = mutableListOf<Prescription>()

    private val apiService: ApiService by lazy { ApiClient.apiService }
    private val patientId = "68950c034af33273204ee634"
    private val bearerToken: String
        get() = "Bearer ${SessionManager.getToken()}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prescription)

        fabAddPrescription = findViewById(R.id.btnPrescription)
        recyclerView = findViewById(R.id.recyclerViewPrescriptions)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)

        adapter = PrescriptionAdapter(prescriptionList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fabAddPrescription.setOnClickListener {
            val intent = Intent(this, IssuePrescriptionActivity::class.java)
            startActivity(intent)
        }

        fetchPrescriptions()
    }

    override fun onResume() {
        super.onResume()
        // Always fetch latest prescriptions whenever activity comes into view
        fetchPrescriptions()
    }

    private fun fetchPrescriptions() {
        apiService.getPrescriptionsForPatient(bearerToken, patientId)
            .enqueue(
                object : Callback<PrescriptionResponse> { // ✅ Change callback type
                    override fun onResponse(
                        call: Call<PrescriptionResponse>,
                        response: Response<PrescriptionResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val prescriptions = response.body()?.prescriptions ?: emptyList() // ✅ extract list

                            Log.d("PrescriptionActivity", "Fetched ${prescriptions.size} prescriptions")
                            prescriptionList.clear()
                            prescriptionList.addAll(prescriptions)
                            updateUI()
                        } else {
                            Log.e("PrescriptionActivity", "Failed: ${response.code()}")
                            tvEmptyMessage.text = "Error loading prescriptions"
                            tvEmptyMessage.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                    }

                    override fun onFailure(
                        p0: Call<PrescriptionResponse>,
                        t: Throwable,
                    ) {
                        Log.e("PrescriptionActivity", "Fetch error: ${t.localizedMessage}")
                        tvEmptyMessage.text = "Failed to connect to server"
                        tvEmptyMessage.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                },
            )
    }

    private fun updateUI() {
        if (prescriptionList.isEmpty()) {
            tvEmptyMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmptyMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
    }
}
