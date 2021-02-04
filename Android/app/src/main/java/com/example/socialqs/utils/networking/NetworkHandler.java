package com.example.socialqs.utils.networking;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;

import org.json.JSONObject;

import java.util.HashMap;

public class NetworkHandler {

    private static NetworkHandler shared = null;
    private static String httpUrl = "";

    public static NetworkHandler getInstance(){

        if (shared == null){
            shared = new NetworkHandler();
        }

        return shared;
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
