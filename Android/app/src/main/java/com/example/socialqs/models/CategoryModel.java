package com.example.socialqs.models;

import org.json.JSONException;
import org.json.JSONObject;

public class CategoryModel {

    public String name;
    public String id;

    public CategoryModel(JSONObject json) throws JSONException {
        name = json.getString("name");
        id = json.getString("_id");
    }
}
