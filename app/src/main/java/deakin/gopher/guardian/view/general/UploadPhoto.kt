package deakin.gopher.guardian.view.general

import android.Manifest
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import java.lang.Throwable
import java.util.UUID
import kotlin.Array
import kotlin.Boolean
import kotlin.Int
import kotlin.IntArray
import kotlin.Long
import kotlin.String
import kotlin.arrayOf

class UploadPhoto constructor() : BaseActivity() {
    var imageuri: Uri? = null
    var imageUri2: Uri? = null
    private var storageReference: StorageReference? = null
    private var CapturePhoto: Boolean = false
    private var launcher: ActivityResultLauncher<CropImageContractOptions>? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_photo)
        setContentView(R.layout.activity_upload_photo)
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()
        val profile: ImageView = findViewById(R.id.profile)
        launcher = registerForActivityResult(
            CropImageContract(),
            { result ->
                if (result.isSuccessful()) {
                    val croppedImageUri: Uri = result.getUriContent()
                    profile.setImageURI(croppedImageUri)
                    uploadImageToFirebase(croppedImageUri)
                }
            })
        val takephoto: Button = findViewById(R.id.takephoto)
        takephoto.setOnClickListener(
            View.OnClickListener({ v: View? ->
                CapturePhoto = true
                onCaptureButtonClick(v)
            })
        )
        val gallery: Button = findViewById(R.id.gallery)
        gallery.setOnClickListener(
            View.OnClickListener({ v: View? ->
                CapturePhoto = false
                PickImageIntent()
            })
        )
        val crop: Button = findViewById(R.id.crop)
        crop.setOnClickListener(
            View.OnClickListener({ v: View? ->
                if (CapturePhoto && null != imageuri) {
                    startCrop(imageuri)
                } else if (!CapturePhoto && null != imageUri2) {
                    startCrop(imageUri2)
                } else {
                    Toast.makeText(
                        this@UploadPhoto,
                        "No image selected for cropping",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        )
        val close: Button = findViewById(R.id.close)
        close.setOnClickListener(
            View.OnClickListener({ v: View? ->
                val intent: Intent = Intent(this@UploadPhoto, PatientAddFragment::class.java)
                startActivity(intent)
            })
        )
    }

    private fun startCrop(imagesUri: Uri?) {
        val cropIntent: Intent = Intent(this, CropImageActivity::class.java)
        val bundle: Bundle = Bundle(2)
        bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE, imagesUri)
        bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, CropImageOptions())
        cropIntent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle)
        launcher.launch(CropImageContractOptions(imagesUri, CropImageOptions()))
    }

    fun onCaptureButtonClick(view: View?) {
        if ((PackageManager.PERMISSION_GRANTED
                    != ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA))
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION
            )
        } else {
            TakePictureIntent()
        }
    }

    private fun TakePictureIntent() {
        val takePicture: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE)
    }

    private fun PickImageIntent() {
        val pickImage: Intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickImage, REQUEST_IMAGE_PICK)
    }

    protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_OK === resultCode) {
            val profile: ImageView = findViewById(R.id.profile)
            if (REQUEST_IMAGE_CAPTURE == requestCode) {
                if (CapturePhoto) {
                    imageuri = data.getData()
                    profile.setImageBitmap(data.getExtras().get("data"))
                } else {
                    imageUri2 = data.getData()
                    uploadImageToFirebase(imageUri2)
                    profile.setImageURI(imageUri2)
                    startCrop(imageUri2)
                }
            } else if (REQUEST_IMAGE_PICK == requestCode) {
                imageUri2 = data.getData()
                uploadImageToFirebase(imageUri2)
                profile.setImageURI(imageUri2)
                startCrop(imageUri2)
            }
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (REQUEST_CAMERA_PERMISSION == requestCode) {
            if (0 < grantResults.size && PackageManager.PERMISSION_GRANTED == grantResults.get(0)) {
                TakePictureIntent()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required to take photos.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri?) {
        val imageName: String = UUID.randomUUID().toString()
        val imageRef: StorageReference = storageReference.child("images/" + imageName)
        val uploadTask: UploadTask = imageRef.putFile(imageUri)
        uploadTask
            .addOnProgressListener(
                { taskSnapshot ->
                    val bytesTransferred: Long = taskSnapshot.getBytesTransferred()
                    val totalBytes: Long = taskSnapshot.getTotalByteCount()
                    val progress: Int = (100.0 * bytesTransferred / totalBytes).toInt()
                    Log.i("Upload Progress", progress.toString())
                })
            .addOnSuccessListener(
                { taskSnapshot ->
                    imageRef
                        .getDownloadUrl()
                        .addOnSuccessListener(
                            { uri ->
                                val downloadUrl: String = uri.toString()
                                Log.i("Download URL", downloadUrl)
                            })
                        .addOnFailureListener(Throwable::printStackTrace)
                })
            .addOnFailureListener(Throwable::printStackTrace)
    }

    companion object {
        private val REQUEST_CAMERA_PERMISSION: Int = 200
        private val REQUEST_IMAGE_CAPTURE: Int = 1
        private val REQUEST_IMAGE_PICK: Int = 2
        private val REQUEST_IMAGE_CROP: Int = 3
    }
}