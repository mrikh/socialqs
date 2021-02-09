package com.example.socialqs.utils.networking;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
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

    public void signUp(String email, String password, String name, NetworkingClosure completion){

        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("name", name);
            params.put("password", password);
            performPostRequest(EndPoints.signUp, params, completion);
        }catch(Exception e){
            completion.completion(null, e.getMessage());
        }
    }

    public void login(String email, String password, NetworkingClosure completion){

        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("password", password);
            performPostRequest(EndPoints.login, params, completion);
        }catch(Exception e){
            completion.completion(null, e.getMessage());
        }
    }


    private void performPostRequest(String endpoint, JSONObject params, NetworkingClosure completion){

        AndroidNetworking.post(httpUrl + endpoint).addJSONObjectBody(params).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String code = response.get("code").toString();
                    if (code.equalsIgnoreCase("200")){
                        completion.completion(response.getJSONObject("data"), null);
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

    private void performGetRequest(String endpoint, HashMap<String, String> params, NetworkingClosure completion){

        AndroidNetworking.get(httpUrl + endpoint).addQueryParameter(params).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String code = response.get("code").toString();
                    if (code.equalsIgnoreCase("200")){
                        completion.completion(response.getJSONObject("data"), null);
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
