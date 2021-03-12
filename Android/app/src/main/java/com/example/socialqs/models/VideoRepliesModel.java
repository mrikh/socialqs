package com.example.socialqs.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoRepliesModel {
    private String replyID, videoQuestionID, videoURL, authorName, authorImg;
    private int createdAt;
    private boolean isCorrect;
    private UserModel person;
    private ArrayList<String> likes, dislikes;

    public VideoRepliesModel(JSONObject object) throws JSONException {
        this.replyID = object.getString("_id");
        this.videoQuestionID = object.getString("questionId");
        this.videoURL = object.getString("videoUrl");
        this.authorName = object.getJSONObject("creator").getString("name");
        this.authorImg = object.getJSONObject("creator").getString("profilePhoto");
        this.createdAt = object.getInt("createdAt");
        this.isCorrect = object.getBoolean("isCorrect");

        JSONArray likesJSONArray = object.getJSONArray("likes");
        JSONArray dislikesJSONArray = object.getJSONArray("dislikes");

        likes = new ArrayList<>();
        for(int i = 0; i < likesJSONArray.length(); i++) {
            likes.add(likesJSONArray.getString(i));
        }

        dislikes = new ArrayList<>();
        for(int i = 0; i < dislikesJSONArray.length(); i++) {
            dislikes.add(dislikesJSONArray.getString(i));
        }
    }

    public String getReplyID(){return replyID;}

    public String getVideoQuestionID(){ return videoQuestionID; }

    public String getVideoURL(){ return videoURL; }

    public String getAuthorName(){ return authorName; }

    public boolean hasUserLiked(){
        return likes.contains(UserModel.current.id);
    }

    public boolean hasUserDisliked(){
        return dislikes.contains(UserModel.current.id);
    }

    public void updateLikes(){

        if (likes.contains(UserModel.current.id)) {return;}
        likes.add(UserModel.current.id);
    }

    public void removeFromLike(){
        likes.remove(UserModel.current.id);
    }

    public void removeFromDisLike(){
        dislikes.remove(UserModel.current.id);
    }

    public void updateDislikes(){
        if (dislikes.contains(UserModel.current.id)) {return;}
        dislikes.add(UserModel.current.id);
    }

    public int getNoOfLikes() {
        return likes.size();
    }

    public int getNoOfDislikes() {
        return dislikes.size();
    }

    public int getCreatedAt(){ return createdAt; }

    public boolean isCorrect(){ return isCorrect; }

}
