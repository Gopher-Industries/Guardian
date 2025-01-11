package deakin.gopher.guardian.view.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.CarePlanAdapter
import deakin.gopher.guardian.model.CarePlan
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.services.api.ApiService
import deakin.gopher.guardian.view.general.AddMedicalHistoryActivity
import deakin.gopher.guardian.view.patient.careplan.CarePlanActivity
import deakin.gopher.guardian.view.general.AddTaskActivity
import deakin.gopher.guardian.view.general.AddNoteActivity
import deakin.gopher.guardian.view.general.ReportIncidentActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminCarePlanManagementActivity : AppCompatActivity() {

    private lateinit var recyclerViewCarePlans: RecyclerView
    private lateinit var carePlanAdapter: CarePlanAdapter
    private val carePlanList = mutableListOf<CarePlan>()

    private val apiService: ApiService = ApiClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_care_plan_management)

        // RecyclerView setup
        recyclerViewCarePlans = findViewById(R.id.recyclerViewCarePlans)
        setupCarePlanRecyclerView()
        loadCarePlansFromApi()

        // Button navigation logic
        findViewById<CardView>(R.id.iconAddTask).setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.iconAddNote).setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.iconReportIncident).setOnClickListener {
            val intent = Intent(this, ReportIncidentActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.iconMedicalHistory).setOnClickListener {
            val intent = Intent(this, AddMedicalHistoryActivity::class.java)
            intent.putExtra("patientId", "samplePatientId") // Replace with actual patient ID
            startActivity(intent)
        }
    }

    private fun setupCarePlanRecyclerView() {
        carePlanAdapter = CarePlanAdapter(carePlanList)
        recyclerViewCarePlans.layoutManager = LinearLayoutManager(this)
        recyclerViewCarePlans.adapter = carePlanAdapter
    }

    private fun loadCarePlansFromApi() {
        apiService.getCarePlans().enqueue(object : Callback<List<CarePlan>> {
            override fun onResponse(call: Call<List<CarePlan>>, response: Response<List<CarePlan>>) {
                if (response.isSuccessful && response.body() != null) {
                    carePlanList.clear()
                    carePlanList.addAll(response.body()!!)
                    carePlanAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AdminCarePlanManagementActivity, "Failed to load care plans", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CarePlan>>, t: Throwable) {
                Log.e("AdminCarePlanManagement", "Error fetching care plans: ${t.message}")
                Toast.makeText(this@AdminCarePlanManagementActivity, "Error fetching care plans", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

