package com.example.socialqs.activities.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.socialqs.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().hide();
    }
}