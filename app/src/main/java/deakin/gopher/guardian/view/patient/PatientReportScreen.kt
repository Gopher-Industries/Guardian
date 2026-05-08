@file:Suppress("ktlint")

package deakin.gopher.guardian

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PatientReportScreen(navController: NavHostController) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var patients by remember { mutableStateOf<List<Patient>>(emptyList()) }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        try {
            val token = "Bearer ${SessionManager.getToken()}"
            val doctorId = SessionManager.getCurrentUser().id
            val response =
                withContext(Dispatchers.IO) {
                    ApiClient.apiService.getDoctorPatients(token, doctorId)
                }
            if (response.isSuccessful && response.body() != null) {
                val rawJson = response.body()?.string().orEmpty()
                patients = parsePatientsFromDoctorResponse(rawJson)
            } else {
                errorMessage = "Unable to load patients (HTTP ${response.code()})."
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load patients: ${e.message ?: "Unknown error"}"
        } finally {
            isLoading = false
        }
    }

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

            patients.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No patients found.")
                }
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(patients) { patient ->
                        val hasHealthIssues = patient.healthConditions.isNotEmpty()
                        val status = if (hasHealthIssues) "Need medication" else "No Issues"
                        val statusColor = if (hasHealthIssues) Color.Red else Color(0xFF4CAF50)
                        val background = if (hasHealthIssues) Color(0xFFFFEBEE) else Color(0xFFE3F2FD)
                        PatientCard(
                            imageId = R.drawable.icon_william,
                            name = patient.fullname,
                            age = patient.age,
                            status = status,
                            statusColor = statusColor,
                            background = background,
                            onClick = {
                                navController.navigate(
                                    "patient_prescriptions/${patient.id}/${Uri.encode(patient.fullname)}",
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun parsePatientsFromDoctorResponse(rawJson: String): List<Patient> {
    if (rawJson.isBlank()) return emptyList()

    val gson = Gson()
    val listType = object : TypeToken<List<Patient>>() {}.type
    val root = JsonParser.parseString(rawJson)

    return when {
        root.isJsonArray -> gson.fromJson(rawJson, listType)
        root.isJsonObject -> {
            val jsonObject = root.asJsonObject
            when {
                jsonObject.has("patients") -> gson.fromJson(jsonObject.get("patients"), listType)
                jsonObject.has("data") -> gson.fromJson(jsonObject.get("data"), listType)
                else -> emptyList()
            }
        }
        else -> emptyList()
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