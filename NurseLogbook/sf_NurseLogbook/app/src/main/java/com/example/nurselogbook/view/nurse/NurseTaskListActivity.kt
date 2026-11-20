package com.example.nurselogbook.view.nurse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class NurseTaskListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply { text = "Nurse Logbook – Task List" })
    }
}
