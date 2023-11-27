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
import deakin.gopher.guardian.view.theme.GuardianTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartRateInput() {
    var heartRate by remember { mutableStateOf("") }

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

        Button(onClick = { /*TODO*/ }) {
            Text(text = "Save")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HeartRateInputPreview() {
    GuardianTheme {
        HeartRateInput()
    }
}