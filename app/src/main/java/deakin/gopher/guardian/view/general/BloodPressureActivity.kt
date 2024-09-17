package deakin.gopher.guardian.view.general

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import deakin.gopher.guardian.databinding.ActivityBloodPressureBinding

class BloodPressureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBloodPressureBinding
    private val bpData = mutableListOf<Pair<Int, Int>>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBloodPressureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        firebaseAuth.currentUser?.let { user ->
            Log.e("Print--->", user.uid)
            setupFirebaseListener(user.uid)
        }

        binding.btnShowData.setOnClickListener {
            val systolic = binding.etSystolic.text.toString().toIntOrNull()
            val diastolic = binding.etDiastolic.text.toString().toIntOrNull()

            if (systolic != null && diastolic != null) {
                binding.etSystolic.setText("")
                binding.etDiastolic.setText("")
                bpData.add(Pair(systolic, diastolic))
                sendInFirebaseData(systolic, diastolic)
                showBloodPressureChart(bpData)
            } else {
                Toast.makeText(this, "Please enter valid BP values", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendInFirebaseData(systolic: Int, diastolic: Int) {
        val bloodPressureData = hashMapOf<String, Any>(
            "Systolic" to systolic,
            "Diastolic" to diastolic
        )
        databaseReference.child("BloodPressure").child(firebaseAuth.currentUser!!.uid)
            .push()
            .setValue(bloodPressureData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data sent successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase Error", "Failed to send data", e)
            }
    }

    private fun setupFirebaseListener(userId: String) {
        databaseReference.child("BloodPressure").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bpData.clear()
                    for (dataSnapshot in snapshot.children) {
                        val systolic = dataSnapshot.child("Systolic").getValue(Int::class.java)
                        val diastolic = dataSnapshot.child("Diastolic").getValue(Int::class.java)

                        if (systolic != null && diastolic != null) {
                            bpData.add(Pair(systolic, diastolic))
                        }
                    }
                    showBloodPressureChart(bpData)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase Error", "Failed to read data", error.toException())
                }
            })
    }

    private fun showBloodPressureChart(bpData: List<Pair<Int, Int>>) {
        val lineChart = binding.lineChart

        lineChart.clear()

        val systolicEntries = mutableListOf<Entry>()
        val diastolicEntries = mutableListOf<Entry>()


        bpData.forEachIndexed { index, (systolic, diastolic) ->
            systolicEntries.add(Entry(index.toFloat(), systolic.toFloat()))
            diastolicEntries.add(Entry(index.toFloat(), diastolic.toFloat()))
        }


        val systolicDataSet = LineDataSet(systolicEntries, "Systolic")
        val diastolicDataSet = LineDataSet(diastolicEntries, "Diastolic")


        systolicDataSet.color = Color.RED
        systolicDataSet.valueTextColor = Color.RED
        systolicDataSet.lineWidth = 3f // Line thickness
        systolicDataSet.circleRadius = 5f // Circle size on points
        systolicDataSet.setCircleColor(Color.RED) // Circle color
        systolicDataSet.setDrawCircleHole(false)
        systolicDataSet.valueTextSize = 10f // Text size for values
        systolicDataSet.setDrawValues(true)


        diastolicDataSet.color = Color.BLUE
        diastolicDataSet.valueTextColor = Color.BLUE
        diastolicDataSet.lineWidth = 3f // Line thickness
        diastolicDataSet.circleRadius = 5f // Circle size on points
        diastolicDataSet.setCircleColor(Color.BLUE) // Circle color
        diastolicDataSet.setDrawCircleHole(false)
        diastolicDataSet.valueTextSize = 10f // Text size for values
        diastolicDataSet.setDrawValues(true)

        val lineData = LineData(systolicDataSet, diastolicDataSet)
        lineChart.data = lineData

        lineChart.description.isEnabled = false // Hide description label
        lineChart.setNoDataText("No Blood Pressure Data Available")
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.animateX(1500) // Animation effect for X-axis
        lineChart.setBackgroundColor(Color.WHITE) // Background color


        val legend = lineChart.legend
        legend.form = Legend.LegendForm.LINE
        legend.textSize = 12f
        legend.textColor = Color.BLACK
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)


        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(true)
        xAxis.textSize = 12f
        xAxis.textColor = Color.BLACK

        val leftAxis = lineChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.textSize = 12f
        leftAxis.textColor = Color.BLACK
        leftAxis.axisMinimum = 0f // Start from 0

        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false // Disable right Y Axis

        lineChart.invalidate() // Refresh chart with new data
    }
}
