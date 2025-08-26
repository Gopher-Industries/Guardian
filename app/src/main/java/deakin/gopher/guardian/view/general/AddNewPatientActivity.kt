package deakin.gopher.guardian.view.general

import android.Manifest
import android.R.style.Theme_Holo_Light_Dialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.google.gson.Gson
import deakin.gopher.guardian.R
import deakin.gopher.guardian.databinding.ActivityAddNewPatientBinding
import deakin.gopher.guardian.databinding.ActivityAddNewPatientNurseBinding
import deakin.gopher.guardian.model.ApiErrorResponse
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

class AddNewPatientActivity : BaseActivity() {
    private lateinit var binding: ViewBinding

    private var selectedPhotoUri: Uri? = null
    private var capturedPhotoBitmap: Bitmap? = null

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
    }

    // Launcher for picking image from gallery
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedPhotoUri = uri
                capturedPhotoBitmap = null
                val localBinding = binding
                when (localBinding) {
                    is ActivityAddNewPatientBinding -> localBinding.imgPreview.setImageURI(uri)
                    is ActivityAddNewPatientNurseBinding -> localBinding.imgPreview.setImageURI(uri)
                }
            }
        }

    // Launcher for taking photo with camera (returns Bitmap)
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                capturedPhotoBitmap = bitmap
                selectedPhotoUri = null
                val localBinding = binding
                when (localBinding) {
                    is ActivityAddNewPatientBinding -> localBinding.imgPreview.setImageBitmap(bitmap)
                    is ActivityAddNewPatientNurseBinding -> localBinding.imgPreview.setImageBitmap(bitmap)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = SessionManager.getCurrentUser()

        if (currentUser.role == Role.Nurse) {
            val nurseBinding = ActivityAddNewPatientNurseBinding.inflate(layoutInflater)
            binding = nurseBinding
            setContentView(nurseBinding.root)
        } else {
            val caretakerBinding = ActivityAddNewPatientBinding.inflate(layoutInflater)
            binding = caretakerBinding
            setContentView(caretakerBinding.root)
        }

        val localBinding = binding
        when (localBinding) {
            is ActivityAddNewPatientBinding -> {
                setSupportActionBar(localBinding.toolbar)
                localBinding.toolbar.setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                setupUI(localBinding)
            }
            is ActivityAddNewPatientNurseBinding -> {
                setSupportActionBar(localBinding.toolbar)
                localBinding.toolbar.setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                setupUI(localBinding)
            }
        }
    }

    // Common UI setup for both bindings
    private fun setupUI(localBinding: ViewBinding) {
        when (localBinding) {
            is ActivityAddNewPatientBinding -> {
                setupGenderSpinner(localBinding.genderSpinner)
                setupDOBPicker(localBinding.txtDob)
                localBinding.btnSelectFromGallery.setOnClickListener { openGallery() }
                localBinding.btnTakePhoto.setOnClickListener { checkCameraPermissionAndOpen() }
                localBinding.btnSave.setOnClickListener { savePatientInfo() }
            }
            is ActivityAddNewPatientNurseBinding -> {
                setupGenderSpinner(localBinding.genderSpinner)
                setupDOBPicker(localBinding.txtDob)
                localBinding.btnSelectFromGallery.setOnClickListener { openGallery() }
                localBinding.btnTakePhoto.setOnClickListener { checkCameraPermissionAndOpen() }
                localBinding.btnSave.setOnClickListener { savePatientInfo() }
            }
        }
    }

    private fun setupGenderSpinner(genderSpinner: android.widget.Spinner) {
        val genderOptions = listOf("Select gender", "Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter
    }

    private fun setupDOBPicker(txtDob: android.widget.EditText) {
        txtDob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(
                    this,
                    Theme_Holo_Light_Dialog,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate =
                            String.format(
                                Locale.getDefault(),
                                "%04d-%02d-%02d",
                                selectedYear,
                                selectedMonth + 1,
                                selectedDay,
                            )
                        txtDob.setText(formattedDate)
                    },
                    year,
                    month,
                    day,
                )

            // Force spinner mode (for API 21+)
            try {
                val datePickerField = datePickerDialog.datePicker.javaClass.getDeclaredField("mDelegate")
                datePickerField.isAccessible = true
                val delegate = datePickerField.get(datePickerDialog.datePicker)

                val spinnerDelegateClass = Class.forName("android.widget.DatePickerSpinnerDelegate")

                if (delegate.javaClass != spinnerDelegateClass) {
                    datePickerField.set(datePickerDialog.datePicker, null) // Clear the current delegate

                    val constructor =
                        datePickerDialog.datePicker.javaClass.getDeclaredConstructor(
                            Context::class.java,
                            android.util.AttributeSet::class.java,
                            Int::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType,
                        )
                    constructor.isAccessible = true

                    val spinnerDelegate =
                        constructor.newInstance(
                            datePickerDialog.datePicker.context,
                            null,
                            android.R.attr.datePickerStyle,
                            0,
                        )
                    datePickerField.set(datePickerDialog.datePicker, spinnerDelegate)

                    // Re-initialize the date picker with current date
                    datePickerDialog.datePicker.updateDate(year, month, day)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Fallback gracefully if reflection fails
            }

            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }

    // Permission check before opening camera
    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
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
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
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

    private fun savePatientInfo() {
        if (!validateInputs()) return

        val localBinding = binding

        val fullname =
            when (localBinding) {
                is ActivityAddNewPatientBinding -> localBinding.txtName.text.toString().trim()
                is ActivityAddNewPatientNurseBinding -> localBinding.txtName.text.toString().trim()
                else -> ""
            }

        val dob =
            when (localBinding) {
                is ActivityAddNewPatientBinding -> localBinding.txtDob.text.toString().trim()
                is ActivityAddNewPatientNurseBinding -> localBinding.txtDob.text.toString().trim()
                else -> ""
            }

        val gender =
            when (localBinding) {
                is ActivityAddNewPatientBinding -> localBinding.genderSpinner.selectedItem?.toString()?.lowercase() ?: ""
                is ActivityAddNewPatientNurseBinding -> localBinding.genderSpinner.selectedItem?.toString()?.lowercase() ?: ""
                else -> ""
            }

        val namePart = fullname.toRequestBody("text/plain".toMediaTypeOrNull())
        val dobPart = dob.toRequestBody("text/plain".toMediaTypeOrNull())
        val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())

        val photoPart: MultipartBody.Part? =
            when {
                selectedPhotoUri != null -> prepareFilePart("photo", selectedPhotoUri!!, this)
                capturedPhotoBitmap != null -> prepareBitmapPart("photo", capturedPhotoBitmap!!)
                else -> null
            }

        val token = "Bearer ${SessionManager.getToken()}"

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                when (localBinding) {
                    is ActivityAddNewPatientBinding -> {
                        localBinding.progressBar.show()
                        localBinding.btnSave.visibility = View.GONE
                    }
                    is ActivityAddNewPatientNurseBinding -> {
                        localBinding.progressBar.show()
                        localBinding.btnSave.visibility = View.GONE
                    }
                }
            }

            val response = ApiClient.apiService.addPatient(token, namePart, dobPart, genderPart, photoPart)

            withContext(Dispatchers.Main) {
                when (localBinding) {
                    is ActivityAddNewPatientBinding -> {
                        localBinding.progressBar.hide()
                        localBinding.btnSave.visibility = View.VISIBLE
                    }
                    is ActivityAddNewPatientNurseBinding -> {
                        localBinding.progressBar.hide()
                        localBinding.btnSave.visibility = View.VISIBLE
                    }
                }

                if (response.isSuccessful) {
                    if (response.body()?.patient != null) {
                        showMessage(response.message())
                        onBackPressedDispatcher.onBackPressed()
                    } else {
                        showMessage(response.body()?.apiError ?: "Failed to add patient")
                    }
                } else {
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
        val localBinding = binding
        return when (localBinding) {
            is ActivityAddNewPatientBinding -> {
                if (localBinding.txtName.text.toString().trim().isEmpty()) {
                    showMessage(getString(R.string.validation_empty_name))
                    false
                } else if (localBinding.txtDob.text.toString().trim().isEmpty()) {
                    showMessage(getString(R.string.validation_empty_dob))
                    false
                } else if (localBinding.genderSpinner.selectedItemPosition == 0) {
                    showMessage(getString(R.string.validation_empty_gender))
                    false
                } else {
                    true
                }
            }
            is ActivityAddNewPatientNurseBinding -> {
                if (localBinding.txtName.text.toString().trim().isEmpty()) {
                    showMessage(getString(R.string.validation_empty_name))
                    false
                } else if (localBinding.txtDob.text.toString().trim().isEmpty()) {
                    showMessage(getString(R.string.validation_empty_dob))
                    false
                } else if (localBinding.genderSpinner.selectedItemPosition == 0) {
                    showMessage(getString(R.string.validation_empty_gender))
                    false
                } else {
                    true
                }
            }
            else -> false
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun calculateAge(
        year: Int,
        month: Int,
        day: Int,
    ): Int {
        val today = Calendar.getInstance()
        val birthDate = Calendar.getInstance()
        birthDate.set(year, month, day)
        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        // If birthday hasn't happened yet this year, subtract one
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
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(fileUri)
        val fileBytes = inputStream?.readBytes()
        if (fileBytes == null) return null
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
}
