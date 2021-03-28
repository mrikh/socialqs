package com.example.socialqs.activities.profile.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.socialqs.R;
import com.example.socialqs.constant.Constant;

import static android.app.Activity.RESULT_OK;

public class ProfileView extends Fragment {

    ImageView profileImage, settingsButton, cameraButton;
    TextView nameView;
    EditText nameEdit;
    ImageView tickButton, editButton;

    View background;

    //CardView collapsedCardView, expandedCardView;
    //ImageView collapsedArrow, expandedArrow;
    //ConstraintLayout collapsedLayout, expandedLayout;

    String name = "Name";

    int image = R.drawable.ic_empty_profile;

    String[] option = new String[]{"Open Camera", "Open Gallery"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_profile_view, container, false);
        //TODO: Get profile image from the server
        profileImage = view.findViewById(R.id.profileImageView);
        profileImage.setImageResource(image);

        cameraButton = view.findViewById(R.id.cameraButton);
        settingsButton = view.findViewById(R.id.settingsButton);

        nameEdit = (EditText) view.findViewById(R.id.nameEdit);
        nameEdit.setVisibility(View.INVISIBLE);
        nameView = (TextView) view.findViewById(R.id.nameView);

        //TODO: Get name of the user from the server
        nameView.setText(name);

        tickButton = view.findViewById(R.id.tickButton);
        tickButton.setVisibility(View.INVISIBLE);
        editButton = view.findViewById(R.id.editButton);

        background = view.findViewById(R.id.profileBackground);

        //collapsedCardView = view.findViewById(R.id.collapsedCardView);
        //collapsedLayout = view.findViewById(R.id.collapsedLayout);

        //expandedCardView = view.findViewById(R.id.expandedCardView);
        //expandedLayout = view.findViewById(R.id.expandedLayout);

        //arrowToggle = view.findViewById(R.id.);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose One");
                builder.setSingleChoiceItems(option, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                editName();
            }
        });

        tickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Upload new name to the server
                updateName();
            }
        });

    }

    public void checkPermissionCamera() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CAMERA}, Constant.CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constant.CAPTURE_PROFILE_IMAGE);
        }
    }

    public void checkPermissionGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.FILE_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(intent, Constant.CHOOSE_PROFILE_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO: upload the image to the server
        if(requestCode == Constant.CAPTURE_PROFILE_IMAGE  && resultCode == RESULT_OK
                && data != null) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(captureImage);
        }
        try {
            if(requestCode == Constant.CHOOSE_PROFILE_IMAGE  && resultCode == RESULT_OK
                    && data != null) {
                Uri URI = data.getData();
                String[] FILE = { MediaStore.Images.Media.DATA };


                Cursor cursor = getActivity().getContentResolver().query(URI,
                        FILE, null, null, null);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(FILE[0]);
                String ImageDecode = cursor.getString(columnIndex);
                cursor.close();

                //TODO: No image shown
                profileImage.setImageBitmap(BitmapFactory
                        .decodeFile(ImageDecode));
            }
        } catch (Exception e) {

            Toast.makeText(getContext(), "Please try again", Toast.LENGTH_LONG)
                    .show();
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

    public void editName() {
        editButton.setVisibility(View.INVISIBLE);
        nameView.setVisibility(View.INVISIBLE);
        nameEdit.setText(name);
        nameEdit.setVisibility(View.VISIBLE);
        tickButton.setVisibility(View.VISIBLE);
    }
}