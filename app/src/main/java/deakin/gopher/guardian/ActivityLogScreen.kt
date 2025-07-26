package deakin.gopher.guardian

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityLogScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Log") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4BA4E0)),
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_william),
                contentDescription = "Photo",
                modifier =
                    Modifier
                        .size(90.dp)
                        .align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("William S", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_wakeup),
                        contentDescription = "Wake Up",
                        modifier = Modifier.size(24.dp),
                    )
                    Text("Wake-up Time", fontWeight = FontWeight.SemiBold)
                    Text("6:30 AM")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_sleeptime),
                        contentDescription = "Sleep",
                        modifier = Modifier.size(24.dp),
                    )
                    Text("Sleep Time", fontWeight = FontWeight.SemiBold)
                    Text("9:00 PM")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("MEALS", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = R.drawable.icon_meals), contentDescription = "Meals", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("• Breakfast: 1 toast, 1 egg")
                    Text("• Lunch: salad")
                    Text("• Dinner: soup")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("NOTES", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.icon_warning),
                    contentDescription = "Warning",
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lethargy observed", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("BEHAVIOUR", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("• TV")
            Text("• Reading: 1 hour")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.icon_walking),
                    contentDescription = "Walking",
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("• Outdoors: 45 min walking")
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate("doctor_home") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4BA4E0)),
            ) {
                Text("Go to Doctor Home", color = Color.White)
            }
        }
    }
}
