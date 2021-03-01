package com.example.socialqs.models;

import org.json.JSONException;
import org.json.JSONObject;

public class VideoRepliesModel {
    private String replyID, videoQuestionID, videoURL, authorName, authorImg;
    private int noOfLikes, noOfDislikes, createdAt;
    private boolean isCorrect;
    private UserModel person;

    public VideoRepliesModel(JSONObject object) throws JSONException {
        this.replyID = object.getString("_id");
        this.videoQuestionID = object.getString("questionId");
        this.videoURL = object.getString("videoUrl");
        this.authorName = object.getJSONObject("creator").getString("name");
        this.authorImg = object.getJSONObject("creator").getString("profilePhoto");
        this.noOfLikes = object.getInt("likes");
        this.noOfDislikes = object.getInt("dislikes");
        this.createdAt = object.getInt("createdAt");
        this.isCorrect = object.getBoolean("isCorrect");
    }

    public String getVideoQuestionID(){ return videoQuestionID; }

    public String getVideoURL(){ return videoURL; }

    public String getAuthorName(){ return authorName; }

//    public int getAuthorImg(){return authorImg; }

    public String getNoOfLikes(){
        if(noOfLikes == 1){
            return noOfLikes + " Like  >";
        }else{
            return noOfLikes + " Likes  >";
        }
    }

    public String getNoOfDislikes(){
        if(noOfDislikes == 1){
            return noOfDislikes + " Dislike  >";
        }else{
            return noOfDislikes + " Dislikes  >";
        }
    }

}
