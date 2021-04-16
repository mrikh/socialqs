package com.example.socialqs.utils.networking;

import android.net.Network;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Method;
import com.androidnetworking.common.RequestBuilder;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Wrapper class created to handle networking boiler plate code. This is used as a singleton class since almost every screen has a networking call
 * and having to instantiate a class object every time is an unnecessary overhead that will consume resources.
 */
public class NetworkHandler {

    //Made static to ensure just one isntance and global access
    private static NetworkHandler shared = null;
    //The server url
    private static String httpUrl = "https://socialqs-android-app.herokuapp.com/";

    public static NetworkHandler getInstance(){

        if (shared == null){
            shared = new NetworkHandler();
        }

        return shared;
    }

    /**
     * Api to update the answer status on the server.
     * @param params Body params to send to the server containing updated information
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void updateAnswer(JSONObject params, NetworkingClosure completion){
        performPostRequest(EndPoints.updateAnswer, params, completion);
    }

    /**
     * Api to bookmark the question for the particular user
     * @param params Body params to send to the server containing updated information
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void bookmarkQuestion(JSONObject params, NetworkingClosure completion){
        performPostRequest(EndPoints.bookmarkQuestion, params, completion);
    }

    /**
     * Api to create a question on the server
     * @param params Body params to send to the server containing updated information
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void createQuestion(JSONObject params, NetworkingClosure completion){
        performPostRequest(EndPoints.createQuestions, params, completion);
    }

    /**
     * Api to update user info on the server
     * @param pushToken Push Token information to update
     * @param name Name of the user in case they decide to change it
     * @param profilePhoto Update profile photo
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void updateInfo(String pushToken, String name, String profilePhoto, NetworkingClosure completion){

        try {
            JSONObject params = new JSONObject();
            if (pushToken != null){
                params.put("pushToken", pushToken);
            }

            if (name != null){
                params.put("name", name);
            }

            if (profilePhoto != null){
                params.put("profilePhoto", profilePhoto);
            }

            performPostRequest(EndPoints.updateInfo, params, completion);
        }catch(Exception e){
            completion.completion(null, e.getMessage());
        }
    }

    /**
     * Api to create a new user on the server in our database
     * @param email Email id of the user
     * @param password Password of the user
     * @param name Name of the user
     * @param socialId Social id in case sign up happens via social media
     * @param profilePhoto Profile photo of the user
     * @param pushToken Push notification token of the user's device
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void signUp(String email, String password, String name, String socialId, String profilePhoto, String pushToken, NetworkingClosure completion){

        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("name", name);
            params.put("password", password);
            if (socialId != null){
                params.put("socialId", socialId);
            }
            if (profilePhoto != null){
                params.put("profilePhoto", profilePhoto);
            }
            if (pushToken != null){
                params.put("pushToken", pushToken);
            }

            performPostRequest(EndPoints.signUp, params, completion);
        }catch(Exception e){
            completion.completion(null, e.getMessage());
        }
    }

    /**
     * Api to login a user
     * @param object Body params to send to the server containing updated information
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void login(JSONObject object, NetworkingClosure completion){
        performPostRequest(EndPoints.login, object, completion);
    }

    /**
     * Api to resend verification email
     * @param email Email to send to
     * @param isForgot If the email is for the forgot password flow
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void resendVerification(String email, boolean isForgot, NetworkingClosure completion){

        try {
            JSONObject params = new JSONObject();
            params.put("isForgot", isForgot);
            params.put("email", email);
            performPostRequest(EndPoints.resendVerification, params, completion);
        }catch(Exception e){
            completion.completion(null, e.getMessage());
        }
    }

    /**
     * Api to send forgot password email
     * @param email Email to send the information to
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void forgotPassword(String email, NetworkingClosure completion){

        try{
            JSONObject params = new JSONObject();
            params.put("email", email);
            performPostRequest(EndPoints.forgotPass, params, completion);
        }catch (Exception e){
            completion.completion(null, e.getMessage());
        }
    }

    /**
     * Api to update the user's password
     * @param email Email to update the password for
     * @param password Password to set
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void resetPassword(String email, String password, NetworkingClosure completion){

        try{
            JSONObject object = new JSONObject();
            object.put("password", password);
            object.put("email", email);
            performPostRequest(EndPoints.resetPass, object, completion);
        }catch(Exception e){
            completion.completion(null, e.getMessage());
        }
    }

    /**
     * Api to verify that the correct otp has been entered
     * @param email Email to validate against otp
     * @param otp OTP to validate
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void verifyEmail(String email, String otp, NetworkingClosure completion){
        try{
            JSONObject object = new JSONObject();
            object.put("otp", otp);
            object.put("email", email);
            performPostRequest(EndPoints.verifyEmail, object, completion);
        }catch(Exception e){
            completion.completion(null, e.getMessage());
        }
    }

    /**
     * Api to fetch all categories from the server
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void categoryListing(NetworkingClosure completion){
        performGetRequest(EndPoints.categoryList, new HashMap<>(), completion);
    }

    /**
     * Api to get list of questions from the server
     * @param searchString A particular pieces of text that the user searched for.
     * @param categoryId Category for which you want the data. Pass Null to fetch questions for all categories
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void questionListing(String searchString, String categoryId, NetworkingClosure completion){

        HashMap hashmap = new HashMap<>();
        if (categoryId != null) {
            hashmap.put("categoryId", categoryId);
        }
        hashmap.put("search", searchString);

        if (UserModel.current != null){
            hashmap.put("email", UserModel.current.email);
        }

        performGetRequest(EndPoints.questionList, hashmap, completion);
    }

    /**
     * Api to fetch details of a particular question
     * @param id Id of the question to fetch
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void questionDetails(String id, NetworkingClosure completion){
        HashMap hashmap = new HashMap<>();
        hashmap.put("id", id);

        if (UserModel.current != null){
            hashmap.put("email", UserModel.current.email);
        }

        performGetRequest(EndPoints.questionDetails, hashmap, completion);
    }

    /**
     * Fetch all replies for a question
     * @param questionId Question id to fetch replies for
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void repliesListing(String questionId, NetworkingClosure completion){
        HashMap hashmap = new HashMap<>();
        hashmap.put("questionId", questionId);

        performGetRequest(EndPoints.repliesList, hashmap, completion);
    }

    /**
     * Delete an answer from the server
     * @param answerID Answer id to delete
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void deleteAnswer(String answerID, NetworkingClosure completion){
        HashMap hashmap = new HashMap<>();
        hashmap.put("id", answerID);

        performDeleteRequest(EndPoints.deleteAnswer, hashmap, completion);
    }

    /**
     * Delete the question from the server
     * @param id Question id to delete
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void deleteQuestion(String id, NetworkingClosure completion){
        HashMap hashmap = new HashMap<>();
        hashmap.put("id", id);

        performDeleteRequest(EndPoints.deleteQuestion, hashmap, completion);
    }

    /**
     * Fetch all the notifications of a particular user
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void notificationListing(NetworkingClosure completion){
        performGetRequest(EndPoints.notificationList, new HashMap<>(), completion);
    }

    /**
     * Delete a particular notification
     * @param notificationID Notificatoin Id to delete
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void deleteNotification(String notificationID, NetworkingClosure completion){
        HashMap hashmap = new HashMap<>();
        hashmap.put("id", notificationID);

        performDeleteRequest(EndPoints.deleteNotification, hashmap, completion);
    }

    /**
     * Delete all of the users notifications
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void deleteAllNotifications(NetworkingClosure completion){
        performDeleteRequest(EndPoints.deleteAllNotifications, new HashMap<>(), completion);
    }

    /**
     * Add an answer to a particular question
     * @param params Body params to send to the server containing updated information
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    public void createAnswer(JSONObject params, NetworkingClosure completion) {
        performPostRequest(EndPoints.createAnswer, params, completion);
    }

    /**
     * Private convenience method to perform a POST request. Helps avoid having to write the same code in each method above
     * @param endpoint Endpoint to perform request on
     * @param params Params to send to the server
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    private void performPostRequest(String endpoint, JSONObject params, NetworkingClosure completion){

        performBodyRequest(AndroidNetworking.post(httpUrl + endpoint).addJSONObjectBody(params), completion);
    }

    /**
     * Private convenience method to perform a DELETE request.
     * @param endpoint Endpoint to perform request on
     * @param params Params to send to the server
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    private void performDeleteRequest(String endpoint, HashMap<String, String> params, NetworkingClosure completion){

        if (params != null){
            performBodyRequest(AndroidNetworking.delete(httpUrl + endpoint ).addPathParameter(params), completion);
        }else{
            performBodyRequest(AndroidNetworking.delete(httpUrl + endpoint), completion);
        }
    }

    /**
     * Private method to perform a GET request
     * @param endpoint Endpoint to make the request for
     * @param params Query Parameters to pass and make the request
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    private void performGetRequest(String endpoint, HashMap<String, String> params, NetworkingClosure completion){

        AndroidNetworking.get(httpUrl + endpoint).addQueryParameter(params).addHeaders("Authorization", UserModel.networkingHeader()).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String code = response.get("code").toString();
                    if (code.equalsIgnoreCase("200")){
                        boolean hasData = response.has("data");
                        if (hasData) {
                            completion.completion(response.getJSONObject("data"), response.get("message").toString());
                        }else{
                            completion.completion(new JSONObject(), response.get("message").toString());
                        }
                    }else{
                        completion.completion(null, response.get("message").toString());
                    }
                }catch (Exception e){
                    completion.completion(null, e.getMessage());
                }
            }

            @Override
            public void onError(ANError anError) {
                if (anError != null){
                    try {
                        completion.completion(null, anError.getMessage());
                    }catch (Exception e){
                        completion.completion(null, null);
                    }
                }else{
                    completion.completion(null, null);
                }
            }
        });
    }

    /**
     * POST and DELETE requests have a common structure so this method is used to actually make the requests
     * @param builder Builder object containing information depending on DELETE or POST request
     * @param completion Interface to handle request completion status and get the response object or error back
     */
    private void performBodyRequest(ANRequest.PostRequestBuilder builder, NetworkingClosure completion){

        builder.addHeaders("Authorization", UserModel.networkingHeader()).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String code = response.get("code").toString();
                    if (code.equalsIgnoreCase("200")){
                        boolean hasData = response.has("data");
                        if (hasData) {
                            //since in closures we check if object is null, we add in dummy object for
                            // success as we never use it for the apis that return empty object anyways
                            completion.completion(response.getJSONObject("data"), response.get("message").toString());
                        }else{
                            completion.completion(new JSONObject(), response.get("message").toString());
                        }
                    }else{
                        completion.completion(null, response.get("message").toString());
                    }
                }catch (Exception e){
                    completion.completion(null, e.getMessage());
                }
            }

            @Override
            public void onError(ANError anError) {
                if (anError != null){
                    try {
                        JSONObject errorBody = new JSONObject(anError.getErrorBody());
                        String message = errorBody.getString("message");
                        completion.completion(null, message);
                    }catch (Exception e){
                        completion.completion(null, null);
                    }
                }else{
                    completion.completion(null, null);
                }
            }
        });
    }
}
