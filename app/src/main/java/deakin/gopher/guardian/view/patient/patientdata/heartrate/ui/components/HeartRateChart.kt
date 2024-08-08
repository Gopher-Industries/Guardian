package deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.components

import android.graphics.Color.parseColor
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import deakin.gopher.guardian.model.health.HeartRate
import deakin.gopher.guardian.model.health.sortByDate
import java.util.Date
import java.util.UUID

@Suppress("ktlint:standard:function-naming")
@Composable
fun HeartRateChart(heartRates: List<HeartRate>) {
    // Sort heart rate data by measurement date
    val sortedHeartRates = heartRates.sortedBy { it.measurementDate }

    // Map heart rate data to chart entries
    val entries =
        sortedHeartRates.mapIndexed { index, heartRate ->
            entryOf(
                // Use timestamp as X-axis value
                x = heartRate.measurementDate.time.toFloat(),
                y = heartRate.measurement.toFloat(),
            )
        }

    val chartEntryModelProducer = ChartEntryModelProducer(entries)
    val heartRateChartLineSpec = mutableListOf<LineChart.LineSpec>()
    val customLineColor = parseColor("#FF0B98C5")
    heartRateChartLineSpec.add(
        LineChart.LineSpec(
            lineColor = customLineColor,
            lineThicknessDp = 1.0F,
            lineBackgroundShader =
                DynamicShaders.fromBrush(
                    brush =
                        Brush.verticalGradient(
                            listOf(
                                Color(customLineColor),
                                Color.White,
                            ),
                        ),
                ),
        ),
    )

    Chart(
        chart = lineChart(lines = heartRateChartLineSpec),
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
