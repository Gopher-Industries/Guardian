@file:Suppress("ktlint")

package deakin.gopher.guardian

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import deakin.gopher.guardian.model.Prescription
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PatientPrescriptionsScreen(
    navController: NavHostController,
    patientId: String,
    patientName: String,
) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var prescriptions by remember { mutableStateOf<List<Prescription>>(emptyList()) }

    LaunchedEffect(patientId) {
        isLoading = true
        errorMessage = null
        try {
            val token = "Bearer ${SessionManager.getToken()}"
            val response =
                withContext(Dispatchers.IO) {
                    ApiClient.apiService.getPatientPrescriptions(token, patientId)
                }
            if (response.isSuccessful && response.body() != null) {
                prescriptions = response.body() ?: emptyList()
            } else {
                errorMessage = "Unable to load prescriptions. Please try again."
            }
        } catch (e: Exception) {
            errorMessage = "Network error while fetching prescriptions."
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier =
                    Modifier
                        .clickable { navController.popBackStack() },
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$patientName Prescriptions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D8C),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage ?: "", color = Color.Red)
                }
            }

            prescriptions.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No prescriptions found for this patient.")
                }
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(prescriptions) { prescription ->
                        PrescriptionRow(
                            prescription = prescription,
                            onClick = {
                                navController.navigate(
                                    "prescription_detail/${prescription.id}/${Uri.encode(patientName)}",
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrescriptionRow(
    prescription: Prescription,
    onClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = prescription.medicineName ?: "Unknown medicine",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Dosage: ${prescription.dosage ?: "-"}")
            Text(text = "Frequency: ${prescription.frequency ?: "-"}")

            val status = prescription.status?.lowercase() ?: "unknown"
            val statusColor =
                if (status == "discontinued") {
                    Color(0xFFC62828)
                } else {
                    Color(0xFF2E7D32)
                }
            Text(
                text = "Status: ${prescription.status ?: "Active"}",
                color = statusColor,
                fontWeight = FontWeight.SemiBold,
            )

            Text(text = "Start: ${prescription.startDate ?: "-"}")
            Text(text = "End: ${prescription.endDate ?: "-"}")
        }
    }
}

@Composable
fun PrescriptionDetailScreen(
    navController: NavHostController,
    prescriptionId: String,
    patientName: String,
) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var prescription by remember { mutableStateOf<Prescription?>(null) }

    LaunchedEffect(prescriptionId) {
        isLoading = true
        errorMessage = null
        try {
            val token = "Bearer ${SessionManager.getToken()}"
            val response =
                withContext(Dispatchers.IO) {
                    ApiClient.apiService.getPrescriptionById(token, prescriptionId)
                }
            if (response.isSuccessful && response.body() != null) {
                prescription = response.body()
            } else {
                errorMessage = "Unable to load prescription details."
            }
        } catch (e: Exception) {
            errorMessage = "Network error while fetching prescription details."
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier =
                    Modifier
                        .clickable { navController.popBackStack() },
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$patientName Prescription Detail",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D8C),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage ?: "", color = Color.Red)
                }
            }

            prescription == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Prescription not found.")
                }
            }

            else -> {
                val item = prescription!!
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Medicine: ${item.medicineName ?: "-"}", fontWeight = FontWeight.Bold)
                    Text(text = "Dosage: ${item.dosage ?: "-"}")
                    Text(text = "Frequency: ${item.frequency ?: "-"}")
                    Text(text = "Status: ${item.status ?: "-"}")
                    Text(text = "Start date: ${item.startDate ?: "-"}")
                    Text(text = "End date: ${item.endDate ?: "-"}")
                    Text(text = "Created: ${item.createdAt ?: "-"}")
                    Text(text = "Updated: ${item.updatedAt ?: "-"}")
                    Text(text = "Instructions: ${item.instructions ?: "-"}")
                }
            }
        }
    }
}
