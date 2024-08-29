package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import deakin.gopher.guardian.R

class AddNewPatientActivity : BaseActivity() {
    var userId: String =
        FirebaseAuth.getInstance().getCurrentUser().getUid() // Get the current user's UID
    var name: EditText? = null
    var address: EditText? = null
    var underCare: EditText? = null
    var photo: EditText? = null
    var phone: EditText? = null
    var dob: EditText? = null
    var medicareNo: EditText? = null
    var btnAdd: Button? = null
    var btnBack: Button? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_patient)
        name = findViewById(R.id.txtName)
        address = findViewById(R.id.txtAddress)
        underCare = findViewById(R.id.txtUnderCare)
        phone = findViewById(R.id.txtPhone)
        photo = findViewById(R.id.urlPhoto)
        dob = findViewById(R.id.txtDoB)
        medicareNo = findViewById(R.id.txtMedicareNumber)
        btnAdd = findViewById(R.id.btnAdd)
        btnBack = findViewById(R.id.btnBack)
        btnAdd!!.setOnClickListener { v: View? ->
            insertData()
            clearAll()
        }
        btnBack!!.setOnClickListener { v: View? ->
            // finish();
            onBackPressed()
        }
    }

    private fun insertData() {
        val map: MutableMap<String, Any> = HashMap()
        map["userId"] = userId // Include the userId in the map
        map["name"] = name.getText().toString()
        map["address"] = address.getText().toString()
        map["underCare"] = underCare.getText().toString()
        map["phone"] = phone.getText().toString()
        map["photo"] = photo.getText().toString()
        map["dob"] = dob.getText().toString()
        map["medicareNo"] = medicareNo.getText().toString()
        FirebaseDatabase.getInstance()
            .getReference()
            .child("patient_profile")
            .push()
            .setValue(map)
            .addOnSuccessListener { unused ->
                Toast.makeText(this@AddNewPatientActivity, "New patient added", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this@AddNewPatientActivity, "Error adding patient", Toast.LENGTH_SHORT
                )
                    .show()
            }
    }

    private fun clearAll() {
        name.setText("")
        address.setText("")
        underCare.setText("")
        dob.setText("")
        photo.setText("")
        phone.setText("")
        medicareNo.setText("")
    }
}