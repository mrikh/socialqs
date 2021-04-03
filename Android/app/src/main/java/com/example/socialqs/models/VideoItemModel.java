package com.example.socialqs.models;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * Information for each video item
 */
public class VideoItemModel implements Serializable {

    private String videoID, videoURL, videoQuestion, authorName, authorImg, category;
    private int replyAmount, count;
    private boolean isBookmarked;

    public VideoItemModel(JSONObject object) throws JSONException {
        this.videoID = object.getString("_id");
        this.category = object.getJSONObject("category").getString("name");
        this.videoURL = object.getString("videoUrl");
        this.videoQuestion = object.getString("title");
        this.authorName = object.getJSONObject("creator").getString("name");
        this.authorImg = object.getJSONObject("creator").getString("profilePhoto");
        this.replyAmount = object.getInt("answerCount");
        this.isBookmarked = object.getBoolean("isBookmarked");

        count = replyAmount;
    }

    public String getVideoID(){ return videoID; }

    public String getCategory(){ return category; }

    public String getVideoURL(){ return videoURL; }

    public String getVideoQuestion(){ return videoQuestion; }

    public String getAuthorName(){ return authorName; }

    public String getAuthorImg() { return authorImg; }

    public void setVideoReplyAmount(int count){ this.count = count; }

    public int getCount(){ return count;}

    public String getVideoReplyAmount(){
        if(count == 1){
            return count + " Answer  >";
        }else{
            return count + " Answers  >";
        }
    }

    public boolean isBookmarked(){ return isBookmarked; }

    public boolean setBookmarked(boolean bookmarked){ return this.isBookmarked = bookmarked; }
}
