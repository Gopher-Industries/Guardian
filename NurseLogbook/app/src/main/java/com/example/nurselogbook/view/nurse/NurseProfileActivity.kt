package com.example.nurselogbook.view.nurse

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.nurselogbook.R

class NurseProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nurse_profile)

        // Find the button by ID
        val btnGoToTasks: Button = findViewById(R.id.btnGoToTasks)

        // Set button click listener
        btnGoToTasks.setOnClickListener {
            val intent = Intent(this, NurseTaskListActivity::class.java)
            startActivity(intent)
        }
    }
}
