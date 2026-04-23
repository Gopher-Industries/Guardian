package deakin.gopher.guardian.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import deakin.gopher.guardian.repository.LogbookRepository
import deakin.gopher.guardian.repository.ApiResult
import deakin.gopher.guardian.model.logbook.LogEntry
import deakin.gopher.guardian.model.logbook.CreateLogRequest
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class LogbookViewModel(
    private val logbookRepository: LogbookRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogbookUiState())
    val uiState: StateFlow<LogbookUiState> = _uiState.asStateFlow()

    private val _selectedPatientId = MutableStateFlow<String?>(null)
    val selectedPatientId: StateFlow<String?> = _selectedPatientId.asStateFlow()

    fun filterLogsByDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val token = getAuthToken()
            if (token.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Please log in to view logs"
                )
                return@launch
            }

            // For now, we'll get all logs and filter client-side
            // In a real implementation, you'd modify your API to support date filtering
            val result = logbookRepository.getLogs(
                token = token,
                patientId = _selectedPatientId.value,
                limit = 100 // Get more logs for filtering
            )

            when (result) {
                is ApiResult.Success -> {
                    val filteredLogs = filterLogsByDateRange(result.data, startDate, endDate)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        logs = filteredLogs,
                        error = null
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is ApiResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    fun filterLogsByDays(days: Int) {
        val calendar = Calendar.getInstance()
        val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        filterLogsByDateRange(startDate, endDate)
    }

    fun filterByToday() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())
        filterLogsByDateRange(today, today)
    }

    fun filterByThisWeek() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val startOfWeek = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, 6)
        val endOfWeek = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        filterLogsByDateRange(startOfWeek, endOfWeek)
    }

    // Helper function to filter logs client-side by date range
    private fun filterLogsByDateRange(logs: List<LogEntry>, startDate: String, endDate: String): List<LogEntry> {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)

            logs.filter { log ->
                try {
                    // Parse the log's createdAt field (assuming ISO format)
                    val logDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    val logDate = logDateFormat.parse(log.createdAt)

                    logDate != null && start != null && end != null &&
                            (logDate.after(start) || logDate == start) &&
                            (logDate.before(end) || logDate == end)
                } catch (e: Exception) {
                    // If date parsing fails, include the log
                    true
                }
            }
        } catch (e: Exception) {
            // If filtering fails, return all logs
            logs
        }
    }

    fun loadLogs(patientId: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val token = getAuthToken()
            if (token.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Please log in to view logs"
                )
                return@launch
            }

            val result = logbookRepository.getLogs(
                token = token,
                patientId = patientId ?: _selectedPatientId.value,
                limit = 50
            )

            when (result) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        logs = result.data,
                        error = null
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is ApiResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    fun loadAllLogs() {
        loadLogs(patientId = null)
    }

    fun createLog(title: String, description: String, patientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, error = null)

            val token = getAuthToken()
            if (token.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    error = "Please log in to create logs"
                )
                return@launch
            }

            val request = CreateLogRequest(
                title = title,
                description = description,
                patient = patientId
            )

            val result = logbookRepository.createLog(token, request)

            when (result) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        error = null
                    )
                    // Refresh the logs to show the new entry
                    loadLogs(_selectedPatientId.value)
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        error = result.message
                    )
                }
                is ApiResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    fun deleteLog(logId: String) {
        viewModelScope.launch {
            val token = getAuthToken()
            if (token.isEmpty()) {
                _uiState.value = _uiState.value.copy(error = "Please log in to delete logs")
                return@launch
            }

            val result = logbookRepository.deleteLog(token, logId)

            when (result) {
                is ApiResult.Success -> {
                    // Remove the log from local state
                    val updatedLogs = _uiState.value.logs.filter { it.id != logId }
                    _uiState.value = _uiState.value.copy(
                        logs = updatedLogs,
                        error = null
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is ApiResult.Loading -> {
                    // Handle loading if needed
                }
            }
        }
    }

    fun setSelectedPatient(patientId: String) {
        _selectedPatientId.value = patientId
        loadLogs(patientId)
    }

    fun clearSelectedPatient() {
        _selectedPatientId.value = null
        loadAllLogs()
    }

    fun refreshLogs() {
        loadLogs(_selectedPatientId.value)
    }

    private fun getAuthToken(): String {
        val sharedPrefs = context.getSharedPreferences("guardian_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("auth_token", "") ?: ""
    }
}

data class LogbookUiState(
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val logs: List<LogEntry> = emptyList(),
    val error: String? = null
)

class LogbookViewModelFactory(
    private val logbookRepository: LogbookRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogbookViewModel::class.java)) {
            return LogbookViewModel(logbookRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}