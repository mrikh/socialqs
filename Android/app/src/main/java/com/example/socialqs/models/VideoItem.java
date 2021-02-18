package com.example.socialqs.models;

public class VideoItem {
    //TODO CHANGE TO PRIVATE
    public String videoURL, videoQuestion, authorName;
    public int authorImg, replyAmount;

    // TODO REMOVE WHEN DATABASE ADDED
    public String replyAmount1;

    // TODO ADD IN JSONObject
    public VideoItem(){

    }

    public String getVideoURL(){
        return videoURL;
    }

    public String getVideoQuestion(){
        return videoQuestion;
    }

    public String getAuthorName(){
        return authorName;
    }

    public int getAuthorImg(){
        return authorImg;
    }

    public String getVideoReplyAmount(){
        if(replyAmount == 1){
            return replyAmount + " Answer  >";
        }else{
            return replyAmount + " Answers  >";
        }
    }
}
