package deakin.gopher.guardian.view.general

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.databinding.ActivityAddPatientActivityBinding
import deakin.gopher.guardian.model.ApiErrorResponse
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
            "Other"
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
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        binding.txtDate.setText(dateFormat.format(calendar.time))
        binding.txtTime.setText(timeFormat.format(calendar.time))

        // Date picker dialog
        binding.txtDate.setOnClickListener {
            val dpd =
                DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        binding.txtDate.setText(dateFormat.format(calendar.time))
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                )
            dpd.datePicker.maxDate = System.currentTimeMillis()
            dpd.show()
        }

        // Time picker dialog
        binding.txtTime.setOnClickListener {
            val tpd =
                TimePickerDialog(this, { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.txtTime.setText(
                        String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            hour,
                            minute,
                        ),
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            tpd.show()
        }

        binding.txtReporter.setText(SessionManager.getCurrentUser().name)

        binding.btnSave.setOnClickListener {
            savePatientActivity()
        }
    }

    private fun savePatientActivity() {
        val activityType =
            if (binding.txtActivityType.text.toString().trim() == "Other") {
                binding.txtOtherActivity.text.toString().trim()
            } else {
                binding.txtActivityType.text.toString().trim()
            }

        if (activityType.trim().isEmpty()) {
            showMessage(getString(R.string.validation_empty_activity_type))
            return
        }

        val date = binding.txtDate.text.toString()
        val time = binding.txtTime.text.toString()
        // Build ISO timestamp
        val isoTimestamp = "${date}T$time:00Z"

        val comments = binding.txtComment.text.toString().trim()

        val patientId = intent.getStringExtra("patientId").orEmpty()
        val token = "Bearer ${SessionManager.getToken()}"
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.progressBar.show()
                binding.btnSave.visibility = View.GONE
            }

            val response =
                ApiClient.apiService.logPatientActivity(
                    token,
                    patientId,
                    activityType,
                    isoTimestamp,
                    comments,
                )

            withContext(Dispatchers.Main) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.hide()
                    binding.btnSave.visibility = View.VISIBLE
                }
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        showMessage(response.body()?.apiMessage ?: response.message())
                        onBackPressedDispatcher.onBackPressed()
                    } else {
                        showMessage(response.body()?.apiError ?: "Failed to add patient activity")
                    }
                } else {
                    // Handle error
                    val errorResponse =
                        Gson().fromJson(
                            response.errorBody()?.string(),
                            ApiErrorResponse::class.java,
                        )
                    showMessage(errorResponse.apiError ?: response.message())
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
