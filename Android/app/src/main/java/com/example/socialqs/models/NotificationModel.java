package com.example.socialqs.models;

import android.text.format.DateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationModel {

    private String notificationID, questionID, title, message;
    private Long createdAt;

    //TODO DELETE
    private final static String LONG_TEXT = "Lorem ipsum dolor sit amet, et" +
            " alienum inciderint efficiantur nec, posse causae molestie" +
            " eos in. Ea vero praesent vix, nam soleat recusabo id." +
            " Qui ut exerci option laboramus. In habeo posse ridens quo," +
            " eligendi volutpat interesset ut est, mel nibh accusamus no." +
            " Te eam consulatu repudiare adipiscing, usu et choro quodsi euripidis.";

    //TODO DELETE
    public NotificationModel() {
        this.title = "Title";
        this.message = LONG_TEXT;
        this.createdAt = 1508484583259L;
    }

    public NotificationModel(JSONObject object) throws JSONException {
        this.notificationID = object.getString("_id");
        this.questionID = object.getString("questionId");
        this.title = object.getString("title");
        this.message = object.getString("body");
        this.createdAt = object.getLong("createdAt");
    }

    public String getNotificationID(){ return notificationID; }

    public String getQuestionID(){ return questionID; }

    public String getNotificationTitle(){ return title; }

    public String getNotificationMessage(){ return message; }

    public String getTime(){
        return getDate(createdAt);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd MMM yyyy, hh:mm a", cal).toString();
        return date;
    }

}
