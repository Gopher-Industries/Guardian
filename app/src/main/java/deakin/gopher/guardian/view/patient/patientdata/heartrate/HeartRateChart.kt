package deakin.gopher.guardian.view.patient.patientdata.heartrate

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import deakin.gopher.guardian.model.health.HeartRate
import java.util.Date
import java.util.UUID

@Composable
fun HeartRateChart(heartRates: List<HeartRate>) {
    val getEntries = List(heartRates.size) { index ->
        entryOf(
            x = index.toFloat(),
            y = heartRates[index].measurement.toFloat()
        )
    }

    val chartEntryModelProducer = ChartEntryModelProducer(getEntries)

    Chart(
        chart = lineChart(),
        chartModelProducer = chartEntryModelProducer,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(),
    )
//
//    Chart(
//        chart = lineChart(),
//        model = chartEntryModel,
//        startAxis = rememberStartAxis(
//            title = "Heart Rate",
//            label = axisLabelComponent(
//                color = Color.Green
//            ),
//        ),
//        bottomAxis = rememberBottomAxis(),
//
//        )

//    val data = listOf(
//        "2022-07-01" to 2f,
//        "2022-07-02" to 6f,
//        "2022-07-04" to 4f
//    ).associate { (dateString, yValue) ->
//        LocalDate.parse(dateString) to yValue
//    }
//
//    val points: List<Point> = heartRates.mapIndexed { index, heartRate ->
//        Point(index.toFloat(), heartRate.measurement.toFloat())
//    }
//
//    val xAxisData = AxisData.Builder()
//        .axisStepSize(100.dp)
//        .backgroundColor(MaterialTheme.colorScheme.background)
//        .steps(points.size - 1)
//        .labelData { i -> i.toString() }
//        .labelAndAxisLinePadding(15.dp)
//        .build()
//
//    val yAxisData = AxisData.Builder()
//        .backgroundColor(MaterialTheme.colorScheme.primary)
//        .labelAndAxisLinePadding(20.dp)
//        .labelData { i -> i.toString() }
//        .build()
//
//    val lineChartData = LineChartData(
//        linePlotData = LinePlotData(
//            lines = listOf(
//                Line(
//                    dataPoints = points,
//                    LineStyle(),
//                    IntersectionPoint(),
//                    SelectionHighlightPoint(),
//                    ShadowUnderLine(),
//                    SelectionHighlightPopUp()
//                )
//            )
//        ),
//        xAxisData = xAxisData,
//        yAxisData = yAxisData,
//        gridLines = GridLines(),
//        backgroundColor = MaterialTheme.colorScheme.background
//    )
//
//    LineChart(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(300.dp), lineChartData = lineChartData
//    )
//    val xValuesToDates = data.keys.associateBy { it.toEpochDay().toFloat() }
//    val dateEntryModel = entryModelOf(xValuesToDates.keys.zip(data.values, ::entryOf))
//    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")
//    val horizontalAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal> { value, _ ->
//        (xValuesToDates[value] ?: LocalDate.ofEpochDay(value.toLong())).format(dateTimeFormatter)
//    }
//
//    horizontalAxisValueFormatter.formatValue()
//
//    Chart(
//        chart = lineChart(),
//        model = dateEntryModel,
//        startAxis = rememberStartAxis(
//            title = "Heart Rate",
//            label = axisLabelComponent(
//                color = Color.Green
//            ),
//        ),
//        bottomAxis = rememberBottomAxis(
//            valueFormatter = horizontalAxisValueFormatter
//        ),
//
//        )
}

@Preview(showBackground = true)
@Composable
fun HeartRateChartPreview() {
    val heartRates = listOf(
        HeartRate(
            heartRateId = UUID.randomUUID(),
            patientId = UUID.randomUUID(),
            measurement = 100,
            measurementDate = Date()
        ),
        HeartRate(
            heartRateId = UUID.randomUUID(),
            patientId = UUID.randomUUID(),
            measurement = 60,
            measurementDate = Date()
        ),
        HeartRate(
            heartRateId = UUID.randomUUID(),
            patientId = UUID.randomUUID(),
            measurement = 55,
            measurementDate = Date()
        ),
    )
    HeartRateChart(heartRates)
}