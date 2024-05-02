package deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import deakin.gopher.guardian.view.patient.patientdata.heartrate.HeartRateRoutes
import deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.components.HeartRateInput
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database

@ExperimentalMaterial3Api
@Composable
fun AddHeartRateScreen(navController: NavController) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Heart Rate") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(HeartRateRoutes.HeartRateOverview.route) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Return to Heart Rate Overview",
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            HeartRateInput(onSaveHeartRate = { heartRate, date ->
                // Here you would add your Firebase save logic
                // For example:
                val db = Firebase.database.reference
                val key = db.child("heartRates").push().key ?: ""
                val heartRateEntry = mapOf("rate" to heartRate, "date" to date)
                db.child("heartRates").child(key).setValue(heartRateEntry)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Heart rate saved successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to save heart rate", Toast.LENGTH_SHORT).show()
                    }
            })
        }
    }
}