package deakin.gopher.guardian

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import deakin.gopher.guardian.model.login.SessionManager

@Composable
fun DoctorProfileScreen(
    onBack: () -> Unit
) {
    val user = SessionManager.getCurrentUser()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Doctor Profile",
            fontSize = 26.sp,
            color = Color(0xFF1976D2)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Image(
            painter = painterResource(id = R.drawable.dr_photo),
            contentDescription = "Doctor Profile Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = user.name.ifEmpty { "Doctor" },
            fontSize = 22.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        ProfileRow(
            label = "Email",
            value = user.email
        )

        ProfileRow(
            label = "Role",
            value = user.roleName ?: "Doctor"
        )

        ProfileRow(
            label = "User ID",
            value = user.id
        )

        Spacer(modifier = Modifier.height(35.dp))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1976D2)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Back",
                color = Color.White
            )
        }
    }
}

@Composable
private fun ProfileRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Gray
        )

        Text(
            text = value.ifEmpty { "Not available" },
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}