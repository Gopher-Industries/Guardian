package deakin.gopher.guardian.model

import java.io.Serializable

data class PrescriptionItem(
    val name: String,
    val dose: String,
    val frequency: String,
    val durationDays: Int,
) : Serializable

data class Prescription(
    val id: String? = null,
    val patientId: String,
    val items: List<PrescriptionItem>,
    val notes: String,
) : Serializable
