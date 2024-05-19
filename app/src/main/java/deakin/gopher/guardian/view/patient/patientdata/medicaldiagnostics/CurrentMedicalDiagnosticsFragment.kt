package deakin.gopher.guardian.view.patient.patientdata.medicaldiagnostics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.MedicalDiagnostic
import deakin.gopher.guardian.view.patient.patientdata.heartrate.HeartRateActivity

class CurrentMedicalDiagnosticsFragment : Fragment {
    private val editTextIds =
        intArrayOf(
            R.id.currentMedicalDiagnosticsNameTextView,
            R.id.currentMedicalDiagnosticsBloodPressureTextView,
            R.id.currentMedicalDiagnosticsHeartRateTextView,
            R.id.currentMedicalDiagnosticsPatientTemperatureTextView,
            R.id.currentMedicalDiagnosticsGlucoseLevelTextView,
            R.id.currentMedicalDiagnosticsO2SaturationTextView,
            R.id.currentMedicalDiagnosticsPulseRateTextView,
            R.id.currentMedicalDiagnosticsRespirationRateTextView,
            R.id.currentMedicalDiagnosticsBloodfatLevelTextView,
        )
    private val editButtonIds =
        intArrayOf(
            R.id.current_name_pencil,
            R.id.current_blood_pressure_pencil,
            R.id.current_heart_rate_pencil,
            R.id.current_temperature_pencil,
            R.id.current_glucose_level_pencil,
            R.id.current_blood_o2_saturation_pencil,
            R.id.current_pulse_rate_pencil,
            R.id.current_respiration_rate_pencil,
            R.id.current_bloodfat_level_pencil,
        )

    private lateinit var medicalDiagnosticCurrent: MedicalDiagnostic
    private lateinit var editTextArray: Array<EditText?>
    private lateinit var editButtonArray: Array<Button?>
    private lateinit var patientId: String

    constructor()
    constructor(patientId: String?) {
        if (patientId != null) {
            this.patientId = patientId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val rootView =
            inflater.inflate(R.layout.fragment_current_medical_diagnostics, container, false)
        editTextArray = arrayOfNulls(editTextIds.size)
        editButtonArray = arrayOfNulls(editButtonIds.size)
        for (i in editTextIds.indices) {
            editTextArray[i] = rootView.findViewById(editTextIds[i])
            editButtonArray[i] = rootView.findViewById(editButtonIds[i])
            val editText = editTextArray[i]
            val editButton = editButtonArray[i]
            editText!!.isFocusable = false
            editText.isFocusableInTouchMode = false
            editText.isEnabled = false
            editButton!!.visibility = View.INVISIBLE
            editButton.setOnClickListener { v: View? ->
                editText.isFocusable = true
                editText.isFocusableInTouchMode = true
                editText.isEnabled = true
                editText.requestFocus()
                editText.selectAll()
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        val heartRateArrowButton: ImageView =
            rootView.findViewById(R.id.currentMedicalDiagnosticsHeartRateArrowButton)
        heartRateArrowButton.setOnClickListener {
            Intent(this.context, HeartRateActivity::class.java).also {
                startActivity(it)
            }
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInfo()
    }
    private fun setInfo() {
        val reference = FirebaseDatabase.getInstance().getReference("health_details")
        val query = reference.orderByChild("patient_id").equalTo(patientId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val diagnostic = childSnapshot.getValue(MedicalDiagnostic::class.java)
                        if (diagnostic != null && diagnostic.current == true) {
                            updateUI(diagnostic)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching data", error.toException())
            }
        })
    }

    private fun updateUI(diagnostic: MedicalDiagnostic) {
        editTextArray[0]?.setText(diagnostic.name ?: "") // Name
        editTextArray[1]?.setText(diagnostic.bloodPressure ?: "") // Blood Pressure
        editTextArray[2]?.setText(diagnostic.patientTemp ?: "") // Patient Temperature
        editTextArray[3]?.setText(diagnostic.glucoseLevel ?: "") // Glucose Level
        editTextArray[4]?.setText(diagnostic.oxygenSaturation ?: "") // Oxygen Saturation
        editTextArray[5]?.setText(diagnostic.pulseRate ?: "") // Pulse Rate
        editTextArray[6]?.setText(diagnostic.respirationRate ?: "") // Respiration Rate
        editTextArray[7]?.setText(diagnostic.bloodFatLevel ?: "") // Blood Fat Level
    }

    fun setEditState(isEditable: Boolean) {
        if (isEditable) {
            for (editButton in editButtonArray) {
                editButton!!.visibility = View.VISIBLE
            }
        } else {
            for (editButton in editButtonArray) {
                editButton!!.visibility = View.INVISIBLE
            }
            for (editText in editTextArray) {
                editText!!.isFocusable = false
                editText.isFocusableInTouchMode = false
                editText.isEnabled = false
            }
            saveInFirebase()
        }
    }

    private fun saveInFirebase() {
        val reference = FirebaseDatabase.getInstance().getReference("health_details")
        val query = reference.orderByChild("patient_id").equalTo(patientId)
        if (dataChecker()) {
            query.addListenerForSingleValueEvent(MyValueEventListener())
        }
    }

    private fun dataChecker(): Boolean {
        for (editText in editTextArray) {
            if (TextUtils.isEmpty(editText!!.text)) {
                editText.error = "it shouldn't be empty!"
                return false
            }
        }
        medicalDiagnosticCurrent =
            MedicalDiagnostic(
                patientId,
                editTextArray[0]!!.text.toString(),
                editTextArray[1]!!.text.toString(),
                editTextArray[2]!!.text.toString(),
                editTextArray[3]!!.text.toString(),
                editTextArray[4]!!.text.toString(),
                editTextArray[5]!!.text.toString(),
                editTextArray[6]!!.text.toString(),
                editTextArray[7]!!.text.toString(),
                true,
            )
        return true
    }

    private inner class MyValueEventListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (childSnapshot in snapshot.children) {
                val isCurrent =
                    childSnapshot.child("current").getValue(
                        Boolean::class.java,
                    )
                if (java.lang.Boolean.TRUE == isCurrent) {
                    childSnapshot.ref.setValue(medicalDiagnosticCurrent)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    }
}
