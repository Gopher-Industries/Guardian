package deakin.gopher.guardian.view.patient

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.MedicationAdapter
import deakin.gopher.guardian.model.Medication

class MedicationDetailsActivity : AppCompatActivity() {

    private lateinit var recyclerViewMorning: RecyclerView
    private lateinit var recyclerViewAfternoon: RecyclerView
    private lateinit var recyclerViewEvening: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_details)
        recyclerViewMorning = findViewById(R.id.recyclerViewMorning)
        recyclerViewAfternoon = findViewById(R.id.recyclerViewAfternoon)
        recyclerViewEvening = findViewById(R.id.recyclerViewEvening)

        // Example data
        val morningMedications = listOf(
            Medication("Azithromycin X 2"),
            Medication("Levothyroxine X 1"),
            Medication("Lisinopril X 2")
        )

        val afternoonMedications = listOf(
            Medication("Azithromycin X 2"),
            Medication("Levothyroxine X 1"),
            Medication("Lisinopril X 2")
        )

        val eveningMedications = listOf(
            Medication("Azithromycin X 1"),
            Medication("Levothyroxine X 1")
        )

        setupRecyclerView(recyclerViewMorning, morningMedications)
        setupRecyclerView(recyclerViewAfternoon, afternoonMedications)
        setupRecyclerView(recyclerViewEvening, eveningMedications)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, medications: List<Medication>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MedicationAdapter(medications)
    }
}
