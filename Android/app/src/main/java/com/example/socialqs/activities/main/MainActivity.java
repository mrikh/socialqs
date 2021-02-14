package com.example.socialqs.activities.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.example.socialqs.R;
import com.example.socialqs.activities.prelogin.PreLoginActivity;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    String pushToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AndroidNetworking.initialize(getApplicationContext());

        try{
            JSONObject object = Utilities.getInstance().fetchJsonObject(getApplicationContext(), "user");
            UserModel currentUser = new UserModel(object);
            UserModel.current = currentUser;
        }catch (Exception e){
            System.out.print(e.getMessage());
        }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    System.out.println(task.getException());
                    return;
                }

                // Get new FCM registration token
                String token = task.getResult();

                if (UserModel.current != null){
                    NetworkHandler.getInstance().updateInfo(token, null, null, new NetworkingClosure() {
                        @Override
                        public void completion(JSONObject object, String message) {
                            //update token on server if already logged in
                        }
                    });
                }else{

                    //store as instance variable and pass to prelogin flow
                    MainActivity.this.pushToken = token;
                }
            }
        });

        Intent myIntent;
        if(UserModel.current != null){
            //TODO: Go to landing screen instead of prelogin in try
            myIntent = new Intent(MainActivity.this, PreLoginActivity.class);
        }else{
            myIntent = new Intent(MainActivity.this, PreLoginActivity.class);
            myIntent.putExtra("pushToken", pushToken);
        }

        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
    }
}