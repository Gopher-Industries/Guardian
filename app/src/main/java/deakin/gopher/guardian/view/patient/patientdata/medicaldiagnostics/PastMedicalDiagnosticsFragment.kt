package deakin.gopher.guardian.view.patient.patientdata.medicaldiagnostics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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

class PastMedicalDiagnosticsFragment : Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private val editTextIds =
        intArrayOf(
            R.id.pastMedicalDiagnosticsNameTextView,
            R.id.pastMedicalDiagnosticsBloodPressureTextView,
            R.id.pastMedicalDiagnosticsHeartRateTextView,
            R.id.pastMedicalDiagnosticsPatientTemperatureTextView,
            R.id.pastMedicalDiagnosticsGlucoseLevelTextView,
            R.id.pastMedicalDiagnosticsO2SaturationTextView,
            R.id.pastMedicalDiagnosticsPulseRateTextView,
            R.id.pastMedicalDiagnosticsRespirationRateTextView,
            R.id.pastMedicalDiagnosticsBloodfatLevelTextView,
        )
    private val editButtonIds =
        intArrayOf(
            R.id.past_name_pencil,
            R.id.past_blood_pressure_pencil,
            R.id.past_heart_rate_pencil,
            R.id.past_temperature_pencil,
            R.id.past_glucose_level_pencil,
            R.id.past_blood_o2_saturation_pencil,
            R.id.past_pulse_rate_pencil,
            R.id.past_respiration_rate_pencil,
            R.id.past_bloodfat_level_pencil,
        )

    private lateinit var medicalDiagnosticPast: MedicalDiagnostic
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
            inflater.inflate(R.layout.fragment_past_medical_diagnostics, container, false)
        editTextArray = arrayOfNulls(editTextIds.size)
        editButtonArray = arrayOfNulls(editButtonIds.size)
        for (i in editTextIds.indices) {
            editTextArray[i] = rootView.findViewById(editTextIds[i])
            editButtonArray[i] = rootView.findViewById(editButtonIds[i])
            val editText = editTextArray[i]
            val editButton = editButtonArray[i]
            editButton!!.setOnClickListener { v: View? ->
                editText!!.isFocusable = true
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
            rootView.findViewById(R.id.pastMedicalDiagnosticsHeartRateArrowButton)
        heartRateArrowButton.setOnClickListener {
            Intent(this.context, HeartRateActivity::class.java).also {
                startActivity(it)
            }
        }

        return rootView
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
            query.addListenerForSingleValueEvent(SaveInFirebaseListener())
        }
    }

    private fun dataChecker(): Boolean {
        for (editText in editTextArray) {
            if (TextUtils.isEmpty(editText!!.text)) {
                editText.error = "it shouldn't be empty!"
                return false
            }
        }
        medicalDiagnosticPast =
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
                false,
            )
        return true
    }

    private inner class SaveInFirebaseListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (childSnapshot in snapshot.children) {
                val isCurrent =
                    childSnapshot.child("current").getValue(
                        Boolean::class.java,
                    )
                if (java.lang.Boolean.FALSE == isCurrent) {
                    childSnapshot.ref.setValue(medicalDiagnosticPast)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    }
}
