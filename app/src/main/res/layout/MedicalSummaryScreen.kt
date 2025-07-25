package com.example.guardionmonitor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun MedicalSummaryScreen(navController: NavHostController, patientName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with image and patient name
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4BA4E0))
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.icon_william),
                    contentDescription = "$patientName photo",
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color.White, CircleShape)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(patientName, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Medical Summary box
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(Color(0xFFE3F2FD), shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            Text("MEDICAL SUMMARY", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Condition: Hypertension")
            Text("Last Visit: 12 March 2025")
            Text("Medicine:")
            Text("  • Amlodipine 5mg - 1/day")
            Text("  • Lisinopril 10mg - 1/day")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Appointment info
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Calendar",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Next Appointment:", fontWeight = FontWeight.SemiBold)
                Text("1 April 2025")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(Color(0xFFE3F2FD), shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            Text("NOTES:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Blood pressure was above normal range during the last visit.")
            Spacer(modifier = Modifier.height(4.dp))
            Text("Medication adherence needs to be monitored closely.")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Assign Nurse + Activity Log Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("assign_nurse") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Assign Nurse")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("activity_log") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Activity Log")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.navigate("patient_report") }
            )
        }
    }
}
