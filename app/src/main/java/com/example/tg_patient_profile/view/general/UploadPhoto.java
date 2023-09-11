package com.example.tg_patient_profile.view.general;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.databinding.ActivityUploadPhotoBinding;
import com.example.tg_patient_profile.view.patient.PatientAddFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

public class UploadPhoto extends AppCompatActivity {

    ActivityUploadPhotoBinding binding;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private boolean CapturePhoto = false;

    Uri imageuri;
    Uri imageUri2;

    Uri UpdatedPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        binding = ActivityUploadPhotoBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();


        binding.takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CapturePhoto = true;
                onCaptureButtonClick(v);
            }
        });

        binding.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CapturePhoto = false;
                PickImageIntent();
            }
        });

        binding.crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CapturePhoto && imageuri != null) {
                    startCrop(imageuri);
                } else if (!CapturePhoto && imageUri2 != null) {
                    startCrop(imageUri2);
                } else {
                    Toast.makeText(UploadPhoto.this, "No image selected for cropping", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UploadPhoto.this, PatientAddFragment.class);
                startActivity(intent);
            }
        });
    }

    private void startCrop(Uri imagesUri){
        CropImage.activity(imagesUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    public void onCaptureButtonClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            TakePictureIntent();
        }
    }

    private void TakePictureIntent() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
    }

    private void PickImageIntent(){
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQUEST_IMAGE_PICK);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri croppedImageUri = result.getUri();
                binding.profile.setImageURI(croppedImageUri);
                uploadImageToFirebase(croppedImageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (CapturePhoto) {
                    imageuri = data.getData();
                    Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
                    binding.profile.setImageBitmap(photoBitmap);
                    UpdatedPic=imageuri;
                } else {
                    imageUri2 = data.getData();
                    uploadImageToFirebase(imageUri2);
                    binding.profile.setImageURI(imageUri2);
                    startCrop(imageUri2);
                    UpdatedPic=imageUri2;
                }
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                imageUri2 = data.getData();
                uploadImageToFirebase(imageUri2);
                binding.profile.setImageURI(imageUri2);
                startCrop(imageUri2);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                TakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission is required to take photos.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadImageToFirebase(Uri imageUri) {

        String imageName = UUID.randomUUID().toString();

        StorageReference imageRef = storageReference.child("images/" + imageName);

        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnProgressListener(taskSnapshot -> {

            long bytesTransferred = taskSnapshot.getBytesTransferred();
            long totalBytes = taskSnapshot.getTotalByteCount();
            int progress = (int) (100.0 * bytesTransferred / totalBytes);
            Log.i("Upload Progress", String.valueOf(progress));
        }).addOnSuccessListener(taskSnapshot -> {

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.i("Download URL", downloadUrl);

            }).addOnFailureListener(exception -> {

                exception.printStackTrace();
            });
        }).addOnFailureListener(exception -> {

            exception.printStackTrace();
        });

    }

}