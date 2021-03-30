package com.example.socialqs.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.example.socialqs.models.UserModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class Utilities {

    private static Utilities shared = null;

    public static Utilities getInstance(){
        if (shared == null){
            shared = new Utilities();
        }

        return shared;
    }

    public String getFileName(){
        return UserModel.current.id + Long.toString(System.currentTimeMillis());
    }

    public String s3UrlString(String filename){
        return "https://s3.eu-west-1.amazonaws.com/socialqs-bucket/" + filename;
    }

//    https://grokonez.com/android/uploaddownload-files-images-amazon-s3-android
    public void uploadFile(String fileName, String path, Context c, TransferListener listener) throws Exception {
        CognitoCredentialsProvider cred = new CognitoCredentialsProvider("eu-west-1:af8a10f1-f5e8-429d-bd5e-cce8d49845d0", Regions.EU_WEST_1);
        AmazonS3Client client = new AmazonS3Client(cred);

        TransferNetworkLossHandler.getInstance(c);

        TransferUtility utility = TransferUtility.builder()
                .context(c)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .s3Client(client)
                .build();

        File file = new File(new URI(path));
        TransferObserver observer = utility.upload("socialqs-bucket", fileName, file, CannedAccessControlList.PublicRead);

        observer.setTransferListener(listener);
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

    public void logout(Context c) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
