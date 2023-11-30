package deakin.gopher.guardian.view.patient.patientdata.heartrate

sealed class HeartRateRoutes(val route: String) {
    data object HeartRateOverview : HeartRateRoutes("heartRateOverview")
    data object AddHeartRate : HeartRateRoutes("addHeartRate")
}
