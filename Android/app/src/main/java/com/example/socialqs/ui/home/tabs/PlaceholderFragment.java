package com.example.socialqs.ui.home.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.example.socialqs.R;
import com.example.socialqs.adapters.VideoDisplayAdapter;
import com.example.socialqs.models.VideoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private boolean isVisibleFrag = false;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_placeholder, container, false);

        final ViewPager2 videoViewPager = root.findViewById((R.id.video_view_pager));
        List<VideoItem> videoItems = new ArrayList<>();

        // TODO: REMOVE HARDCODED ELEMENTS
        VideoItem videoItem1 = new VideoItem();
        videoItem1.videoURL="android.resource://" + getContext().getPackageName() + "/" + R.raw.foodvideo;
        videoItem1.videoQuestion = "What is the best way to cook pasta?";
        videoItem1.replyAmount1 = "7 Answers  >";
        videoItem1.authorName = "John Smith";
        videoItem1.authorImg = R.drawable.com_facebook_profile_picture_blank_portrait;
        videoItems.add(videoItem1);

        VideoItem videoItem2 = new VideoItem();
        videoItem2.videoURL="android.resource://" + getContext().getPackageName() + "/" + R.raw.foodvideo;
        videoItem2.videoQuestion = "How do you solve this maths problem??";
        videoItem2.replyAmount1 = "1 Answer  >";
        videoItem2.authorName = "Sarah Fox";
        videoItem2.authorImg = R.drawable.com_facebook_profile_picture_blank_portrait;
        videoItems.add(videoItem2);

        System.out.println("VISIBLE: " + isVisibleFrag);

        videoViewPager.setAdapter(new VideoDisplayAdapter(videoItems, isVisibleFrag));
        return root;
    }

}