package com.example.socialqs.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

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

    public AlertDialog createSingleActionAlert(CharSequence body, CharSequence buttonTitle, Context context, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(body);
        builder1.setCancelable(true);

        if (listener != null){
            builder1.setPositiveButton(buttonTitle, listener);
        }else{
            builder1.setPositiveButton(buttonTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        AlertDialog alert11 = builder1.create();
        return alert11;
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
