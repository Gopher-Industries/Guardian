package com.example.guardian.view.general;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.guardian.R;
import com.example.guardian.databinding.ActivityUploadPhotoBinding;
import com.example.guardian.view.patient.PatientAddFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.UUID;

public class UploadPhoto extends AppCompatActivity {

  private static final int REQUEST_CAMERA_PERMISSION = 200;
  private static final int REQUEST_IMAGE_CAPTURE = 1;
  private static final int REQUEST_IMAGE_PICK = 2;
  ActivityUploadPhotoBinding binding;
  Uri imageuri;
  Uri imageUri2;
  private StorageReference storageReference;
  private boolean CapturePhoto;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_upload_photo);

    binding = ActivityUploadPhotoBinding.inflate(getLayoutInflater());

    setContentView(binding.getRoot());

    final FirebaseStorage storage = FirebaseStorage.getInstance();
    storageReference = storage.getReference();

    binding.takephoto.setOnClickListener(
        v -> {
          CapturePhoto = true;
          onCaptureButtonClick(v);
        });

    binding.gallery.setOnClickListener(
        v -> {
          CapturePhoto = false;
          PickImageIntent();
        });

    binding.crop.setOnClickListener(
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

    binding.close.setOnClickListener(
        v -> {
          final Intent intent = new Intent(UploadPhoto.this, PatientAddFragment.class);
          startActivity(intent);
        });
  }

  private void startCrop(final Uri imagesUri) {
    CropImage.activity(imagesUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
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
      if (CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
        final CropImage.ActivityResult result = CropImage.getActivityResult(data);
        final Uri croppedImageUri = result.getUri();
        binding.profile.setImageURI(croppedImageUri);
        uploadImageToFirebase(croppedImageUri);
      } else if (REQUEST_IMAGE_CAPTURE == requestCode) {
        if (CapturePhoto) {
          imageuri = data.getData();
          final Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
          binding.profile.setImageBitmap(photoBitmap);
        } else {
          imageUri2 = data.getData();
          uploadImageToFirebase(imageUri2);
          binding.profile.setImageURI(imageUri2);
          startCrop(imageUri2);
        }
      } else if (REQUEST_IMAGE_PICK == requestCode) {
        imageUri2 = data.getData();
        uploadImageToFirebase(imageUri2);
        binding.profile.setImageURI(imageUri2);
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
