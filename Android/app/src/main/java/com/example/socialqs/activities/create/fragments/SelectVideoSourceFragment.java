package com.example.socialqs.activities.create.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.socialqs.R;
import com.example.socialqs.activities.create.CreateActivity;
import com.example.socialqs.activities.home.MainMenuActivity;

public class SelectVideoSourceFragment extends Fragment {

    //True = Record Video
    //False = Pick from Gallery
    boolean choice = true;
    Button recordBtn, galleryBtn;
    View navView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_video_source, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((CreateActivity) getActivity()).getSupportActionBar().show();

        recordBtn = view.findViewById(R.id.recordVideo);
        galleryBtn = view.findViewById(R.id.pickFromGallery);

        recordBtn.setOnClickListener(v -> {
            choice = true; //Record Video
            navView = v;

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CAMERA},101);
            } else {
                startRecordAction();
            }
        });

        galleryBtn.setOnClickListener(v -> {
            choice = false; //Gallery Video
            navView = v;

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},102);
            }
            else {
                accessFile();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecordAction();
            }
        }
        if (requestCode == 102) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                accessFile();
            }
        }
    }

    public void accessFile() {
        Bundle arg = new Bundle();
        arg.putBoolean("choice", choice);
        Navigation.findNavController(navView).navigate(R.id.action_selectVideoSourceFragment_to_createVideoFragment, arg);
    }

    public void startRecordAction() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, 103);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 103) {

        }
    }
}

