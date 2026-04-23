package deakin.gopher.guardian.model.logbook

import com.google.gson.annotations.SerializedName
import deakin.gopher.guardian.model.BaseModel

// Request Models
data class CreateLogRequest(
    val title: String,
    val description: String,
    val patient: String // Patient ID
)

// Response Models
data class LogEntry(
    @SerializedName("_id")
    val id: String,
    val title: String,
    val description: String,
    val patient: String,
    val createdBy: LogCreator,
    @SerializedName("createdAt")
    val createdAt: String
)

data class LogCreator(
    val fullname: String,
    val role: String
)

// API Response Wrappers
data class CreateLogResponse(
    val message: String,
    val log: LogEntry
) : BaseModel()

data class LogListResponse(
    val logs: List<LogEntry>? = null,
    val data: List<LogEntry>? = null
) : BaseModel() {
    val actualLogs: List<LogEntry>
        get() = logs ?: data ?: emptyList()
}

data class DeleteLogResponse(
    val message: String = "Log deleted successfully"
) : BaseModel()