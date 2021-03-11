package com.example.socialqs.models;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationModel {

    private String notificationID, questionID, title, message, timeCreated;

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
        this.timeCreated = "12:22";
    }

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
