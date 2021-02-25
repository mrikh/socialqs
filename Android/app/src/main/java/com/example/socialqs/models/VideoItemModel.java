package com.example.socialqs.models;

import org.json.JSONException;
import org.json.JSONObject;

public class VideoItemModel {

    private String videoURL, videoQuestion, authorName, authorImg, category;
    private int replyAmount;
    private boolean isBookmarked;
    private UserModel person;

    public VideoItemModel(JSONObject object) throws JSONException {
        this.category = object.getJSONObject("category").getString("name");
        this.videoURL = object.getString("videoUrl");
        this.videoQuestion = object.getString("title");
        this.authorName = object.getJSONObject("creator").getString("name");
        this.authorImg = object.getJSONObject("creator").getString("profilePhoto");
        this.replyAmount = object.getInt("answerCount");
        this.isBookmarked = object.getBoolean("isBookmarked");
    }

    public String getCategory(){ return category; }

    public String getVideoURL(){ return videoURL; }

    public String getVideoQuestion(){ return videoQuestion; }

    public String getAuthorName(){ return authorName; }

//    public int getAuthorImg(){return authorImg; }

    public String getVideoReplyAmount(){
        if(replyAmount == 1){
            return replyAmount + " Answer  >";
        }else{
            return replyAmount + " Answers  >";
        }
    }

    public boolean isBookmarked(){ return isBookmarked; }
}