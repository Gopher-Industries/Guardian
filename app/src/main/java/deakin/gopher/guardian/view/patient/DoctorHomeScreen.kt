package deakin.gopher.guardian

import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import deakin.gopher.guardian.model.login.SessionManager

@Composable
fun DoctorHomeScreen(navController: NavHostController) {

    val context = LocalContext.current
    val currentUser = SessionManager.getCurrentUser()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White),
    ) {

        // Doctor's photo
        Image(
            painter = painterResource(id = R.drawable.dr_photo),
            contentDescription = "Doctor",
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(220.dp),
        )

        // Doctor's name
        Text(
            text = currentUser.name,
            fontSize = 18.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2),
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 16.dp),
        )

        // Grid of feature cards
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // First row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                HomeCard("Patients", R.drawable.icon_patients) {
                    navController.navigate("patient_report")
                }

                HomeCard("Appointments", R.drawable.icon_appointments) {
                    navController.navigate("appointment")
                }
            }

            // Second row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                HomeCard("Prescriptions", R.drawable.icon_prescription) {
                    navController.navigate("prescription")
                }

                HomeCard("Billing", R.drawable.icon_billing) {
                    navController.navigate("billing")
                }
            }

            // Third row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(
                    onClick = {
                        Toast.makeText(
                            navController.context,
                            "PROFILE CLICKED",
                            Toast.LENGTH_SHORT
                        ).show()

                        navController.navigate("doctor_profile")
                    },
                    modifier = Modifier.size(130.dp)
                ) {
                    Text("PROFILE")
                }

                HomeCard("Sign out", R.drawable.icon_signout) {
                    navController.navigate("sign_out")
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    label: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .size(130.dp)
                .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4BA4E0)
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(36.dp),
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = label,
                fontSize = 13.sp,
                color = Color.White,
            )
        }
    }
}