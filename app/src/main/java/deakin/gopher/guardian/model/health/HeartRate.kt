package deakin.gopher.guardian.model.health

import java.util.Date
import java.util.UUID

data class HeartRate(
    val heartRateId: UUID,
    val patientId: UUID,
    val measurement: Int,
    val measurementDate: Date,
)
