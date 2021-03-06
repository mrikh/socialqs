package com.example.socialqs.activities.create.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.example.socialqs.R;

/**
 * Display video question created
 */
public class VideoDisplayFragment extends Fragment {

    VideoView videoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_video_display, container, false);
        videoView = view.findViewById(R.id.create_VideoView);

        return view;
    }
}