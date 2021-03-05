package com.example.socialqs.activities.create.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.example.socialqs.R;
import com.example.socialqs.activities.create.CreateActivity;

public class CreateVideoFragment extends Fragment {

    boolean choice = false;
    VideoView videoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        choice = (boolean) getArguments().get("choice");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_video, container, false);
        videoView = view.findViewById(R.id.create_VideoView);

        ((CreateActivity) getActivity()).getSupportActionBar().hide();

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 101) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 103) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            videoView.setVideoURI(data.getData());
            videoView.start();
        }
    }
}