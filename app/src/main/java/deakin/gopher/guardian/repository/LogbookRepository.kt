package deakin.gopher.guardian.repository


import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.model.logbook.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogbookRepository {

    private val apiService = ApiClient.apiService

    suspend fun createLog(
        token: String,
        request: CreateLogRequest
    ): ApiResult<LogEntry> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createPatientLog("Bearer $token", request)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        if (apiResponse.apiError != null) {
                            ApiResult.Error(apiResponse.apiError)
                        } else {
                            ApiResult.Success(apiResponse.log)
                        }
                    } ?: ApiResult.Error("Empty response body")
                } else {
                    val errorMessage = try {
                        response.errorBody()?.string() ?: "Unknown error"
                    } catch (e: Exception) {
                        "Network error: ${response.code()}"
                    }
                    ApiResult.Error(errorMessage, response.code())
                }
            } catch (e: Exception) {
                ApiResult.Error(e.message ?: "Network error occurred")
            }
        }
    }

    suspend fun getLogs(
        token: String,
        patientId: String? = null,
        page: Int? = null,
        limit: Int? = null
    ): ApiResult<List<LogEntry>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPatientLogs("Bearer $token", patientId, page, limit)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        if (apiResponse.apiError != null) {
                            ApiResult.Error(apiResponse.apiError)
                        } else {
                            ApiResult.Success(apiResponse.actualLogs)
                        }
                    } ?: ApiResult.Error("Empty response body")
                } else {
                    val errorMessage = try {
                        response.errorBody()?.string() ?: "Unknown error"
                    } catch (e: Exception) {
                        "Network error: ${response.code()}"
                    }
                    ApiResult.Error(errorMessage, response.code())
                }
            } catch (e: Exception) {
                ApiResult.Error(e.message ?: "Network error occurred")
            }
        }
    }

    suspend fun deleteLog(
        token: String,
        logId: String
    ): ApiResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deletePatientLog("Bearer $token", logId)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        if (apiResponse.apiError != null) {
                            ApiResult.Error(apiResponse.apiError)
                        } else {
                            ApiResult.Success(apiResponse.message)
                        }
                    } ?: ApiResult.Success("Log deleted successfully")
                } else {
                    val errorMessage = try {
                        when (response.code()) {
                            403 -> "You don't have permission to delete this log"
                            404 -> "Log not found"
                            else -> response.errorBody()?.string() ?: "Unknown error"
                        }
                    } catch (e: Exception) {
                        "Network error: ${response.code()}"
                    }
                    ApiResult.Error(errorMessage, response.code())
                }
            } catch (e: Exception) {
                ApiResult.Error(e.message ?: "Network error occurred")
            }
        }
    }
}
