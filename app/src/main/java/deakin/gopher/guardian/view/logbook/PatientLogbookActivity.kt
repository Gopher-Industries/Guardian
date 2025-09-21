package deakin.gopher.guardian.view.logbook

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.LogbookAdapter
import deakin.gopher.guardian.model.logbook.LogEntry
import deakin.gopher.guardian.repository.LogbookRepository
import deakin.gopher.guardian.view.dialog.LogEntryDialog
import deakin.gopher.guardian.viewmodel.LogbookViewModel
import deakin.gopher.guardian.viewmodel.LogbookViewModelFactory
import kotlinx.coroutines.launch
import java.util.Calendar

class PatientLogbookActivity : AppCompatActivity() {

    private lateinit var viewModel: LogbookViewModel
    private lateinit var logbookRepository: LogbookRepository
    private lateinit var viewModelFactory: LogbookViewModelFactory

    // UI components based on your existing layout
    private lateinit var headerText: TextView
    private lateinit var patientSelectionSpinner: Spinner  // Fixed: changed from patient_spinner
    private lateinit var selectedPatientText: TextView
    private lateinit var logsRecyclerView: RecyclerView
    private lateinit var addLogFab: FloatingActionButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var backButton: ImageButton
    private lateinit var filterButton: ImageButton
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var emptyMessage: TextView
    private lateinit var createFirstLogButton: Button

    private lateinit var logsAdapter: LogbookAdapter
    private var currentPatientId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_logbook)

        // Get patient ID from intent if provided
        currentPatientId = intent.getStringExtra("PATIENT_ID")

        setupDependencies()
        initViews()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Load initial data
        currentPatientId?.let { patientId ->
            viewModel.setSelectedPatient(patientId)
        } ?: viewModel.loadAllLogs()
    }

    private fun setupDependencies() {
        logbookRepository = LogbookRepository()
        viewModelFactory = LogbookViewModelFactory(logbookRepository, this)
        viewModel = ViewModelProvider(this, viewModelFactory)[LogbookViewModel::class.java]
    }

    private fun initViews() {
        // Map to your existing layout IDs
        headerText = findViewById(R.id.header_text)
        patientSelectionSpinner = findViewById(R.id.patient_selection_spinner)
        selectedPatientText = findViewById(R.id.selected_patient_text)
        logsRecyclerView = findViewById(R.id.logs_recycler_view)
        addLogFab = findViewById(R.id.add_log_fab)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        backButton = findViewById(R.id.back_button)
        filterButton = findViewById(R.id.filter_button)
        emptyStateLayout = findViewById(R.id.empty_state_layout)
        emptyMessage = findViewById(R.id.empty_message)
        createFirstLogButton = findViewById(R.id.create_first_log_button)

        headerText.text = "Patient Logs"
        setupPatientSpinner()
    }

    private fun setupPatientSpinner() {
        // For now, you can add patient selection logic here
        // This would typically load from your patient repository
        val patientOptions = arrayOf("All Patients", "Select Patient...")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, patientOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patientSelectionSpinner.adapter = adapter

        patientSelectionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> { // All Patients
                        currentPatientId = null
                        selectedPatientText.visibility = View.GONE
                        viewModel.clearSelectedPatient()
                    }
                    // Add more cases for specific patients
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupRecyclerView() {
        logsAdapter = LogbookAdapter(
            onLogClick = { log -> showLogDetails(log) },
            onDeleteClick = { log -> confirmDeleteLog(log) }
        )
        logsRecyclerView.adapter = logsAdapter
        logsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateLoadingState(state.isLoading)
                updateErrorState(state.error)
                updateLogsDisplay(state.logs)
                updateFabState(state.isCreating)
            }
        }

        lifecycleScope.launch {
            viewModel.selectedPatientId.collect { patientId ->
                currentPatientId = patientId
                updateHeaderForPatient(patientId)
                updateSelectedPatientText(patientId)
            }
        }
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }

        filterButton.setOnClickListener {
            // Implement filter functionality if needed
            showFilterOptions()
        }

        addLogFab.setOnClickListener {
            showCreateLogDialog()
        }

        createFirstLogButton.setOnClickListener {
            showCreateLogDialog()
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshLogs()
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        swipeRefreshLayout.isRefreshing = isLoading
    }

    private fun updateErrorState(error: String?) {
        if (error != null) {
            showErrorMessage(error)
        }
    }

    private fun updateLogsDisplay(logs: List<LogEntry>) {
        if (logs.isEmpty()) {
            logsRecyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
            updateEmptyStateMessage()
        } else {
            logsRecyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
            logsAdapter.updateLogs(logs)
        }
    }

    private fun updateEmptyStateMessage() {
        val message = if (currentPatientId != null) {
            "No logs for this patient"
        } else {
            "No log entries yet"
        }
        emptyMessage.text = message
    }

    private fun updateFabState(isCreating: Boolean) {
        addLogFab.isEnabled = !isCreating
        if (isCreating) {
            // Use a standard Android drawable for loading state
            addLogFab.setImageResource(android.R.drawable.ic_popup_sync)
        } else {
            addLogFab.setImageResource(R.drawable.ic_add)
        }
    }

    private fun updateHeaderForPatient(patientId: String?) {
        headerText.text = if (patientId != null) {
            "Patient Logs"
        } else {
            "All Patient Logs"
        }
    }

    private fun updateSelectedPatientText(patientId: String?) {
        if (patientId != null) {
            selectedPatientText.text = "Logs for Patient: $patientId"
            selectedPatientText.visibility = View.VISIBLE
        } else {
            selectedPatientText.visibility = View.GONE
        }
    }

    private fun showCreateLogDialog() {
        if (currentPatientId == null) {
            // Show patient selection dialog or set a default patient
            showErrorMessage("Please select a patient first or enter a patient ID")
            return
        }

        LogEntryDialog.Companion.show(
            context = this,
            patientId = currentPatientId!!,
            onSave = { title, description, patientId ->
                viewModel.createLog(title, description, patientId)
            }
        )
    }

    private fun showLogDetails(log: LogEntry) {
        LogEntryDialog.Companion.show(
            context = this,
            existingLog = log,
            isViewOnly = true
        )
    }

    private fun confirmDeleteLog(log: LogEntry) {
        AlertDialog.Builder(this)
            .setTitle("Delete Log Entry")
            .setMessage("Are you sure you want to delete this log entry?\n\n\"${log.title}\"")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteLog(log.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showFilterOptions() {
        val options = arrayOf("All Logs", "Recent (Last 7 Days)", "Today", "This Week", "By Date Range")
        AlertDialog.Builder(this)
            .setTitle("Filter Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // All Logs
                        viewModel.loadAllLogs()
                    }
                    1 -> {
                        // Recent (Last 7 Days)
                        filterByRecentDays(7)
                    }
                    2 -> {
                        // Today
                        filterByToday()
                    }
                    3 -> {
                        // This Week
                        filterByThisWeek()
                    }
                    4 -> {
                        // By Date Range
                        showDateRangePicker()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun filterByRecentDays(days: Int) {
        viewModel.filterLogsByDays(days)
        showToastMessage("Showing logs from last $days days")
    }

    private fun filterByToday() {
        viewModel.filterByToday()
        showToastMessage("Showing today's logs")
    }

    private fun filterByThisWeek() {
        viewModel.filterByThisWeek()
        showToastMessage("Showing this week's logs")
    }

    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Pick start date
        DatePickerDialog(this, { _, startYear, startMonth, startDay ->
            val startDate = String.format("%04d-%02d-%02d", startYear, startMonth + 1, startDay)

            // Pick end date
            DatePickerDialog(this, { _, endYear, endMonth, endDay ->
                val endDate = String.format("%04d-%02d-%02d", endYear, endMonth + 1, endDay)

                viewModel.filterLogsByDateRange(startDate, endDate)
                showToastMessage("Showing logs from $startDate to $endDate")

            }, year, month, day).show()

        }, year, month, day).show()
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val EXTRA_PATIENT_ID = "PATIENT_ID"
    }
}