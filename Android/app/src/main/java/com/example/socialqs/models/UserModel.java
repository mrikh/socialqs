package com.example.socialqs.models;

import android.content.Context;

import com.example.socialqs.utils.Utilities;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class UserModel {

    public static UserModel current;

    boolean emailVerified;
    String profilePhoto;
    String id;
    String name;
    String email;
    String token;

    public UserModel(JSONObject json) throws JSONException {
        emailVerified = json.getBoolean("emailVerified");
        profilePhoto = json.getString("profilePhoto");
        id = json.getString("_id");
        name = json.getString("name");
        email = json.getString("email");
        token = json.getString("token");
    }

    public void saveToDefaults(Context c) throws JSONException {

        JSONObject json = new JSONObject();
        json.put("emailVerified", emailVerified);
        json.put("profilePhoto", profilePhoto);
        json.put("_id", id);
        json.put("name", name);
        json.put("email", email);
        json.put("token", token);

        Utilities.getInstance().saveJsonObject("user", json, c);
    }
}
