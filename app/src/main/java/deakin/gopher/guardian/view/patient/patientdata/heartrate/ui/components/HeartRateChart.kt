package deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import deakin.gopher.guardian.model.health.HeartRate
import deakin.gopher.guardian.model.health.sortByDate
import java.util.Date
import java.util.UUID

@Suppress("ktlint:standard:function-naming")
@Composable
fun HeartRateChart(heartRates: List<HeartRate>) {
    val getEntries =
        List(heartRates.size) { index ->
            entryOf(
                x = index.toFloat(),
                y = heartRates[index].measurement.toFloat(),
            )
        }

    val chartEntryModelProducer = ChartEntryModelProducer(getEntries)
    Chart(
        chart = lineChart(),
        chartModelProducer = chartEntryModelProducer,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(),
        modifier = Modifier.fillMaxWidth().padding(16.dp),
    )
}

@Suppress("ktlint:standard:function-naming")
@Preview(showBackground = true)
@Composable
fun HeartRateChartPreview() {
    val heartRates =
        listOf(
            HeartRate(
                heartRateId = UUID.randomUUID(),
                patientId = UUID.randomUUID(),
                measurement = 100,
                measurementDate = Date(),
            ),
            HeartRate(
                heartRateId = UUID.randomUUID(),
                patientId = UUID.randomUUID(),
                measurement = 60,
                measurementDate = Date(),
            ),
            HeartRate(
                heartRateId = UUID.randomUUID(),
                patientId = UUID.randomUUID(),
                measurement = 55,
                measurementDate = Date(),
            ),
            HeartRate(
                heartRateId = UUID.randomUUID(),
                patientId = UUID.randomUUID(),
                measurement = 120,
                measurementDate = Date(),
            ),
        ).sortByDate()

    HeartRateChart(heartRates)
}
