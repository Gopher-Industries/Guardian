package deakin.gopher.guardian.view.calendar


import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.WeekDaysAdapter
import deakin.gopher.guardian.adapter.TaskEventsAdapter
import deakin.gopher.guardian.model.calendar.TaskResponse
import deakin.gopher.guardian.repository.TaskRepository
import deakin.gopher.guardian.viewmodel.CalendarViewModel


import deakin.gopher.guardian.util.CalendarUtils
import deakin.gopher.guardian.view.dialog.TaskDetailDialog
import deakin.gopher.guardian.viewmodel.CalendarViewModelFactory
import java.util.*

class GuardianCalendarActivity : AppCompatActivity() {

    private lateinit var viewModel: CalendarViewModel
    private lateinit var taskRepository: TaskRepository
    private lateinit var viewModelFactory: CalendarViewModelFactory

    // UI components
    private lateinit var headerText: TextView
    private lateinit var timeText: TextView
    private lateinit var weekRecyclerView: RecyclerView
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var addTaskFab: FloatingActionButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var prevWeekButton: ImageButton
    private lateinit var nextWeekButton: ImageButton
    private lateinit var backButton: ImageButton

    private lateinit var weekAdapter: WeekDaysAdapter
    private lateinit var eventsAdapter: TaskEventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardian_calendar)

        setupDependencies()
        initViews()
        setupRecyclerViews()
        setupObservers()
        setupClickListeners()
    }

    private fun setupDependencies() {
        taskRepository = TaskRepository()
        viewModelFactory = CalendarViewModelFactory(taskRepository, this)
        viewModel = ViewModelProvider(this, viewModelFactory)[CalendarViewModel::class.java]
    }

    private fun initViews() {
        headerText = findViewById(R.id.header_text)
        timeText = findViewById(R.id.time_text)
        weekRecyclerView = findViewById(R.id.week_recycler_view)
        eventsRecyclerView = findViewById(R.id.events_recycler_view)
        addTaskFab = findViewById(R.id.add_task_fab)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        prevWeekButton = findViewById(R.id.prev_week_button)
        nextWeekButton = findViewById(R.id.next_week_button)
        backButton = findViewById(R.id.back_button)
    }

    private fun setupRecyclerViews() {
        weekAdapter = WeekDaysAdapter(emptyList()) { date ->
            viewModel.selectDate(date)
        }
        weekRecyclerView.adapter = weekAdapter
        weekRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        eventsAdapter = TaskEventsAdapter(
            onTaskClick = { task ->
                TaskDetailDialog.show(
                    context = this,
                    task = task,
                    onEdit = { editTask(it) },
                    onMarkComplete = { taskId -> viewModel.markTaskComplete(taskId) }
                )
            },
            onTaskComplete = { taskId -> viewModel.markTaskComplete(taskId) },
            onTaskEdit = { task -> editTask(task) }
        )
        eventsRecyclerView.adapter = eventsAdapter
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                swipeRefreshLayout.isRefreshing = state.isLoading

                if (state.error != null) {
                    showErrorMessage(state.error)
                }

                eventsAdapter.updateTasks(state.filteredTasks)
            }
        }

        lifecycleScope.launch {
            viewModel.selectedDate.collect { calendar ->
                updateHeaderText(calendar.time)
                updateWeekDays(calendar.time)
            }
        }
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        prevWeekButton.setOnClickListener {
            viewModel.navigateWeek(-1)
        }

        nextWeekButton.setOnClickListener {
            viewModel.navigateWeek(1)
        }

        addTaskFab.setOnClickListener {
            val intent = Intent(this, AddTaskCalendarActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_TASK)
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshTasks()
        }
    }

    private fun updateHeaderText(selectedDate: Date) {
        val isToday = CalendarUtils.isToday(selectedDate)
        headerText.text = if (isToday) "Today" else CalendarUtils.formatDateHeader(selectedDate)

        timeText.text = CalendarUtils.formatTime(Date())
    }

    private fun updateWeekDays(selectedDate: Date) {
        val weekDays = CalendarUtils.getWeekDays(selectedDate)
        weekAdapter.updateDays(weekDays)
    }

    private fun editTask(task: TaskResponse) {
        val intent = Intent(this, AddTaskCalendarActivity::class.java).apply {
            putExtra("EDIT_MODE", true)
            putExtra("TASK_ID", task.id)
            putExtra("TASK_TITLE", task.title)
            putExtra("TASK_DESCRIPTION", task.description)
            putExtra("TASK_PRIORITY", task.priority)
            putExtra("TASK_DUE_DATE", task.dueDate)
        }
        startActivityForResult(intent, REQUEST_EDIT_TASK)
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_ADD_TASK || requestCode == REQUEST_EDIT_TASK) && resultCode == RESULT_OK) {
            viewModel.refreshTasks()
        }
    }

    companion object {
        private const val REQUEST_ADD_TASK = 1001
        private const val REQUEST_EDIT_TASK = 1002
    }
}
