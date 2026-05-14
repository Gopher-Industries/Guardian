package deakin.gopher.guardian.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/** Lenient decoding for GET /admin/patient-overview/{patientId}; unknown fields ignored by Gson. */
data class PatientAdminOverviewResponse(
    @SerializedName("patient") val patient: Patient? = null,
    @SerializedName("summary") val summary: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("overview") val overview: String? = null,
    @SerializedName("carePlanSummary") val carePlanSummary: String? = null,
    @SerializedName("riskLevel") val riskLevel: String? = null,
    @SerializedName("alerts") val alerts: List<String>? = null,
) : Serializable
