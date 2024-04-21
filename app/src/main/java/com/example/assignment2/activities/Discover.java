package com.example.assignment2.activities;

import com.example.assignment2.models.User;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.assignment2.R;
import com.example.assignment2.utilities.Constants;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class Discover extends AppCompatActivity {
    private TextView name, gender, university;
    private ShapeableImageView profileImage;
    private List<User> users = new ArrayList<>();
    private int currentUserIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        // Initialize views and set onClickListeners
        initializeViews();
        setListeners();

        // Load users from Firestore
        loadUsers();
    }

    private void initializeViews() {
        name = findViewById(R.id.Name);
        gender = findViewById(R.id.Gender);
        university = findViewById(R.id.University);
        profileImage = findViewById(R.id.Profile_icon);
    }

    private void setListeners() {
        findViewById(R.id.next_icon).setOnClickListener(v -> showNextUser(false));
        findViewById(R.id.love_icon).setOnClickListener(v -> showNextUser(true));
        findViewById(R.id.search_icon).setOnClickListener(v -> startActivity(new Intent(this, Discover.class)));
        findViewById(R.id.chat_icon).setOnClickListener(v -> startActivity(new Intent(this, UsersActivity.class)));

        // Pass userId to Profile class
        String userId = getIntent().getStringExtra(Constants.KEY_USER_ID);
        findViewById(R.id.profile_icon).setOnClickListener(v -> {
            Intent profileIntent = new Intent(this, Profile.class);
            profileIntent.putExtra(Constants.KEY_USER_ID, userId);
            startActivity(profileIntent);
        });
    }


    private void loadUsers() {
        FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    user.setId(document.getId()); // Make sure the ID is set in your User class
                    users.add(user);
                }
                if (!users.isEmpty()) {
                    displayUser(users.get(currentUserIndex));
                }
            } else {
                Toast.makeText(Discover.this, "Error loading users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUser(User user) {
        name.setText(user.getName());
        gender.setText(user.getGender()); // Assuming you want to display email in the 'gender' TextView
        university.setText(user.getUniversity()); // Assuming you want to display token in the 'university' TextView

        String imageEncoded = user.getImage();
        if (imageEncoded != null) {
            byte[] decodedBytes = Base64.decode(imageEncoded, Base64.DEFAULT);
            Glide.with(this)
                    .load(decodedBytes)
                    .apply(new RequestOptions().override(560, 560))
                    .into(profileImage);
        }
    }

    private void showNextUser(boolean navigateToChat) {
        if (navigateToChat) {
            Intent intent = new Intent(Discover.this, ChatActivity.class);
            // Pass the entire User object instead of just the ID
            intent.putExtra(Constants.KEY_USER, users.get(currentUserIndex));
            startActivity(intent);
        } else {
            currentUserIndex = (currentUserIndex + 1) % users.size();
            displayUser(users.get(currentUserIndex));
        }
    }

}