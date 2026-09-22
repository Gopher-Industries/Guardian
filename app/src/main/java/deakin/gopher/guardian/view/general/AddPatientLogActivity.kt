package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import deakin.gopher.guardian.databinding.ActivityAddPatientActivityBinding
import deakin.gopher.guardian.model.CreatePatientLogRequest
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.view.hide
import deakin.gopher.guardian.view.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddPatientLogActivity : BaseActivity() {
    private lateinit var binding: ActivityAddPatientActivityBinding
    private val activityTypes =
        listOf(
            "Woke up",
            "Ate",
            "Took Medication",
            "Watched TV",
            "Reading",
            "Went outdoors",
            "Meditation",
            "Other",
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPatientActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, activityTypes)
        binding.txtActivityType.setAdapter(adapter)

        // Show/hide "Other" input
        binding.txtActivityType.setOnItemClickListener { parent, _, position, _ ->
            val selectedActivity = parent.getItemAtPosition(position).toString()
            binding.txtOtherActivityLayout.visibility =
                if (selectedActivity == "Other") View.VISIBLE else View.GONE
        }

        // Set default date and time
        // Keep current date/time visible but prevent editing
        binding.txtDate.isFocusable = false
        binding.txtDate.isClickable = false

        binding.txtTime.isFocusable = false
        binding.txtTime.isClickable = false

// Live updating current date and time
        val handler = android.os.Handler(mainLooper)

        val updateTimeRunnable =
            object : Runnable {
                override fun run() {
                    val currentCalendar = Calendar.getInstance()

                    val currentDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(currentCalendar.time)

                    val currentTime =
                        SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            .format(currentCalendar.time)

                    binding.txtDate.setText(currentDate)
                    binding.txtTime.setText(currentTime)

                    handler.postDelayed(this, 1000)
                }
            }

        handler.post(updateTimeRunnable)

        binding.txtReporter.setText(SessionManager.getCurrentUser().name)

        binding.btnSave.setOnClickListener {
            savePatientActivity()
        }
    }

    private fun savePatientActivity() {
        val title =
            if (binding.txtActivityType.text.toString().trim() == "Other") {
                binding.txtOtherActivity.text.toString().trim()
            } else {
                binding.txtActivityType.text.toString().trim()
            }

        val description = binding.txtComment.text.toString().trim()

        if (title.isEmpty()) {
            showMessage("Please enter title")
            return
        }

        if (description.isEmpty()) {
            showMessage("Please enter description")
            return
        }

        val patientId = intent.getStringExtra("patientId").orEmpty()

        val request =
            CreatePatientLogRequest(
                patient = patientId,
                title = title,
                description = description,
            )

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.progressBar.show()
                binding.btnSave.visibility = View.GONE
            }

            try {
                val response =
                    ApiClient.apiService.createPatientLog(
                        "Bearer ${SessionManager.getToken()}",
                        request,
                    )

                withContext(Dispatchers.Main) {
                    binding.progressBar.hide()
                    binding.btnSave.visibility = View.VISIBLE

                    if (response.isSuccessful) {
                        showMessage("Log created successfully")

                        finish()
                    } else {
                        showMessage(
                            response.errorBody()?.string() ?: "Failed to create log",
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.hide()
                    binding.btnSave.visibility = View.VISIBLE

                    showMessage(e.message ?: "Unknown error")
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
