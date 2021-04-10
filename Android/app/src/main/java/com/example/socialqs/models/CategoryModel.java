package com.example.socialqs.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Category name and id for each item
 */
//parceable to pass them across activities easily
public class CategoryModel implements Parcelable {

    public String name;
    public String id;

    public CategoryModel(JSONObject json) throws JSONException {
        name = json.getString("name");
        id = json.getString("_id");
    }

    protected CategoryModel(Parcel in) {
        name = in.readString();
        id = in.readString();
    }

    public static final Creator<CategoryModel> CREATOR = new Creator<CategoryModel>() {
        @Override
        public CategoryModel createFromParcel(Parcel in) {
            return new CategoryModel(in);
        }

        @Override
        public CategoryModel[] newArray(int size) {
            return new CategoryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
    }

    //below method necessary to implement the model adapter for create
    @Override
    public String toString() {
        return name;
    }
}
