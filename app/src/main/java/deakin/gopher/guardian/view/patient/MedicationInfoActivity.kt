package deakin.gopher.guardian.view.patient

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import deakin.gopher.guardian.R

class MedicationInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_info) // Replace with your actual layout

        // Initialize the Spinner
        val spinner: Spinner = findViewById(R.id.dropdownSpinner)

        // Get the string array from resources
        val items = resources.getStringArray(R.array.dropdown_items)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinner.adapter = adapter
    }
}