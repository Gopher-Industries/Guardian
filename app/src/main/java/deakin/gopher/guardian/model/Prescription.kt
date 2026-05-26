package deakin.gopher.guardian.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PrescriptionListResponse(
    @SerializedName("prescriptions") val prescriptions: List<Prescription> = emptyList()
)

data class Prescription(
    @SerializedName("_id") val id: String,

    @SerializedName("patient") val patientId: String? = null,

    @SerializedName("prescriber") val prescriber: PrescriptionPrescriber? = null,

    @SerializedName("items") val items: List<PrescriptionItem> = emptyList(),

    @SerializedName("status") val status: String? = null,

    @SerializedName("createdAt") val createdAt: String? = null,

    @SerializedName("updatedAt") val updatedAt: String? = null
) : Serializable

data class PrescriptionPrescriber(
    @SerializedName("_id") val id: String? = null,

    @SerializedName("fullName") val fullName: String? = null,

    @SerializedName("email") val email: String? = null
) : Serializable

data class PrescriptionItem(
    @SerializedName("name") val name: String,

    @SerializedName("dose") val dose: String,

    @SerializedName("frequency") val frequency: String,

    @SerializedName("durationDays") val durationDays: Int
) : Serializable

data class CreatePrescriptionRequest(
    @SerializedName("patientId") val patientId: String,

    @SerializedName("items") val items: List<PrescriptionItemRequest>
)

data class UpdatePrescriptionRequest(
    @SerializedName("patientId") val patientId: String? = null,

    @SerializedName("items") val items: List<PrescriptionItemRequest>
)

data class PrescriptionItemRequest(
    @SerializedName("name") val name: String,

    @SerializedName("dose") val dose: String,

    @SerializedName("frequency") val frequency: String,

    @SerializedName("durationDays") val durationDays: Int
)