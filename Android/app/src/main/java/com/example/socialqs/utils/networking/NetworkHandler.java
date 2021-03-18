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

public class NetworkHandler {

    private static NetworkHandler shared = null;
    private static String httpUrl = "https://socialqs-android-app.herokuapp.com/";

    public static NetworkHandler getInstance(){

        if (shared == null){
            shared = new NetworkHandler();
        }

        return shared;
    }

    public void updateAnswer(JSONObject params, NetworkingClosure completion){
        performPatchRequest(EndPoints.updateAnswer, params, completion);
    }

    public void createQuestion(JSONObject params, NetworkingClosure completion){
        performPostRequest(EndPoints.createQuestions, params, completion);
    }

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

    public void login(JSONObject object, NetworkingClosure completion){
        performPostRequest(EndPoints.login, object, completion);
    }

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

    public void forgotPassword(String email, NetworkingClosure completion){

        try{
            JSONObject params = new JSONObject();
            params.put("email", email);
            performPostRequest(EndPoints.forgotPass, params, completion);
        }catch (Exception e){
            completion.completion(null, e.getMessage());
        }
    }

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

    public void categoryListing(NetworkingClosure completion){
        performGetRequest(EndPoints.categoryList, new HashMap<>(), completion);
    }

    public void questionListing(String categoryId, NetworkingClosure completion){

        HashMap hashmap = new HashMap<>();
        if (categoryId != null) {
            hashmap.put("categoryId", categoryId);
        }
        performGetRequest(EndPoints.questionList, hashmap, completion);
    }

    public void repliesListing(String questionId, NetworkingClosure completion){
        HashMap hashmap = new HashMap<>();
        hashmap.put("questionId", questionId);

        performGetRequest(EndPoints.repliesList, hashmap, completion);
    }

    public void deleteAnswer(String answerID, NetworkingClosure completion){
        HashMap hashmap = new HashMap<>();
        hashmap.put("id", answerID);

        performDeleteRequest(EndPoints.deleteAnswer, hashmap, completion);
    }

    public void notificationListing(NetworkingClosure completion){
        performGetRequest(EndPoints.notificationList, new HashMap<>(), completion);
    }

    public void deleteNotification(String notificationID, NetworkingClosure completion){
        HashMap hashmap = new HashMap<>();
        hashmap.put("id", notificationID);

        performDeleteRequest(EndPoints.deleteNotification, hashmap, completion);
    }

    public void deleteAllNotifications(NetworkingClosure completion){
        performDeleteRequest(EndPoints.deleteAllNotifications, new HashMap<>(), completion);
    }

    public void createAnswer(JSONObject params, NetworkingClosure completion) {
        performPostRequest(EndPoints.createAnswer, params, completion);
    }


    private void performPostRequest(String endpoint, JSONObject params, NetworkingClosure completion){

        performBodyRequest(AndroidNetworking.post(httpUrl + endpoint).addJSONObjectBody(params), completion);
    }

    private void performPatchRequest(String endpoint, JSONObject params, NetworkingClosure completion){

        performBodyRequest(AndroidNetworking.patch(httpUrl + endpoint).addJSONObjectBody(params), completion);
    }

    private void performDeleteRequest(String endpoint, HashMap<String, String> params, NetworkingClosure completion){

        if (params != null){
            performBodyRequest(AndroidNetworking.delete(httpUrl + endpoint ).addPathParameter(params), completion);
        }else{
            performBodyRequest(AndroidNetworking.delete(httpUrl + endpoint), completion);
        }
    }

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