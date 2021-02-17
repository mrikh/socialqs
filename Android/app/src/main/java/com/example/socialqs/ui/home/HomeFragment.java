package com.example.socialqs.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.socialqs.R;
import com.example.socialqs.adapters.VideoDisplayAdapter;
import com.example.socialqs.models.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private List<VideoItem> videoItems;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final ViewPager2 videoViewPager = root.findViewById((R.id.videoViewPager));
        videoItems = new ArrayList<>();

        // TODO: REMOVE HARDCODED ELEMENTS
        VideoItem videoItem1 = new VideoItem();
        videoItem1.videoURL="android.resource://" + getContext().getPackageName() + "/" + R.raw.foodvideo;
        videoItem1.postQuestion = "What is the best way to cook pasta?";
        videoItem1.authorName = "John Smith";
        videoItem1.authorImg = R.drawable.com_facebook_profile_picture_blank_portrait;
        videoItems.add(videoItem1);

        VideoItem videoItem2 = new VideoItem();
        videoItem2.videoURL="android.resource://" + getContext().getPackageName() + "/" + R.raw.foodvideo;
        videoItem2.postQuestion = "How can you solve this maths problem??";
        videoItem2.authorName = "Sarah Fox";
        videoItem2.authorImg = R.drawable.com_facebook_profile_picture_blank_portrait;
        videoItems.add(videoItem2);

        videoViewPager.setAdapter(new VideoDisplayAdapter(videoItems));
        return root;
    }
}