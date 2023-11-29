package deakin.gopher.guardian.model.health

import java.util.Date
import java.util.UUID

data class HeartRate(
    val heartRateId: UUID,
    val patientId: UUID,
    val measurement: Int,
    val measurementDate: Date,
) {
    companion object {
        fun sortByDate(heartRates: List<HeartRate>): List<HeartRate> {
            return heartRates.sortedBy { it.measurementDate }
        }
    }
}

fun List<HeartRate>.soryByDate(): List<HeartRate> {
    return this.sortedBy { it.measurementDate }
}
