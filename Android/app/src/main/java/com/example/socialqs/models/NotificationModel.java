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
