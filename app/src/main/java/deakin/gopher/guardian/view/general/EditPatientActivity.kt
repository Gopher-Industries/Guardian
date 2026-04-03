package deakin.gopher.guardian.view.general

import android.Manifest
import android.R.style.Theme_Holo_Light_Dialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.databinding.ActivityEditPatientBinding
import deakin.gopher.guardian.model.ApiErrorResponse
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.model.login.Role
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
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.Locale

class EditPatientActivity : BaseActivity() {
    private lateinit var binding: ActivityEditPatientBinding
    private lateinit var patient: Patient

    private var selectedPhotoUri: Uri? = null
    private var capturedPhotoBitmap: Bitmap? = null

    companion object {
        const val EXTRA_PATIENT = "patient"
        private const val CAMERA_PERMISSION_CODE = 1002
        private const val MAX_ALLOWED_AGE = 120
        private val NAME_REGEX = Regex("^[A-Za-z][A-Za-z .'-]{1,49}$")
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedPhotoUri = uri
                capturedPhotoBitmap = null
                binding.imgPreview.setImageURI(uri)
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                capturedPhotoBitmap = bitmap
                selectedPhotoUri = null
                binding.imgPreview.setImageBitmap(bitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patient = intent.getSerializableExtra(EXTRA_PATIENT) as? Patient ?: run {
            showMessage("Patient data missing")
            finish()
            return
        }

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (SessionManager.getCurrentUser().role == Role.Nurse) {
            binding.toolbar.setBackgroundColor(getColor(R.color.TG_blue))
        }

        setupGenderSpinner()
        setupDOBPicker()
        setupValidationListeners()
        populatePatientData()

        binding.btnSelectFromGallery.setOnClickListener { openGallery() }
        binding.btnTakePhoto.setOnClickListener { checkCameraPermissionAndOpen() }
//        binding.btnSave.setOnClickListener { updatePatientInfo() }
    }

    private fun setupGenderSpinner() {
        val genderOptions = listOf("Select gender", "Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = adapter
    }

    private fun populatePatientData() {
        binding.txtName.setText(patient.fullname)
        binding.txtDob.setText(patient.dateOfBirth.substringBefore("T"))
        binding.txtAge.setText(patient.age.toString())

        when (patient.gender.lowercase()) {
            "male" -> binding.genderSpinner.setSelection(1)
            "female" -> binding.genderSpinner.setSelection(2)
            "other" -> binding.genderSpinner.setSelection(3)
            else -> binding.genderSpinner.setSelection(0)
        }

        Glide.with(this).load(patient.photoUrl).placeholder(R.drawable.profile).circleCrop()
            .into(binding.imgPreview)
    }

    private fun setupDOBPicker() {
        binding.txtDob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                Theme_Holo_Light_Dialog,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format(
                        Locale.getDefault(),
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay,
                    )
                    binding.txtDob.setText(formattedDate)
                    updateAgeField(selectedYear, selectedMonth, selectedDay)
                },
                year,
                month,
                day,
            )

            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }

    private fun setupValidationListeners() {
        binding.txtName.doAfterTextChanged {
            binding.nameInputLayout.error = null
        }
        binding.txtDob.doAfterTextChanged {
            binding.dobInputLayout.error = null
        }
    }

    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE,
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                showMessage("Camera permission is required to take photos")
            }
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun openCamera() {
        cameraLauncher.launch(null)
    }

//    private fun updatePatientInfo() {
//        if (!validateInputs()) return
//
//        val fullname = binding.txtName.text.toString().trim()
//        val dob = binding.txtDob.text.toString().trim()
//        val gender = binding.genderSpinner.selectedItem?.toString()?.lowercase() ?: ""
//
//        val namePart = fullname.toRequestBody("text/plain".toMediaTypeOrNull())
//        val dobPart = dob.toRequestBody("text/plain".toMediaTypeOrNull())
//        val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())
//
//        val photoPart: MultipartBody.Part? = when {
//            selectedPhotoUri != null -> prepareFilePart("photo", selectedPhotoUri!!, this)
//            capturedPhotoBitmap != null -> prepareBitmapPart("photo", capturedPhotoBitmap!!)
//            else -> null
//        }
//
//        val token = "Bearer ${SessionManager.getToken()}"
//
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.Main) {
//                binding.progressBar.show()
//                binding.btnSave.hide()
//            }
//
//            val response = try {
//                ApiClient.apiService.updatePatient(
//                    token = token,
//                    patientId = patient.id,
//                    name = namePart,
//                    dob = dobPart,
//                    gender = genderPart,
//                    photo = photoPart,
//                )
//            } catch (e: Exception) {
//                null
//            }
//
//            withContext(Dispatchers.Main) {
//                binding.progressBar.hide()
//                binding.btnSave.show()
//
//                if (response?.isSuccessful == true) {
//                    showMessage(response.body()?.apiMessage ?: "Patient updated successfully")
//                    finish()
//                } else {
//                    val errorBody = try {
//                        response?.errorBody()?.string()
//                    } catch (e: Exception) {
//                        null
//                    }
//
//                    val errorResponse = try {
//                        Gson().fromJson(errorBody, ApiErrorResponse::class.java)
//                    } catch (e: Exception) {
//                        null
//                    }
//
//                    showMessage(errorResponse?.apiError?.takeIf { it.isNotBlank() }
//                        ?: response?.message()?.takeIf { it.isNotBlank() }
//                        ?: "Failed to update patient")
//                }
//            }
//        }
//    }

    private fun validateInputs(): Boolean {
        clearErrors()

        val name = binding.txtName.text.toString().trim().replace("\\s+".toRegex(), " ")
        val dobText = binding.txtDob.text.toString().trim()

        return when {
            name.isEmpty() -> {
                setNameError(getString(R.string.validation_empty_name))
                false
            }

            name.length < 2 -> {
                setNameError("Name must be at least 2 characters")
                false
            }

            !NAME_REGEX.matches(name) -> {
                setNameError("Name can contain only letters, spaces, apostrophes, dots or hyphens")
                false
            }

            dobText.isEmpty() -> {
                setDobError(getString(R.string.validation_empty_dob))
                false
            }

            !isValidDob(dobText) -> {
                setDobError("Please select a valid date of birth")
                false
            }

            binding.genderSpinner.selectedItemPosition == 0 -> {
                showMessage(getString(R.string.validation_empty_gender))
                false
            }

            else -> true
        }
    }

    private fun clearErrors() {
        binding.nameInputLayout.error = null
        binding.dobInputLayout.error = null
    }

    private fun setNameError(message: String) {
        binding.nameInputLayout.error = message
        binding.txtName.requestFocus()
    }

    private fun setDobError(message: String) {
        binding.dobInputLayout.error = message
        binding.txtDob.requestFocus()
    }

    private fun isValidDob(dobText: String): Boolean {
        val dobParts = dobText.split("-")
        if (dobParts.size != 3) return false

        val year = dobParts[0].toIntOrNull() ?: return false
        val month = dobParts[1].toIntOrNull()?.minus(1) ?: return false
        val day = dobParts[2].toIntOrNull() ?: return false

        val age = calculateAge(year, month, day)
        return age in 0..MAX_ALLOWED_AGE
    }

    private fun updateAgeField(year: Int, month: Int, day: Int) {
        val age = calculateAge(year, month, day).coerceAtLeast(0)
        binding.txtAge.setText(age.toString())
    }

    private fun calculateAge(year: Int, month: Int, day: Int): Int {
        val today = Calendar.getInstance()
        val birthDate = Calendar.getInstance()
        birthDate.set(year, month, day)
        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    private fun prepareFilePart(
        partName: String,
        fileUri: Uri,
        context: Context,
    ): MultipartBody.Part? {
        val inputStream = context.contentResolver.openInputStream(fileUri)
        val fileBytes = inputStream?.readBytes() ?: return null
        val requestFile = fileBytes.toRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, "profile.jpg", requestFile)
    }

    private fun prepareBitmapPart(
        partName: String,
        bitmap: Bitmap,
    ): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val byteArray = stream.toByteArray()
        val requestFile = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, "profile.jpg", requestFile)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}