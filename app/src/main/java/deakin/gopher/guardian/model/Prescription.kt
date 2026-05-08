package deakin.gopher.guardian.model

import com.google.gson.annotations.SerializedName

data class Prescription(
    @SerializedName("_id") val id: String,
    @SerializedName("medicineName") val medicineName: String? = null,
    @SerializedName("dosage") val dosage: String? = null,
    @SerializedName("frequency") val frequency: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("instructions") val instructions: String? = null,
)
