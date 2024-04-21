package com.example.assignment2.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.assignment2.R;
import com.example.assignment2.utilities.Constants;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private TextView name, gender, university, email;
    private ShapeableImageView profileImage;

    private Button editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Retrieve user ID from the Intent
        String userId = getIntent().getStringExtra(Constants.KEY_USER_ID);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference userRef = database.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    String namee = documentSnapshot.getString("name"); // Replace with actual field name
                    String genderr = documentSnapshot.getString("gender"); // Replace with actual field name
                    String universityy = documentSnapshot.getString("university"); // Replace with actual field name
                    String emaill = documentSnapshot.getString("email"); // Replace with actual field name
                    String imageUrl = documentSnapshot.getString("image");

                    name = findViewById(R.id.name);
                    gender = findViewById(R.id.gender);
                    university = findViewById(R.id.university);
                    email = findViewById(R.id.email);
                    profileImage = findViewById(R.id.profile_image);

                    name.setText(namee);
                    gender.setText(genderr);
                    university.setText(universityy);
                    email.setText(emaill);

                    // Decode base64 string to bitmap
                    byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
// Load decoded bitmap into ShapeableImageView using Glide
                    Glide.with(this)
                            .load(decodedBitmap)
                            .into(profileImage);


                } else {
                    Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to fetch user profile", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize your ImageViews
        ImageView searchIcon = findViewById(R.id.search_icon);
        ImageView profileIcon = findViewById(R.id.profile_icon);
        ImageView chatIcon = findViewById(R.id.chat_icon);
        editProfileButton = findViewById(R.id.editProfile);

        // Set an OnClickListener for the search icon
        searchIcon.setOnClickListener(v -> {
            // Intent to start the Discover activity
            Intent discoverIntent = new Intent(Profile.this, Discover.class);
            startActivity(discoverIntent);
        });

        // Set an OnClickListener for the chat icon
        chatIcon.setOnClickListener(v -> {
            // Intent to start the UsersActivity
            startActivity(new Intent(getApplicationContext(), UsersActivity.class));
        });

        // Set an OnClickListener for the profile icon
        profileIcon.setOnClickListener(v -> {
            // Intent to start the Profile activity
            Intent profileIntent = new Intent(Profile.this, Profile.class);
            startActivity(profileIntent);
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to EditProfile activity
                Intent editProfileIntent = new Intent(Profile.this, EditProfile.class);

                // Retrieve the userId from the Intent
                String userId = getIntent().getStringExtra(Constants.KEY_USER_ID);

                // Pass the userId to EditProfile activity using putExtra
                editProfileIntent.putExtra(Constants.KEY_USER_ID, userId);

                // Start the EditProfile activity
                startActivity(editProfileIntent);
            }
        });

    }
}
