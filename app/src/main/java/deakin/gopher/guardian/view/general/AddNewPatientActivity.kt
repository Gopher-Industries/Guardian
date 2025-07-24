package deakin.gopher.guardian.view.general

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.databinding.ActivityAddNewPatientBinding
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.login.SessionManager
import deakin.gopher.guardian.services.api.ApiClient
import deakin.gopher.guardian.view.hide
import deakin.gopher.guardian.view.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Calendar
import java.util.Locale

class AddNewPatientActivity : BaseActivity() {
    private lateinit var binding: ActivityAddNewPatientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.txtDob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(
                    this,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        // Format the selected date as "YYYY-MM-DD"
                        val formattedDate =
                            String.format(
                                Locale.getDefault(),
                                "%04d-%02d-%02d",
                                selectedYear,
                                selectedMonth + 1,
                                selectedDay,
                            )
                        binding.txtDob.setText(formattedDate)
                    },
                    year,
                    month,
                    day,
                )

            datePickerDialog.datePicker.maxDate =
                System.currentTimeMillis()
            datePickerDialog.show()
        }

        binding.btnSave.setOnClickListener {
            savePatientInfo()
        }
    }

    private fun savePatientInfo() {
        val inputValidated = validateInputs()

        if (!inputValidated) {
            return
        }

        val fullname = binding.txtName.text.toString().trim()
        val dob = binding.txtDob.text.toString().trim()
        val gender =
            findViewById<MaterialButton>(binding.genderToggleGroup.checkedButtonId).text.toString()
                .lowercase()

        val namePart = fullname.toRequestBody("text/plain".toMediaTypeOrNull())
        val dobPart = dob.toRequestBody("text/plain".toMediaTypeOrNull())
        val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())

        val photoFile = null
        val photoPart: MultipartBody.Part? =
            photoFile?.let {
                prepareFilePart("photo", it, this@AddNewPatientActivity.baseContext)
            }

        val token = "Bearer ${SessionManager.getToken()}"
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.progressBar.show()
                binding.btnSave.visibility = View.GONE
            }
            val response =
                ApiClient.apiService.addPatient(token, namePart, dobPart, genderPart, photoPart)
            withContext(Dispatchers.Main) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.hide()
                    binding.btnSave.visibility = View.VISIBLE
                }
                if (response.isSuccessful) {
                    if (response.body()?.patient != null) {
                        showMessage(response.message())
                        onBackPressedDispatcher.onBackPressed()
                    } else {
                        showMessage(response.body()?.apiError ?: "Failed to add patient")
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

    private fun validateInputs(): Boolean {
        if (binding.txtName.text.toString().trim().isEmpty()) {
            showMessage(getString(R.string.validation_empty_name))
            return false
        }

        if (binding.txtDob.text.toString().trim().isEmpty()) {
            showMessage(getString(R.string.validation_empty_dob))
            return false
        }

        if (binding.genderToggleGroup.checkedButtonId == View.NO_ID) {
            showMessage(getString(R.string.validation_empty_gender))
            return false
        }

        return true
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun prepareFilePart(
        partName: String,
        fileUri: Uri,
        context: Context,
    ): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(fileUri)
        val fileBytes = inputStream?.readBytes()
        val requestFile = fileBytes?.toRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, "profile.jpg", requestFile!!)
    }
}
