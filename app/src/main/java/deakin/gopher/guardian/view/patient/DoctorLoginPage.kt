@file:Suppress("ktlint")

package deakin.gopher.guardian

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Checkbox
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun DoctorLoginPage(navController: NavHostController) {
//    var username by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var rememberMe by remember { mutableStateOf(false) }
//    var loginError by remember { mutableStateOf(false) }

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

        val username = null
//        OutlinedTextField(
//            value = username,
//            onValueChange = {
//                val it = null
//                username = it
//            },
//            label = { Text("Username") },
//            modifier = Modifier.fillMaxWidth(0.8f),
//            singleLine = true
//        )

        Spacer(modifier = Modifier.height(10.dp))

        var password = null
        OutlinedTextField(
            value = password,
            onValueChange = {
                val it = null
                password = it
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(0.8f),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            var rememberMe = false
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Text("Remember me", color = Color(0xFF1976D2))
        }

        Spacer(modifier = Modifier.height(10.dp))

        var loginError = false
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

fun OutlinedTextField(value: Nothing?, onValueChange: () -> Unit, label: @Composable () -> Unit, modifier: Modifier, singleLine: Boolean) {

}