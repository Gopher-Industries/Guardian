package deakin.gopher.guardian.view.patient

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.MedicationAdapter
import deakin.gopher.guardian.model.Medication
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.launch

class MedicationDetailsActivity : AppCompatActivity() {
    private lateinit var recyclerViewMorning: RecyclerView
    private lateinit var recyclerViewAfternoon: RecyclerView
    private lateinit var recyclerViewEvening: RecyclerView

    private val adapterMorning = MedicationAdapter(emptyList())
    private val adapterAfternoon = MedicationAdapter(emptyList())
    private val adapterEvening = MedicationAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_details)
        recyclerViewMorning = findViewById(R.id.recyclerViewMorning)
        recyclerViewAfternoon = findViewById(R.id.recyclerViewAfternoon)
        recyclerViewEvening = findViewById(R.id.recyclerViewEvening)

        setupRecyclerView(recyclerViewMorning, adapterMorning)
        setupRecyclerView(recyclerViewAfternoon, adapterAfternoon)
        setupRecyclerView(recyclerViewEvening, adapterEvening)

        fetchMedications()
    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        adapter: MedicationAdapter,
    ) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun fetchMedications() {
        val patientId = intent.getStringExtra("patientId") ?: intent.getStringExtra("id") ?: ""
        val token = "Bearer ${SessionManager.getToken()}"

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getPatientMedications(token, patientId)
                if (response.isSuccessful) {
                    val medications = response.body().orEmpty()
                    adapterMorning.updateData(medications)
                    adapterAfternoon.updateData(emptyList())
                    adapterEvening.updateData(emptyList())
                } else {
                    Toast.makeText(this@MedicationDetailsActivity, "Failed to load medications", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MedicationDetailsActivity, "Network error loading medications", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
