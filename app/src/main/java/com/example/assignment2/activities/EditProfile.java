package com.example.assignment2.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment2.utilities.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;
import com.example.assignment2.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfile extends AppCompatActivity {

    private EditText nameEditText;
    private EditText genderEditText;
    private EditText universityEditText;
    private EditText emailEditText;
    private Button submitButton;
    private Button cancelButton;
    private ImageView profileImageView;
    private Button uploadImageButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile2);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize EditText fields, Buttons, and ImageView
        nameEditText = findViewById(R.id.name);
        genderEditText = findViewById(R.id.gender);
        universityEditText = findViewById(R.id.university);
        emailEditText = findViewById(R.id.email);
        submitButton = findViewById(R.id.submit_button);
        cancelButton = findViewById(R.id.cancel_button);
        profileImageView = findViewById(R.id.profile_image);
        uploadImageButton = findViewById(R.id.uploadImage);

        // Fetch and set current user data
        fetchAndSetCurrentUserData();

        // Set click listener for Submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        // Set click listener for Cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfileIntent = new Intent(EditProfile.this, Profile.class);
                startActivity(editProfileIntent);
            }
        });

        // Set click listener for Upload Image button
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void fetchAndSetCurrentUserData() {
        String userId = getIntent().getStringExtra(Constants.KEY_USER_ID);

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String gender = documentSnapshot.getString("gender");
                        String university = documentSnapshot.getString("university");
                        String email = documentSnapshot.getString("email");
                        String imageUrl = documentSnapshot.getString("image");

                        nameEditText.setText(name);
                        genderEditText.setText(gender);
                        universityEditText.setText(university);
                        emailEditText.setText(email);

                        // Load image into ImageView using Glide after decoding Base64 string
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Glide.with(this)
                                    .asBitmap()
                                    .load(decodedBitmap)
                                    .into(profileImageView);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, Constants.REQUEST_CODE_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
                uploadImageToFirebase(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase(Bitmap bitmap) {
        String userId = getIntent().getStringExtra(Constants.KEY_USER_ID);

        String encodedImage = encodeImage(bitmap); // Encode bitmap to Base64

        // Update user profile with encoded image
        db.collection("users").document(userId)
                .update(Constants.KEY_IMAGE, encodedImage)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Image uploaded and profile updated", Toast.LENGTH_SHORT).show();
                    // Refresh user data after image upload
                    fetchAndSetCurrentUserData();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Image uploaded but failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); // Adjusted quality to 100%
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void updateProfile() {
        String name = nameEditText.getText().toString().trim();
        String gender = genderEditText.getText().toString().trim();
        String university = universityEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String userId = getIntent().getStringExtra(Constants.KEY_USER_ID);

        db.collection("users").document(userId)
                .update("name", name, "gender", gender, "university", university, "email", email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully Update", Toast.LENGTH_SHORT).show();
                        Intent editProfileIntent = new Intent(EditProfile.this, Profile.class);
                        startActivity(editProfileIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Fail to update", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
