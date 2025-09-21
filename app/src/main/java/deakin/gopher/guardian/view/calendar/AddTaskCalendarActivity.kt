package deakin.gopher.guardian.view.calendar

import deakin.gopher.guardian.model.calendar.UpdateTaskRequest


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.WeekCalendarAdapter
import deakin.gopher.guardian.adapter.TimeSlotAdapter
import deakin.gopher.guardian.adapter.CalendarGridAdapter
import deakin.gopher.guardian.model.calendar.CreateTaskRequest
import deakin.gopher.guardian.model.calendar.TaskResponse
import deakin.gopher.guardian.repository.TaskRepository
import deakin.gopher.guardian.repository.ApiResult
import deakin.gopher.guardian.util.CalendarUtils
import java.text.SimpleDateFormat
import java.util.*

class AddTaskCalendarActivity : AppCompatActivity() {

    // Calendar components
    private lateinit var weekRecyclerView: RecyclerView
    private lateinit var timeSlotRecyclerView: RecyclerView
    private lateinit var calendarGridRecyclerView: RecyclerView
    private lateinit var weekTitleText: TextView
    private lateinit var backButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var todayButton: Button
    private lateinit var previousWeekButton: ImageButton
    private lateinit var nextWeekButton: ImageButton

    private lateinit var weekAdapter: WeekCalendarAdapter
    private lateinit var timeSlotAdapter: TimeSlotAdapter
    private lateinit var calendarGridAdapter: CalendarGridAdapter

    private var currentWeekStart = Calendar.getInstance()
    private var selectedDate: Date? = null
    private var selectedTime: String? = null

    // Task form components
    private lateinit var taskFormScroll: ScrollView
    private lateinit var selectedTimeDisplay: TextView
    private lateinit var taskTitleEditText: EditText
    private lateinit var taskDescriptionEditText: EditText
    private lateinit var priorityRadioGroup: RadioGroup
    private lateinit var patientIdEditText: EditText

    private lateinit var taskRepository: TaskRepository
    private var isEditMode = false
    private var editingTaskId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task_calendar)

        taskRepository = TaskRepository()

        checkEditMode()
        initViews()
        setupRecyclerViews()
        setupClickListeners()
        loadCurrentWeek()
    }

    private fun checkEditMode() {
        isEditMode = intent.getBooleanExtra("EDIT_MODE", false)
        if (isEditMode) {
            editingTaskId = intent.getStringExtra("TASK_ID")
            // Pre-populate form with existing task data
            populateFormForEdit()
        }
    }

    private fun populateFormForEdit() {
        intent.getStringExtra("TASK_TITLE")?.let { title ->
            // We'll populate the form after views are initialized
        }
    }

    private fun initViews() {
        weekRecyclerView = findViewById(R.id.week_header_recycler_view)
        timeSlotRecyclerView = findViewById(R.id.time_slot_recycler_view)
        calendarGridRecyclerView = findViewById(R.id.calendar_grid_recycler_view)
        weekTitleText = findViewById(R.id.week_title_text)
        backButton = findViewById(R.id.back_button)
        saveButton = findViewById(R.id.save_button)
        todayButton = findViewById(R.id.today_button)
        previousWeekButton = findViewById(R.id.previous_week_button)
        nextWeekButton = findViewById(R.id.next_week_button)

        taskFormScroll = findViewById(R.id.task_form_scroll)
        selectedTimeDisplay = findViewById(R.id.selected_time_display)
        taskTitleEditText = findViewById(R.id.task_title_edit_text)
        taskDescriptionEditText = findViewById(R.id.task_description_edit_text)
        priorityRadioGroup = findViewById(R.id.priority_radio_group)
        patientIdEditText = findViewById(R.id.patient_id_edit_text)

        // If in edit mode, populate the form
        if (isEditMode) {
            populateEditForm()
        }
    }

    private fun populateEditForm() {
        intent.getStringExtra("TASK_TITLE")?.let { taskTitleEditText.setText(it) }
        intent.getStringExtra("TASK_DESCRIPTION")?.let { taskDescriptionEditText.setText(it) }
        intent.getStringExtra("TASK_PRIORITY")?.let { priority ->
            when (priority.lowercase()) {
                "low" -> priorityRadioGroup.check(R.id.radio_priority_low)
                "medium" -> priorityRadioGroup.check(R.id.radio_priority_medium)
                "high" -> priorityRadioGroup.check(R.id.radio_priority_high)
            }
        }
    }

    private fun setupRecyclerViews() {
        weekAdapter = WeekCalendarAdapter(getWeekDays()) { date -> }
        weekRecyclerView.adapter = weekAdapter
        weekRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        timeSlotAdapter = TimeSlotAdapter(generateTimeSlots())
        timeSlotRecyclerView.adapter = timeSlotAdapter
        timeSlotRecyclerView.layoutManager = LinearLayoutManager(this)

        calendarGridAdapter = CalendarGridAdapter(
            weekDays = getWeekDays(),
            timeSlots = generateTimeSlots(),
            onSlotClick = { date, time -> onTimeSlotSelected(date, time) }
        )
        calendarGridRecyclerView.adapter = calendarGridAdapter
        calendarGridRecyclerView.layoutManager = GridLayoutManager(this, 7)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }
        previousWeekButton.setOnClickListener { navigateWeek(-1) }
        nextWeekButton.setOnClickListener { navigateWeek(1) }
        todayButton.setOnClickListener {
            currentWeekStart = Calendar.getInstance()
            loadCurrentWeek()
        }
        saveButton.setOnClickListener { saveTask() }
    }

    private fun getWeekDays(): List<Date> {
        val days = mutableListOf<Date>()
        val calendar = currentWeekStart.clone() as Calendar
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        repeat(7) {
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

    private fun generateTimeSlots(): List<String> {
        val slots = mutableListOf<String>()
        for (hour in 6..22) {
            slots.add(String.format("%02d:00", hour))
            slots.add(String.format("%02d:30", hour))
        }
        return slots
    }

    private fun navigateWeek(direction: Int) {
        currentWeekStart.add(Calendar.WEEK_OF_YEAR, direction)
        loadCurrentWeek()
    }

    private fun loadCurrentWeek() {
        weekAdapter.updateDays(getWeekDays())
        calendarGridAdapter.updateWeekDays(getWeekDays())
        updateWeekTitle()
    }

    private fun updateWeekTitle() {
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val weekStart = getWeekDays().first()
        val weekEnd = getWeekDays().last()
        weekTitleText.text = "${dateFormat.format(weekStart)} - ${dateFormat.format(weekEnd)}, ${SimpleDateFormat("yyyy", Locale.getDefault()).format(weekEnd)}"
    }

    private fun onTimeSlotSelected(date: Date, time: String) {
        selectedDate = date
        selectedTime = time

        // Show task form
        taskFormScroll.visibility = View.VISIBLE

        // Update selected time display
        val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
        selectedTimeDisplay.text = "📅 ${dateFormat.format(date)} at $time"
    }

    private fun saveTask() {
        if (!validateForm()) return

        val sharedPrefs = getSharedPreferences("guardian_prefs", MODE_PRIVATE)
        val token = sharedPrefs.getString("auth_token", "") ?: ""
        val caretakerId = sharedPrefs.getString("user_id", "") ?: "68950c034af33273204ee625"

        if (token.isEmpty()) {
            showErrorDialog("Please log in to create tasks")
            return
        }

        val title = taskTitleEditText.text.toString().trim()
        val description = taskDescriptionEditText.text.toString().trim()
        val patientId = patientIdEditText.text.toString().trim()
        val priority = getSelectedPriority()

        val dueDate = if (selectedDate != null && selectedTime != null) {
            buildDueDateTime(selectedDate!!, selectedTime!!)
        } else {
            CalendarUtils.formatDateForApi(Date()) // Default to now if no selection
        }

        val taskRequest = CreateTaskRequest(
            title = title,
            description = if (description.isNotEmpty()) description else title,
            priority = priority,
            status = "pending",
            dueDate = dueDate,
            caretaker = caretakerId,
            patientId = if (patientId.isNotEmpty()) patientId else null
        )

        // Show loading
        saveButton.isEnabled = false
        saveButton.text = "Saving..."

        lifecycleScope.launch {
            val result = if (isEditMode && editingTaskId != null) {
                // Update existing task
                taskRepository.updateTask(token, editingTaskId!!, taskRequest.toUpdateRequest())
            } else {
                // Create new task
                taskRepository.createTask(token, taskRequest)
            }

            saveButton.isEnabled = true
            saveButton.text = "Save"

            when (result) {
                is ApiResult.Success -> {
                    val message = if (isEditMode) "Task updated successfully!" else "Task created successfully!"
                    Toast.makeText(this@AddTaskCalendarActivity, "✅ $message", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is ApiResult.Error -> {
                    showErrorDialog("Failed to save task: ${result.message}")
                }
                is ApiResult.Loading -> {
                    // Handled above
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        if (taskTitleEditText.text.toString().trim().isEmpty()) {
            taskTitleEditText.error = "Task title is required"
            return false
        }

        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun getSelectedPriority(): String {
        return when (priorityRadioGroup.checkedRadioButtonId) {
            R.id.radio_priority_high -> "high"
            R.id.radio_priority_low -> "low"
            else -> "medium"
        }
    }

    private fun buildDueDateTime(selectedDate: Date, selectedTime: String): String {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate  // Set the date

        // Parse and set the time
        val timeParts = selectedTime.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
        calendar.set(Calendar.MINUTE, timeParts[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return CalendarUtils.formatDateForApi(calendar.time)
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}

// Extension function to convert CreateTaskRequest to UpdateTaskRequest
private fun CreateTaskRequest.toUpdateRequest() = UpdateTaskRequest(
    title = this.title,
    description = this.description,
    priority = this.priority,
    status = this.status,
    dueDate = this.dueDate,
    patientId = this.patientId
)