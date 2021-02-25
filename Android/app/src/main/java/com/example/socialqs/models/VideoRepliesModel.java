package com.example.socialqs.models;

public class VideoRepliesModel {
    public String videoURL, authorName;
    public int authorImg, videoQuestionID, noOfLikes, noOfDislikes;
    private UserModel person;

    public VideoRepliesModel(){

    }

    public int getVideoQuestionID(){ return videoQuestionID; }

    public String getVideoURL(){ return videoURL; }

    public String getAuthorName(){ return authorName; }

    public int getNoOfLikes(){ return noOfLikes; }

    public int getNoOfDislikes(){ return noOfDislikes; }

//    public String getNoOfLikes(){
//        if(noOfLikes == 1){
//            return noOfLikes + " Like  >";
//        }else{
//            return noOfLikes + " Likes  >";
//        }
//    }
//
//    public String getNoOfDislikes(){
//        if(noOfDislikes == 1){
//            return noOfDislikes + " Dislike  >";
//        }else{
//            return noOfDislikes + " Dislikes  >";
//        }
//    }

}
