package com.example.socialqs.ui.home.tabs;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialqs.models.VideoItem;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();

    private LiveData<List<VideoItem>> mVideoList = Transformations.map(mIndex, new Function<Integer, List<VideoItem>>() {

        @Override
        public List<VideoItem> apply(Integer input) {
            List<VideoItem> videoList = new ArrayList<>();

//            NetworkHandler.getInstance().questionListing(new NetworkingClosure() {
//                @Override
//                public void completion(JSONObject object, String message) {
//                    try {
//                        JSONArray arr = object.getJSONArray("result");
//
//                        System.out.println("TESTING LIST: "+arr.toString());
//
//                        for (int i = 0; i < arr.length(); i++) {
//                            videoList.add(new VideoItem(arr.getJSONObject(i), link));
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
            return videoList;
        }
    });

    //Page Index
    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<List<VideoItem>> getVideos() {
        System.out.println("TESTING 5: " + mVideoList);
        return mVideoList; }

}