package com.example.nurselogbook.model


data class NurseTask(
    val id: String,
    val title: String,
    val details: String,
    val patientName: String,
    val time: String,
    var done: Boolean = false
)


