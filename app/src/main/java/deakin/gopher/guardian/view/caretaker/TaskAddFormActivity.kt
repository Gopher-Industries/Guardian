package deakin.gopher.guardian.view.caretaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.Task

class TaskAddFormActivity : AppCompatActivity() {

    var tv_Task_Name : EditText?=null
    var tv_Task_Description : EditText?=null
    var btnAdd : Button?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add_form)

        tv_Task_Name = findViewById(R.id.tv_Task_Name)
        tv_Task_Description = findViewById(R.id.tv_Task_Description)
        btnAdd = findViewById<Button>(R.id.btnAdd)

        btnAdd?.setOnClickListener(
            View.OnClickListener { v: View? ->
                insertData()
                clearAll()
            })
    }
    private fun clearAll() {
        tv_Task_Name?.setText("")
        tv_Task_Description?.setText("")

    }
    private fun insertData() {

        var task= Task()
        task.Task_Name=tv_Task_Name?.text.toString()
        task.Description=tv_Task_Description?.text.toString()



        val map: MutableMap<String, Any> = HashMap()
        map["Task_Name"] = tv_Task_Name?.text.toString()
        map["Description"] = tv_Task_Description?.text.toString()

        FirebaseDatabase.getInstance()
            .reference
            .child("Add_Task")
            .push()
            .setValue(map)
            .addOnSuccessListener { unused: Void? ->
                Toast.makeText(this@TaskAddFormActivity, "New Task Added", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e: Exception? ->
                Toast.makeText(
                    this@TaskAddFormActivity, "Error Adding Task", Toast.LENGTH_SHORT
                )
                    .show()
            }
    }


}