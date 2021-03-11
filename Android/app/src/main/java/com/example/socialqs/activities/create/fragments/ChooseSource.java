package com.example.socialqs.activities.create.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.socialqs.R;
import com.example.socialqs.activities.create.CreateActivity;
import com.example.socialqs.constant.Constant;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.utils.Utilities;

import java.net.URISyntaxException;

public class ChooseSource extends Fragment {

    Button record, pick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((CreateActivity) getActivity()).getSupportActionBar().show();
        View view = inflater.inflate(R.layout.fragment_choose_source, container, false);

        record = view.findViewById(R.id.recordVideo);
        pick = view.findViewById(R.id.pickFromGallery);

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

            Uri videoUri = data.getData();
            String filename = UserModel.current.id + Long.toString(System.currentTimeMillis());

            try {
                Utilities.getInstance().uploadFile(filename, videoUri.toString(), getContext(), new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        System.out.println("============= 1 ===========");
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        System.out.println("============= 2 ===========");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        System.out.println("============= 3 ===========");
                    }
                });
            } catch (URISyntaxException e) {
                Utilities.getInstance().createSingleActionAlert(e.getLocalizedMessage(), "Okay", getContext(), null).show();
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

