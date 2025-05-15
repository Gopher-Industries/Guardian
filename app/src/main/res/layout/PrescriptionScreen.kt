package com.example.guardionmonitor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun PrescriptionScreen(navController: NavHostController) {
    var patientName by remember { mutableStateOf("Tarryn N") }
    var date by remember { mutableStateOf("March 30 2025") }
    var medication by remember { mutableStateOf("Amoxicillin") }
    var dosage by remember { mutableStateOf("3/day") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4BA4E0))
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.icon_prescription),
                    contentDescription = "Prescription Icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PRESCRIPTION",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Fields
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            FieldLabel("PATIENT NAME")
            OutlinedTextField(
                value = patientName,
                onValueChange = { patientName = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            FieldLabel("DATE")
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            FieldLabel("MEDICATION")
            OutlinedTextField(
                value = medication,
                onValueChange = { medication = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            FieldLabel("DOSAGE")
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Send button
            Button(
                onClick = { navController.navigate("doctorHome") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4BA4E0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("SEND", color = Color.White)
            }
        }
    }
}

@Composable
fun FieldLabel(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        color = Color.Black
    )
    Spacer(modifier = Modifier.height(4.dp))
}
