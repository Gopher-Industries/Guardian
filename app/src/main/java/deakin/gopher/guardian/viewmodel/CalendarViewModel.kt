package deakin.gopher.guardian.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import deakin.gopher.guardian.repository.TaskRepository
import deakin.gopher.guardian.repository.ApiResult
import deakin.gopher.guardian.model.calendar.TaskResponse
import deakin.gopher.guardian.util.CalendarUtils
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewModel(
    private val taskRepository: TaskRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val _selectedDate = MutableStateFlow(Calendar.getInstance())
    val selectedDate: StateFlow<Calendar> = _selectedDate.asStateFlow()

    init {
        loadTasksForCurrentWeek()
    }

    fun selectDate(date: Date) {
        _selectedDate.value = Calendar.getInstance().apply { time = date }
        filterTasksForSelectedDate()
    }

    fun navigateWeek(direction: Int) {
        val currentWeek = _selectedDate.value.clone() as Calendar
        currentWeek.add(Calendar.WEEK_OF_YEAR, direction)
        _selectedDate.value = currentWeek
        loadTasksForCurrentWeek()
    }

    fun loadTasksForCurrentWeek() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val sharedPrefs = context.getSharedPreferences("guardian_prefs", Context.MODE_PRIVATE)
                val token = sharedPrefs.getString("auth_token", "") ?: ""
                val caretakerId = sharedPrefs.getString("user_id", "") ?: "68950c034af33273204ee625"

                if (token.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Please log in to view tasks"
                    )
                    return@launch
                }

                val weekStart = CalendarUtils.getWeekStart(_selectedDate.value.time)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val result = taskRepository.getTasks(
                    token = token,
                    caretakerId = caretakerId,
                    dueDate = dateFormat.format(weekStart),
                    limit = 50,
                    sort = "due_date"
                )

                // Handle result using when expression instead of extension functions
                when (result) {
                    is ApiResult.Success -> {
                        val tasks = result.data
                        val weekTasks = tasks.filter { isTaskInCurrentWeek(it) }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            allTasks = weekTasks,
                            error = null
                        )
                        filterTasksForSelectedDate()
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

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    private fun filterTasksForSelectedDate() {
        val selectedDateTasks = _uiState.value.allTasks.filter { task ->
            task.dueDate?.let { dueDate ->
                CalendarUtils.isSameDay(CalendarUtils.parseDateString(dueDate), _selectedDate.value.time)
            } ?: false
        }

        _uiState.value = _uiState.value.copy(filteredTasks = selectedDateTasks)
    }

    fun refreshTasks() {
        loadTasksForCurrentWeek()
    }

    fun markTaskComplete(taskId: String) {
        viewModelScope.launch {
            val sharedPrefs = context.getSharedPreferences("guardian_prefs", Context.MODE_PRIVATE)
            val token = sharedPrefs.getString("auth_token", "") ?: ""

            if (token.isEmpty()) {
                _uiState.value = _uiState.value.copy(error = "Please log in to update tasks")
                return@launch
            }

            val result = taskRepository.updateTaskStatus(token, taskId, "completed")

            when (result) {
                is ApiResult.Success -> {
                    val updatedTask = result.data
                    // Update local state
                    val updatedTasks = _uiState.value.allTasks.map { task ->
                        if (task.id == taskId) {
                            task.copy(status = "completed")
                        } else {
                            task
                        }
                    }
                    _uiState.value = _uiState.value.copy(allTasks = updatedTasks)
                    filterTasksForSelectedDate()
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = "Failed to update task: ${result.message}")
                }
                is ApiResult.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            val sharedPrefs = context.getSharedPreferences("guardian_prefs", Context.MODE_PRIVATE)
            val token = sharedPrefs.getString("auth_token", "") ?: ""

            if (token.isEmpty()) {
                _uiState.value = _uiState.value.copy(error = "Please log in to delete tasks")
                return@launch
            }

            val result = taskRepository.deleteTask(token, taskId)

            when (result) {
                is ApiResult.Success -> {
                    // Remove from local state
                    val updatedTasks = _uiState.value.allTasks.filter { it.id != taskId }
                    _uiState.value = _uiState.value.copy(allTasks = updatedTasks)
                    filterTasksForSelectedDate()
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = "Failed to delete task: ${result.message}")
                }
                is ApiResult.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }

    private fun isTaskInCurrentWeek(task: TaskResponse): Boolean {
        val weekStart = CalendarUtils.getWeekStart(_selectedDate.value.time)
        val weekEnd = CalendarUtils.getWeekEnd(_selectedDate.value.time)

        val taskDate = task.dueDate?.let { CalendarUtils.parseDateString(it) } ?: return false
        return taskDate.after(weekStart) && taskDate.before(weekEnd) ||
                CalendarUtils.isSameDay(taskDate, weekStart) || CalendarUtils.isSameDay(taskDate, weekEnd)
    }
}

data class CalendarUiState(
    val isLoading: Boolean = false,
    val allTasks: List<TaskResponse> = emptyList(),
    val filteredTasks: List<TaskResponse> = emptyList(),
    val error: String? = null
)

// ViewModelFactory for dependency injection
class CalendarViewModelFactory(
    private val taskRepository: TaskRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            return CalendarViewModel(taskRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}