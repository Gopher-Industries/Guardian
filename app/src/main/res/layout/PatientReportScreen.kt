package com.example.guardionmonitor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
fun PatientReportScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.navigate("doctor_home") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PATIENT REPORT",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D8C)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Patient List
        PatientCard(
            imageId = R.drawable.icon_william,
            name = "Williams S",
            age = 60,
            status = "Need medication",
            statusColor = Color.Red,
            background = Color(0xFFFFEBEE),
            onClick = { navController.navigate("medical_summary") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PatientCard(
            imageId = R.drawable.icon_norah,
            name = "Norah P",
            age = 56,
            status = "No Issues",
            statusColor = Color(0xFF4CAF50),
            background = Color(0xFFE3F2FD),
            onClick = { /* navController.navigate("medical_summary") */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PatientCard(
            imageId = R.drawable.icon_serah,
            name = "Serah S",
            age = 56,
            status = "No Issues",
            statusColor = Color(0xFF4CAF50),
            background = Color(0xFFE3F2FD),
            onClick = { /* navController.navigate("medical_summary") */ }
        )
    }
}

@Composable
fun PatientCard(
    imageId: Int,
    name: String,
    age: Int,
    status: String,
    statusColor: Color,
    background: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "$name Photo",
            modifier = Modifier
                .size(60.dp)
                .background(Color.White, shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "Age: $age", fontSize = 14.sp)
            Text(text = status, fontSize = 14.sp, color = statusColor)
        }
    }
}
