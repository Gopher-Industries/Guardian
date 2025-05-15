package com.example.guardionmonitor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun DoctorLoginPage(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf(false) }

    val validUsername = "doctor"
    val validPassword = "1234"

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
                contentDescription = "Guardian Logo",
                modifier = Modifier.size(70.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.icon_stethoscope),
            contentDescription = "Stethoscope Icon",
            modifier = Modifier.size(60.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "DOCTOR LOGIN",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF004D8C)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(0.8f),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(0.8f),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Text("Remember me", color = Color(0xFF1976D2))
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (loginError) {
            Text(
                text = "Invalid username or password",
                color = Color.Red,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Button(
            onClick = {
                if (username == validUsername && password == validPassword) {
                    loginError = false
                    navController.navigate("doctor_home") // FIXED ROUTE
                } else {
                    loginError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("Sign in")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Forgot password?",
            fontSize = 14.sp,
            color = Color(0xFF1976D2)
        )
    }
}
