package com.example.tg_patient_profile.view.general;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.databinding.ActivityUploadPhotoBinding;
import com.example.tg_patient_profile.view.patient.PatientAdd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
                startCrop();
            }
        });

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadPhoto.this, PatientAdd.class));
            }
        });
    }

    private void startCrop(){
        CropImage.activity()
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
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (CapturePhoto) {
                    imageuri = data.getData();
                    Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
                    binding.profile.setImageBitmap(photoBitmap);
                    startCrop();
                } else {
                    uploadImageToFirebase(imageUri2);
                    binding.profile.setImageURI(imageUri2);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                imageUri2 = data.getData();
                uploadImageToFirebase(imageUri2);
                binding.profile.setImageURI(imageUri2);
                startCrop();
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

        String imageName = String.valueOf(imageUri); // Replace "example.jpg" with the desired image file name
        StorageReference imageRef = storageReference.child("images/" + imageName);


        UploadTask uploadTask = imageRef.putFile(imageUri);


        uploadTask.addOnProgressListener(taskSnapshot -> {

            long bytesTransferred = taskSnapshot.getBytesTransferred();
            long totalBytes = taskSnapshot.getTotalByteCount();

            int progress = (int) (100.0 * bytesTransferred / totalBytes);

        }).addOnSuccessListener(taskSnapshot -> {

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                // Now you have the download URL of the uploaded image
                // You can store this URL in your database or use it as needed
            });
        }).addOnFailureListener(exception -> {

            exception.printStackTrace();
        });
    }

}