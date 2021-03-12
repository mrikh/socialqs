package com.example.socialqs.activities.create.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.socialqs.R;
import com.example.socialqs.activities.create.CreateActivity;
import com.example.socialqs.constant.Constant;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.utils.FilePath;
import com.example.socialqs.utils.Utilities;

import java.net.URISyntaxException;

import okhttp3.internal.Util;

public class ChooseSource extends Fragment {

    Button record, pick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((CreateActivity) getActivity()).getSupportActionBar().show();
        View view = inflater.inflate(R.layout.fragment_choose_source, container, false);

        record = view.findViewById(R.id.recordVideo);
        pick = view.findViewById(R.id.pickFromGallery);

        NavController navController = NavHostFragment.findNavController(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.CAMERA}, Constant.CAMERA_PERMISSION);
                } else {
                    startRecordAction();
                }
                //Navigation.findNavController(v).navigate(R.id.onRecord);
            }
        });

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.onPick);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Constant.CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecordAction();
            }
        }
    }

    public void startRecordAction() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, Constant.CAMERA_PERMISSION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == Constant.CAMERA_PERMISSION) {

            if (data != null) {
                Uri videoUri = data.getData();
                String filePath = FilePath.getPath(getContext(), videoUri);
                ((CreateActivity) getActivity()).uploadAction(Utilities.getInstance().getFileName(), filePath);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateActionBar();
    }

    private void updateActionBar(){

        ((CreateActivity)getActivity()).setActionBarTitle("Select Source", "#ffffff", R.color.black);
        ((CreateActivity)getActivity()).updateActionBarBack(true);
    }
}

