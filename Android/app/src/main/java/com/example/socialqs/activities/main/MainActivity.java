package com.example.socialqs.activities.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.androidnetworking.AndroidNetworking;
import com.example.socialqs.R;
import com.example.socialqs.activities.prelogin.PreLoginActivity;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.utils.Utilities;

import org.json.JSONObject;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AndroidNetworking.initialize(getApplicationContext());

        Intent myIntent;
        try{
            JSONObject object = Utilities.getInstance().fetchJsonObject(getApplicationContext(), "user");
            UserModel currentUser = new UserModel(object);
            UserModel.current = currentUser;
            //TODO: Go to landing screen instead of prelogin in try
            myIntent = new Intent(MainActivity.this, PreLoginActivity.class);
        }catch (Exception e){
            //no user model so go to login
            myIntent = new Intent(MainActivity.this, PreLoginActivity.class);
        }
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
    }
}