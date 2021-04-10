package com.example.socialqs.utils.networking;

public class EndPoints{

    public static String signUp = "users/signUp";
    public static String login = "users/login";
    public static String resendVerification = "users/resendVerification";
    public static String verifyEmail = "users/verifyEmail";
    public static String forgotPass = "users/forgotPass";
    public static String resetPass = "users/resetPassword";
    public static String updateInfo = "users/updateInfo";
    public static String categoryList = "questions/category";
    public static String questionList = "questions/list";
    public static String createQuestions = "questions/create";
    public static String notificationList = "notifications";
    public static String deleteNotification = "notifications/id/{id}";
    public static String deleteAllNotifications = "notifications/all";
    public static String deleteAnswer = "answers/{id}";
    public static String repliesList = "answers/list";
    public static String updateAnswer = "answers/update";
    public static String createAnswer = "answers/answer";
    public static String bookmarkQuestion = "questions/bookmark";
    public static String questionDetails = "questions/details";
    public static String deleteQuestion = "questions/{id}";

}
