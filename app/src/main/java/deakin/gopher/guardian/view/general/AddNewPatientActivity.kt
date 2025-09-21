@file:Suppress("ktlint")

package deakin.gopher.guardian.view.general

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Locale
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
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
import java.io.File
import java.util.*

class AddNewPatientActivity : AppCompatActivity() {

    private lateinit var txtName: EditText
    private lateinit var txtDob: EditText
    private lateinit var txtAge: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var btnSave: MaterialButton
    private lateinit var btnSelectFromGallery: MaterialButton
    private lateinit var btnTakePhoto: MaterialButton
    private lateinit var imgPreview: ImageView
    private lateinit var progressBar: ProgressBar

    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_patient)

        val currentUser = SessionManager.getCurrentUser()

        if (currentUser.role == Role.Nurse) {
            val nurseBinding = ActivityAddNewPatientNurseBinding.inflate(layoutInflater)
            var binding = nurseBinding
            setContentView(nurseBinding.root)
        } else {
            val caretakerBinding = ActivityAddNewPatientBinding.inflate(layoutInflater)
            var binding = caretakerBinding
            setContentView(caretakerBinding.root)
        }


        txtName = findViewById(R.id.txtName)
        txtDob = findViewById(R.id.txtDob)
        txtAge = findViewById(R.id.txtAge)
        genderSpinner = findViewById(R.id.genderSpinner)
        btnSave = findViewById(R.id.btnSave)
        btnSelectFromGallery = findViewById(R.id.btnSelectFromGallery)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        imgPreview = findViewById(R.id.imgPreview)
        progressBar = findViewById(R.id.progressBar)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }


        btnSelectFromGallery.setOnClickListener {
            if (checkPermissions()) openGallery()
        }
        btnTakePhoto.setOnClickListener {
            if (checkPermissions()) openCamera()
        }


        setupGenderSpinner()
        setupDOBPicker()

        btnSave.setOnClickListener { savePatientInfo() }
        btnSelectFromGallery.setOnClickListener { openGallery() }
        btnTakePhoto.setOnClickListener { openCamera() }
    }

    private fun setupGenderSpinner() {
        val options = listOf("Select gender", "Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter
    }

    private fun calculateAge(year: Int, month: Int, day: Int): Int {
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - year

        // Adjust if birthday hasn't occurred yet this year
        if (today.get(Calendar.MONTH) < month ||
            (today.get(Calendar.MONTH) == month && today.get(Calendar.DAY_OF_MONTH) < day)) {
            age--
        }
        return age
    }


    private fun setupDOBPicker() {
        val calendar = Calendar.getInstance()

        txtDob.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog,
                { _, selectedYear, selectedMonth, selectedDay ->
                    txtDob.setText("${selectedDay}/${selectedMonth + 1}/${selectedYear}")
                    txtAge.setText(calculateAge(selectedYear, selectedMonth, selectedDay).toString())
                },
                year,
                month,
                day
            )

            dpd.datePicker.maxDate = System.currentTimeMillis()
            dpd.show()
        }
    }


    private fun validateInputs(): Boolean {
        if (txtName.text.toString().trim().isEmpty()) {
            showMessage(getString(R.string.validation_empty_name))
            return false
        }
        if (txtDob.text.toString().trim().isEmpty()) {
            showMessage(getString(R.string.validation_empty_dob))
            return false
        }
        if (genderSpinner.selectedItemPosition == 0) {
            showMessage(getString(R.string.validation_empty_gender))
            return false
        }
        return true
    }

    private fun savePatientInfo() {
        if (!validateInputs()) return

        val fullname = txtName.text.toString().trim()
        val dobInput = txtDob.text.toString().trim()
        val gender = genderSpinner.selectedItem.toString().lowercase()

        // Convert dob to ISO 8601 format
        val sdfInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sdfOutput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dobFormatted = try {
            val date = sdfInput.parse(dobInput)
            sdfOutput.format(date!!)
        } catch (e: Exception) {
            showMessage("Invalid date format")
            return
        }

        val namePart = fullname.toRequestBody("text/plain".toMediaTypeOrNull())
        val dobPart = dobFormatted.toRequestBody("text/plain".toMediaTypeOrNull())
        val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())

        val photoPart: MultipartBody.Part? = selectedPhotoUri?.let { prepareFilePart("photo", it) }

        val token = "Bearer ${SessionManager.getToken()}"

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                progressBar.show()
                btnSave.visibility = View.GONE
            }

            val response = ApiClient.apiService.addPatient(token, namePart, dobPart, genderPart, photoPart)

            withContext(Dispatchers.Main) {
                progressBar.hide()
                btnSave.visibility = View.VISIBLE

                if (response.isSuccessful) {
                    if (response.body()?.patient != null) {
                        showMessage("Patient added successfully")
                        onBackPressed()
                    } else {
                        showMessage(response.body()?.apiError ?: "Failed to add patient")
                    }
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), ApiErrorResponse::class.java)
                    showMessage(errorResponse.apiError ?: response.message())
                }
            }
        }
    }


    private val GALLERY_REQUEST_CODE = 100

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


    private val CAMERA_REQUEST_CODE = 101
    private var photoUri: Uri? = null

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Prepare a temporary file for the photo
        val photoFile = File(externalCacheDir, "temp_photo.jpg")
        photoUri = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    val selectedImage = data?.data
                    selectedImage?.let { imgPreview.setImageURI(it) }
                }
                CAMERA_REQUEST_CODE -> {
                    photoUri?.let { imgPreview.setImageURI(it) }
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return if (cameraPermission == PackageManager.PERMISSION_GRANTED &&
            storagePermission == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                102
            )
            false
        }
    }

    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part {
        val inputStream = contentResolver.openInputStream(fileUri)
        val bytes = inputStream?.readBytes()
        val requestFile = bytes?.toRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, "profile.jpg", requestFile!!)
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}