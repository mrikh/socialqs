package com.example.socialqs.models;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationModel {

    private String notificationID, questionID, title, message, timeCreated;

    public NotificationModel(JSONObject object) throws JSONException {
        this.notificationID = object.getString("_id");
        this.questionID = object.getString("questionId");
        this.title = object.getString("title");
        this.message = object.getString("body");
        this.timeCreated = object.getString("createdAt");
    }

    public String getNotificationID(){ return notificationID; }

    public String getQuestionID(){ return questionID; }

    public String getNotificationTitle(){ return title; }

    public String getNotificationMessage(){ return message; }

    public String getTimeCreated(){ return timeCreated; }
}
