package com.example.guardionmonitor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.guardionmonitor.R

@Composable
fun RoleSelectionScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFF4BA4E0)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_guardian),
                contentDescription = "Logo",
                modifier = Modifier.size(70.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        RoleButton(
            iconId = R.drawable.icon_person,
            label = "PATIENT",
            onClick = { /* navController.navigate("patient_login") */ } // not defined yet
        )

        Spacer(modifier = Modifier.height(20.dp))

        RoleButton(
            iconId = R.drawable.icon_doctor,
            label = "DOCTOR",
            onClick = { navController.navigate("doctor_login") } // FIXED ROUTE
        )

        Spacer(modifier = Modifier.height(20.dp))

        RoleButton(
            iconId = R.drawable.icon_pharmacist,
            label = "PHARMACISTS",
            onClick = { /* navController.navigate("pharmacist_login") */ } // not defined yet
        )
    }
}

@Composable
fun RoleButton(iconId: Int, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(70.dp)
            .background(Color(0xFFB3E5FC), shape = MaterialTheme.shapes.medium)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = "$label Icon",
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D8C)
            )
        }
    }
}
