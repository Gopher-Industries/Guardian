// Updated TaskRepository.kt with fixed property names
package deakin.gopher.guardian.repository

import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.model.calendar.*
import deakin.gopher.guardian.model.BaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository {

    private val apiService = ApiClient.apiService

    suspend fun createTask(
        token: String,
        request: CreateTaskRequest
    ): ApiResult<TaskResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createTask("Bearer $token", request)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        // Check for API errors using your BaseModel structure
                        if (apiResponse.apiError != null) {
                            ApiResult.Error(apiResponse.apiError)
                        } else {
                            val task = apiResponse.actualTask // Using the new property name
                            if (task != null) {
                                ApiResult.Success(task)
                            } else {
                                ApiResult.Error("No task data in response")
                            }
                        }
                    } ?: ApiResult.Error("Empty response body")
                } else {
                    // Parse error from response body
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

    suspend fun getTasks(
        token: String,
        caretakerId: String,
        filter: String? = null,
        status: String? = null,
        dueDate: String? = null,
        page: Int = 1,
        limit: Int = 50,
        sort: String? = "due_date"
    ): ApiResult<List<TaskResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTasks(
                    "Bearer $token", caretakerId, filter, status, dueDate, page, limit, sort
                )
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        // Check for API errors using your BaseModel structure
                        if (apiResponse.apiError != null) {
                            ApiResult.Error(apiResponse.apiError)
                        } else {
                            val tasks = apiResponse.actualTasks // Using the new property name
                            ApiResult.Success(tasks)
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

    suspend fun updateTaskStatus(
        token: String,
        taskId: String,
        status: String
    ): ApiResult<TaskResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdateTaskStatusRequest(status)
                val response = apiService.updateTaskStatus("Bearer $token", taskId, request)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        if (apiResponse.apiError != null) {
                            ApiResult.Error(apiResponse.apiError)
                        } else {
                            val task = apiResponse.actualTask // Using the new property name
                            if (task != null) {
                                ApiResult.Success(task)
                            } else {
                                ApiResult.Error("No task data in response")
                            }
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

    suspend fun updateTask(
        token: String,
        taskId: String,
        request: UpdateTaskRequest
    ): ApiResult<TaskResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateTask("Bearer $token", taskId, request)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        if (apiResponse.apiError != null) {
                            ApiResult.Error(apiResponse.apiError)
                        } else {
                            val task = apiResponse.actualTask // Using the new property name
                            if (task != null) {
                                ApiResult.Success(task)
                            } else {
                                ApiResult.Error("No task data in response")
                            }
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

    suspend fun deleteTask(token: String, taskId: String): ApiResult<BaseModel> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteTask("Bearer $token", taskId)
                if (response.isSuccessful) {
                    response.body()?.let { baseModel ->
                        if (baseModel.apiError != null) {
                            ApiResult.Error(baseModel.apiError)
                        } else {
                            ApiResult.Success(baseModel)
                        }
                    } ?: run {
                        // If no response body, assume success
                        ApiResult.Success(BaseModel())
                    }
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
}

// ApiResult sealed class (unchanged)
sealed class ApiResult<T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error<T>(val message: String, val code: Int? = null) : ApiResult<T>()
    data class Loading<T>(val message: String = "Loading...") : ApiResult<T>()

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading

    fun getDataOrNull(): T? = if (this is Success) data else null
    fun getErrorMessage(): String? = if (this is Error) message else null
}