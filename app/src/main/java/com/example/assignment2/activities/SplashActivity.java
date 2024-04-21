package com.example.assignment2.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment2.R;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        VideoView videoView = findViewById(R.id.splashVideoView);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.loveandfound;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        videoView.setOnCompletionListener(mediaPlayer -> {
            // When video ends, start the main activity
            startActivity(new Intent(SplashActivity.this, SignInActivity.class));
            // Finish the splash activity so it can't be returned to
            finish();
        });

        videoView.start();

    }
}