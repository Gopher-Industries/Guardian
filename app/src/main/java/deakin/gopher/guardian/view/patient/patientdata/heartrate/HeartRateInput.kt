package deakin.gopher.guardian.view.patient.patientdata.heartrate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.date_time.DateTimeDialog
import com.maxkeppeler.sheets.date_time.models.DateTimeSelection
import deakin.gopher.guardian.view.theme.GuardianTheme
import java.time.LocalDateTime

@ExperimentalMaterial3Api
@Composable
fun HeartRateInput() {
    var heartRate by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var dateTimeDialogVisible by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Heart Rate")
        OutlinedTextField(
            value = heartRate,
            onValueChange = { heartRate = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        )

        Text(text = "Reading Date")
        Text(text = selectedDate.toString())
//        OutlinedTextField(
//            value = selectedDate,
//            onValueChange = { selectedDate = it },
//            singleLine = true,
//        )

        DateTimeDialog(
            state = rememberUseCaseState(
                visible = dateTimeDialogVisible,
                onCloseRequest = { /* TODO */ }),
            selection = DateTimeSelection.DateTime { newDate ->
                selectedDate = newDate
            },
        )

        Button(onClick = { /*TODO*/ }) {
            Text(text = "Save")
        }
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HeartRateInputPreview() {
    GuardianTheme {
        HeartRateInput()
    }
}