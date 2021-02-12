package com.example.socialqs.utils.networking;

import com.androidnetworking.AndroidNetworking;
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

    public void signUp(String email, String password, String name, String socialId, String profilePhoto, NetworkingClosure completion){

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

    private void performPostRequest(String endpoint, JSONObject params, NetworkingClosure completion){

        AndroidNetworking.post(httpUrl + endpoint).addJSONObjectBody(params).addHeaders("Authorization", UserModel.networkingHeader()).build().getAsJSONObject(new JSONObjectRequestListener() {
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
                        completion.completion(null, anError.getErrorDetail());
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
