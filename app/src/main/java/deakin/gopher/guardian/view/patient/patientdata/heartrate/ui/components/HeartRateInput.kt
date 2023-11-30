package deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import deakin.gopher.guardian.R
import deakin.gopher.guardian.view.theme.GuardianTheme

@ExperimentalMaterial3Api
@Suppress("ktlint:standard:function-naming")
@Composable
fun HeartRateInput() {
    var heartRate by remember { mutableStateOf("") }
    var selectedDate = rememberDatePickerState()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.add_heart_rate_title),
            style = MaterialTheme.typography.displaySmall,
        )
        OutlinedTextField(
            value = heartRate,
            onValueChange = { heartRate = it },
            label = {
                Text(text = stringResource(R.string.heart_rate_hint))
            },
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        )

        DatePicker(
            state = selectedDate,
            modifier =
                Modifier
                    .padding(16.dp),
        )

        Button(onClick = { /*TODO*/ }) {
            Text(text = "Save")
        }
    }
}

@ExperimentalMaterial3Api
@Suppress("ktlint:standard:function-naming")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HeartRateInputPreview() {
    GuardianTheme {
        HeartRateInput()
    }
}
