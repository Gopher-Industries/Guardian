package deakin.gopher.guardian.view.general

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.BuildConfig
import deakin.gopher.guardian.databinding.ActivityPatientOverviewBinding
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.MockPatientData
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.PatientAdminOverviewResponse
import deakin.gopher.guardian.model.login.Role
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class PatientOverviewActivity : BaseActivity() {
    private lateinit var binding: ActivityPatientOverviewBinding
    private var patientIdArg: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (SessionManager.getCurrentUser().role == Role.Nurse) {
            binding.toolbar.setBackgroundColor(getColor(R.color.TG_blue))
        }

        patientIdArg = intent.getStringExtra(EXTRA_PATIENT_ID)?.trim()
        if (patientIdArg.isNullOrEmpty()) {
            Toast.makeText(this, R.string.patient_overview_missing_id, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnRetry.setOnClickListener { loadOverview() }
        loadOverview()
    }

    private fun loadOverview() {
        val patientId = patientIdArg ?: return
        setLoadingState(true)
        binding.layoutError.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
        binding.svOverviewContent.visibility = View.GONE
        resetAdminSection()

        if (BuildConfig.USE_MOCK_PATIENT_FLOW) {
            lifecycleScope.launch {
                val patient = MockPatientData.patientForOverviewOrDefault(patientId)
                setLoadingState(false)
                binding.svOverviewContent.visibility = View.VISIBLE
                bindPatientDetails(patient)
                applyAdminOverview(MockPatientData.adminOverviewForPatient(patientId))
            }
            return
        }

        lifecycleScope.launch {
            val token = "Bearer ${SessionManager.getToken()}"

            val patientResponse =
                withContext(Dispatchers.IO) {
                    try {
                        ApiClient.apiService.getPatientById(token, patientId)
                    } catch (_: Exception) {
                        null
                    }
                }

            if (patientResponse == null) {
                setLoadingState(false)
                showError(getString(R.string.patient_overview_error_generic))
                return@launch
            }

            if (!patientResponse.isSuccessful) {
                setLoadingState(false)
                showError(resolveErrorMessage(patientResponse))
                return@launch
            }

            val patient = patientResponse.body()
            if (patient == null) {
                setLoadingState(false)
                showEmpty()
                return@launch
            }

            if (patient.id != patientId) {
                setLoadingState(false)
                showError(getString(R.string.patient_overview_error_generic))
                return@launch
            }

            val adminResponse =
                withContext(Dispatchers.IO) {
                    try {
                        ApiClient.apiService.getPatientAdminOverview(token, patientId)
                    } catch (_: Exception) {
                        null
                    }
                }

            setLoadingState(false)

            binding.svOverviewContent.visibility = View.VISIBLE
            bindPatientDetails(patient)

            if (adminResponse?.isSuccessful == true) {
                adminResponse.body()?.let { applyAdminOverview(it) }
            }
        }
    }

    private fun bindPatientDetails(patient: Patient) {
        binding.tvPatientName.text = patient.fullname
        binding.tvOverviewAge.text = getString(R.string.patient_overview_age_line, patient.age)
        binding.tvOverviewGender.text =
            getString(R.string.patient_overview_gender_line, patient.gender.capitalizeWords())
        binding.tvOverviewDob.text =
            getString(
                R.string.patient_overview_dob_line,
                patient.dateOfBirth.substringBefore("T").ifEmpty { "—" },
            )

        binding.tvOverviewConditions.text =
            if (patient.healthConditions.isEmpty()) {
                getString(R.string.patient_overview_no_conditions)
            } else {
                patient.healthConditions.joinToString("\n") { line -> "• ${line.capitalizeWords()}" }
            }

        binding.tvCaretakerLine.text =
            getString(
                R.string.patient_overview_caretaker_line,
                patient.caretaker.name,
                patient.caretaker.email,
            )

        binding.tvAssignedNursesDetail.text =
            if (patient.assignedNurses.isEmpty()) {
                getString(R.string.patient_overview_no_nurses)
            } else {
                patient.assignedNurses.joinToString("\n") { nurse ->
                    "${nurse.name} (${nurse.email})"
                }
            }

        Glide.with(this)
            .load(patient.photoUrl)
            .placeholder(R.drawable.profile)
            .circleCrop()
            .into(binding.imagePatient)
    }

    private fun resetAdminSection() {
        binding.cardAdminOverview.visibility = View.GONE
        listOf(
            binding.tvOverviewRiskLevel,
            binding.tvOverviewSummary,
            binding.tvOverviewExtended,
            binding.tvOverviewCarePlan,
            binding.tvOverviewNotes,
            binding.tvOverviewAlerts,
        ).forEach { view ->
            view.visibility = View.GONE
            view.text = ""
        }
    }

    private fun applyAdminOverview(body: PatientAdminOverviewResponse) {
        var any = false

        body.riskLevel?.takeIf { it.isNotBlank() }?.let {
            binding.tvOverviewRiskLevel.visibility = View.VISIBLE
            binding.tvOverviewRiskLevel.text = getString(R.string.patient_overview_risk_level, it)
            any = true
        }

        body.summary?.takeIf { it.isNotBlank() }?.let {
            binding.tvOverviewSummary.visibility = View.VISIBLE
            binding.tvOverviewSummary.text = it
            any = true
        }

        body.overview?.takeIf { it.isNotBlank() }?.let {
            binding.tvOverviewExtended.visibility = View.VISIBLE
            binding.tvOverviewExtended.text = it
            any = true
        }

        body.carePlanSummary?.takeIf { it.isNotBlank() }?.let {
            binding.tvOverviewCarePlan.visibility = View.VISIBLE
            binding.tvOverviewCarePlan.text = it
            any = true
        }

        body.notes?.takeIf { it.isNotBlank() }?.let {
            binding.tvOverviewNotes.visibility = View.VISIBLE
            binding.tvOverviewNotes.text = it
            any = true
        }

        body.alerts?.takeIf { it.isNotEmpty() }?.let { list ->
            binding.tvOverviewAlerts.visibility = View.VISIBLE
            binding.tvOverviewAlerts.text = list.joinToString("\n") { alert -> "• $alert" }
            any = true
        }

        if (any) {
            binding.cardAdminOverview.visibility = View.VISIBLE
        }
    }

    private fun setLoadingState(loading: Boolean) {
        binding.progressLoading.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        binding.svOverviewContent.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message
    }

    private fun showEmpty() {
        binding.svOverviewContent.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
    }

    private fun resolveErrorMessage(response: Response<*>): String {
        return try {
            val raw = response.errorBody()?.string().orEmpty()
            Gson().fromJson(raw, ApiErrorResponse::class.java)?.apiError
        } catch (_: Exception) {
            null
        } ?: response.message().takeIf { it.isNotBlank() }
            ?: getString(R.string.patient_overview_error_generic)
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { word ->
            word.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }

    companion object {
        const val EXTRA_PATIENT_ID = "patient_id"
    }
}
