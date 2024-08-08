package deakin.gopher.guardian.view.general;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.canhub.cropper.CropImageActivity;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.canhub.cropper.CropImage;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.view.patient.PatientAddFragment;
import java.util.UUID;

public class UploadPhoto extends BaseActivity {

  private static final int REQUEST_CAMERA_PERMISSION = 200;
  private static final int REQUEST_IMAGE_CAPTURE = 1;
  private static final int REQUEST_IMAGE_PICK = 2;
  private static final int REQUEST_IMAGE_CROP = 3;
  Uri imageuri;
  Uri imageUri2;
  private StorageReference storageReference;
  private boolean CapturePhoto;

  private ActivityResultLauncher<CropImageContractOptions> launcher;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_upload_photo);

    setContentView(R.layout.activity_upload_photo);

    final FirebaseStorage storage = FirebaseStorage.getInstance();
    storageReference = storage.getReference();

    final ImageView profile = findViewById(R.id.profile);
    launcher = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
               final Uri croppedImageUri = result.getUriContent();
                profile.setImageURI(croppedImageUri);
                uploadImageToFirebase(croppedImageUri);
            }
        }
    );

    final Button takephoto = findViewById(R.id.takephoto);
    takephoto.setOnClickListener(
        v -> {
          CapturePhoto = true;
          onCaptureButtonClick(v);
        });

    final Button gallery = findViewById(R.id.gallery);
    gallery.setOnClickListener(
        v -> {
          CapturePhoto = false;
          PickImageIntent();
        });

    final Button crop = findViewById(R.id.crop);
    crop.setOnClickListener(
        v -> {
          if (CapturePhoto && null != imageuri) {
            startCrop(imageuri);
          } else if (!CapturePhoto && null != imageUri2) {
            startCrop(imageUri2);
          } else {
            Toast.makeText(UploadPhoto.this, "No image selected for cropping", Toast.LENGTH_SHORT)
                .show();
          }
        });

    final Button close = findViewById(R.id.close);
    close.setOnClickListener(
        v -> {
          final Intent intent = new Intent(UploadPhoto.this, PatientAddFragment.class);
          startActivity(intent);
        });
  }

  private void startCrop(final Uri imagesUri) {
      final Intent cropIntent = new Intent(this, CropImageActivity.class);
      final Bundle bundle = new Bundle(2);
      bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE, imagesUri);
      bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, new CropImageOptions());
      cropIntent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle);

      launcher.launch(new CropImageContractOptions(imagesUri, new CropImageOptions()));
  }

  public void onCaptureButtonClick(final View view) {
    if (PackageManager.PERMISSION_GRANTED
        != ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
      ActivityCompat.requestPermissions(
          this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    } else {
      TakePictureIntent();
    }
  }

  private void TakePictureIntent() {
    final Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
  }

  private void PickImageIntent() {
    final Intent pickImage =
        new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(pickImage, REQUEST_IMAGE_PICK);
  }

  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (RESULT_OK == resultCode) {
      final ImageView profile = findViewById(R.id.profile);
      if (REQUEST_IMAGE_CAPTURE == requestCode) {
        if (CapturePhoto) {
          imageuri = data.getData();
          final Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
          profile.setImageBitmap(photoBitmap);
        } else {
          imageUri2 = data.getData();
          uploadImageToFirebase(imageUri2);
          profile.setImageURI(imageUri2);
          startCrop(imageUri2);
        }
      } else if (REQUEST_IMAGE_PICK == requestCode) {
        imageUri2 = data.getData();
        uploadImageToFirebase(imageUri2);
        profile.setImageURI(imageUri2);
        startCrop(imageUri2);
      }
    }
  }

  public void onRequestPermissionsResult(
      final int requestCode,
      @NonNull final String[] permissions,
      @NonNull final int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (REQUEST_CAMERA_PERMISSION == requestCode) {
      if (0 < grantResults.length && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
        TakePictureIntent();
      } else {
        Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_SHORT)
            .show();
      }
    }
  }

  private void uploadImageToFirebase(final Uri imageUri) {

    final String imageName = UUID.randomUUID().toString();

    final StorageReference imageRef = storageReference.child("images/" + imageName);

    final UploadTask uploadTask = imageRef.putFile(imageUri);

    uploadTask
        .addOnProgressListener(
            taskSnapshot -> {
              final long bytesTransferred = taskSnapshot.getBytesTransferred();
              final long totalBytes = taskSnapshot.getTotalByteCount();
              final int progress = (int) (100.0 * bytesTransferred / totalBytes);
              Log.i("Upload Progress", String.valueOf(progress));
            })
        .addOnSuccessListener(
            taskSnapshot ->
                imageRef
                    .getDownloadUrl()
                    .addOnSuccessListener(
                        uri -> {
                          final String downloadUrl = uri.toString();
                          Log.i("Download URL", downloadUrl);
                        })
                    .addOnFailureListener(Throwable::printStackTrace))
        .addOnFailureListener(Throwable::printStackTrace);
  }
}
