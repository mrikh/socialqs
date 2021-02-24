package com.example.socialqs.ui.home.tabs;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.socialqs.R;
import com.example.socialqs.adapters.VideoDisplayAdapter;
import com.example.socialqs.models.VideoItem;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TabFragment extends Fragment {

    private static String categoryName;
    private static int tabNumber;
    private static List<String> categories;

    public static TabFragment newInstance(int index, String category) {
        tabNumber = index;
        categoryName = category;
        return new TabFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_home_placeholder, container, false);
        final ViewPager2 videoViewPager = root.findViewById((R.id.video_view_pager));

        List<VideoItem> videoList = new ArrayList<>();

        NetworkHandler.getInstance().questionListing(new NetworkingClosure() {
             @Override
             public void completion(JSONObject object, String message) {
                 if (object == null) {
                     Utilities.getInstance().createSingleActionAlert((message == null) ? getText(R.string.something_wrong) : message, getText(R.string.okay), getActivity(), null).show();
                     return;
                 }

                 try {
                     JSONArray arr = object.getJSONArray("result");
                     for (int i = 0; i < arr.length(); i++) {
                         VideoItem item = new VideoItem(arr.getJSONObject(i));
                         System.out.println("category: " + categoryName);
                         // TODO BELOW CODE DOES NOT WORK ON FIRST TAB
//                         if(item.category.equals(categoryName)) {
//                            videoList.add(item);
//                         }
                         videoList.add(item);
                     }
                     videoViewPager.setAdapter( new VideoDisplayAdapter(videoList, getContext()));
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         });

        return root;
    }


    // TODO OLD CODE -> KEEPING FOR REFERENCE IN CASE NEEDED -> DELETE ONCE FULLY WORKING
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        final ViewPager2 videoViewPager = view.findViewById((R.id.video_view_pager));
//
//
//        List<VideoItem> videoList = new ArrayList<>();

//        VideoItem videoItem1 = new VideoItem();
//        videoItem1.videoURL="android.resource://" + getContext().getPackageName() + "/" + R.raw.foodvideo;
//        videoItem1.videoQuestion = "What is the best way to cook pasta?";
//        videoItem1.replyAmount1 = "7 Answers  >";
//        videoItem1.authorName = "John Smith";
//        videoItem1.authorImg = R.drawable.com_facebook_profile_picture_blank_portrait;
//        videoItems.add(videoItem1);
//
//        VideoItem videoItem2 = new VideoItem();
//        videoItem2.videoURL="android.resource://" + getContext().getPackageName() + "/" + R.raw.foodvideo;
//        videoItem2.videoQuestion = "How do you solve this maths problem??";
//        videoItem2.replyAmount1 = "1 Answer  >";
//        videoItem2.authorName = "Sarah Fox";
//        videoItem2.authorImg = R.drawable.com_facebook_profile_picture_blank_portrait;
//        videoItems.add(videoItem2);

//        System.out.println("VISIBLE: " + isVisibleFrag);

//        videoViewPager.setAdapter(new VideoDisplayAdapter(videoList, isVisibleFrag));
//    }

}