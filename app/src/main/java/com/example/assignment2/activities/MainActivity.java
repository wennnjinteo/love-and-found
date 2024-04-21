package com.example.assignment2.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment2.R;
import com.example.assignment2.databinding.ActivityMainBinding;
import com.example.assignment2.utilities.Constants;
import com.example.assignment2.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Base64;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private com.example.assignment2.utilities.PreferenceManager preferenceManager;
    @Override


    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        getToken();
        setListeners();
    }

    private void setListeners(){
        binding.fabNewChat.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),UsersActivity.class));
        });

    }
        private void loadUserDetails() {
            binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
            byte[] bytes = android.util.Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), android.util.Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imageProfile.setImageBitmap(bitmap);
        }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void updateToken(String token){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants. KEY_COLLECTION_USERS) .document (
                        preferenceManager.getString (Constants. KEY_USER_ID)
                );
        documentReference.update( Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Token updated successfully"))
                .addOnFailureListener(e ->   showToast(" Unable  to update token"));
    }
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
}