package com.example.socialqs.activities.profile.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.socialqs.R;
import com.example.socialqs.constant.Constant;

public class ProfileView extends Fragment {

    ImageView profileImage, settingsButton, cameraButton;
    TextView nameView;
    EditText nameEdit;
    ImageView tickButton, editButton;

    View background;

    String name = "Surya";
    int choice = 0;

    String[] option = new String[]{"Open Camera", "Open Gallery"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_profile_view, container, false);
        profileImage = view.findViewById(R.id.profileImageView);

        cameraButton = view.findViewById(R.id.cameraButton);
        settingsButton = view.findViewById(R.id.settingsButton);

        nameEdit = (EditText) view.findViewById(R.id.nameEdit);
        nameEdit.setVisibility(View.INVISIBLE);
        nameView = (TextView) view.findViewById(R.id.nameView);

        nameView.setText(name);

        tickButton = view.findViewById(R.id.tickButton);
        tickButton.setVisibility(View.INVISIBLE);
        editButton = view.findViewById(R.id.editButton);

        background = view.findViewById(R.id.profileBackground);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                updateName();

                return true;
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openDialog();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose One");
                builder.setSingleChoiceItems(option, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //choice = which;
                        dialog.dismiss();
                        if (which == 0) {
                            checkPermissionCamera();
                        } else {
                            checkPermissionGallery();
                        }
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.profileSettings);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButton.setVisibility(View.INVISIBLE);
                nameView.setVisibility(View.INVISIBLE);
                nameEdit.setText(name);
                nameEdit.setVisibility(View.VISIBLE);
                tickButton.setVisibility(View.VISIBLE);
            }
        });

        tickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName();
            }
        });
    }

    public void checkPermissionCamera() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CAMERA}, Constant.CAMERA_PERMISSION);
        } else {
            captureImage();
        }
    }

    public void checkPermissionGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.FILE_ACCESS_PERMISSION);
        } else {
            openGallery();
        }
    }

    public void captureImage() {
        Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constant.CAPTURE_PROFILE_IMAGE);
    }

    public void openGallery() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constant.CAPTURE_PROFILE_IMAGE) {

            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(captureImage);
        }
    }

    public void updateName() {
        name = nameEdit.getText().toString();
        nameEdit.setVisibility(View.INVISIBLE);
        tickButton.setVisibility(View.INVISIBLE);
        nameView.setText(name);
        nameView.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
    }

}