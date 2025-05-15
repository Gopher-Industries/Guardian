package com.example.guardionmonitor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
fun AppointmentScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFF4BA4E0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "APPOINTMENTS",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(text = "March 30  2025", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Appointments
        AppointmentItem("Tarryn N", "Now", navController, goToPrescription = true)
        AppointmentItem("Ryan P", "4:00 PM", navController)
        AppointmentItem("Coby T", "4:30 PM", navController)

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
                    .clickable { navController.navigate("doctorHome") }
            )
        }
    }
}

@Composable
fun AppointmentItem(
    name: String,
    time: String,
    navController: NavHostController,
    goToPrescription: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
            .clickable {
                if (goToPrescription) {
                    navController.navigate("prescription")
                }
            }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.icon_person),
                contentDescription = "Person Icon",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = name, fontWeight = FontWeight.Bold)
                Text(text = time, color = Color(0xFF4BA4E0), fontSize = 14.sp)
            }
        }
    }
}
