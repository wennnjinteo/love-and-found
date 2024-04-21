package com.example.assignment2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.assignment2.R;
import com.example.assignment2.databinding.ActivitySignIn2Binding;
import com.example.assignment2.utilities.Constants;
import com.example.assignment2.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignIn2Binding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivitySignIn2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }


    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetails()){
                signIn();
            }
        });
    }
    private void signIn() {


        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String userId = documentSnapshot.getId();
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), Discover.class);
                        intent.putExtra(Constants.KEY_USER_ID, userId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });
    }

    private void loading (Boolean isLoading){
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        } else {
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }

    }



    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void addDataToFirestore () {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("first_name", "Chirag");
        data.put("last_name", "lee");
        database.collection("users")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Data Inserted", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private Boolean isValidSignInDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;

        } else {
            return true;
        }
    }

}