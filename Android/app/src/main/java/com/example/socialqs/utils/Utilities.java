package com.example.socialqs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class Utilities {

    private static Utilities shared = null;

    public static Utilities getInstance(){
        if (shared == null){
            shared = new Utilities();
        }

        return shared;
    }

    public void saveJsonObject(String key, @NotNull JSONObject json, Context c){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, json.toString());
        editor.commit();
    }

    public JSONObject fetchJsonObject(Context c, String key) throws Exception{

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        try {
            return new JSONObject(pref.getString(key, null));
        }catch (Exception e){
            throw e;
        }
    }
}
