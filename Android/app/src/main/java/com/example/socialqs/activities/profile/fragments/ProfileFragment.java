package com.example.socialqs.activities.profile.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.example.socialqs.R;
import com.example.socialqs.activities.create.CreateActivity;
import com.example.socialqs.activities.home.AnswerQuestionActivity;
import com.example.socialqs.activities.home.MainMenuActivity;
import com.example.socialqs.constant.Constant;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.utils.FilePath;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private ImageView profileImage, settingsBtn, cameraBtn;
    private EditText profileName;
    private ImageView setNameBtn, editNameBtn;
    private Uri imageUri = null;

    //CardView collapsedCardView, expandedCardView;
    //ImageView collapsedArrow, expandedArrow;
    //ConstraintLayout collapsedLayout, expandedLayout;

    String[] option = new String[]{"Open Camera", "Choose from Gallery"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        profileImage = view.findViewById(R.id.profileImageView);

        if (UserModel.current.profilePhoto.isEmpty()) {
            profileImage.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
        } else {
            try {
                String url = UserModel.current.profilePhoto;
                Picasso.with(getContext()).load(url).into(profileImage);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Please try again", Toast.LENGTH_LONG).show();
            }
        }

        cameraBtn = view.findViewById(R.id.cameraButton);
        settingsBtn = view.findViewById(R.id.settingsButton);

        profileName = (EditText) view.findViewById(R.id.profile_name);
        profileName.setText(UserModel.current.name);

        setNameBtn = view.findViewById(R.id.tickButton);
        editNameBtn = view.findViewById(R.id.editButton);

        //collapsedCardView = view.findViewById(R.id.collapsedCardView);
        //collapsedLayout = view.findViewById(R.id.collapsedLayout);

        //expandedCardView = view.findViewById(R.id.expandedCardView);
        //expandedLayout = view.findViewById(R.id.expandedLayout);

        //arrowToggle = view.findViewById(R.id.);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @SuppressLint("ResourceAsColor")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Change Profile Picture
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Change Profile Picture");
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

                builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //Open Settings Screen
        settingsBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_settings));

        //Edit User Name
        editNameBtn.setOnClickListener(v -> {
            profileName.setCursorVisible(true);
            profileName.setFocusableInTouchMode(true);
            profileName.getBackground().setColorFilter(R.color.black, PorterDuff.Mode.SRC_IN);
            editNameBtn.setVisibility(View.INVISIBLE);
            setNameBtn.setVisibility(View.VISIBLE);
        });

        //Sets New User Name
        setNameBtn.setOnClickListener(v -> {
            UserModel.current.name = profileName.getText().toString();
            setNameBtn.setVisibility(View.INVISIBLE);
            editNameBtn.setVisibility(View.VISIBLE);
            profileName.setBackgroundResource(android.R.color.transparent);
            profileName.setFocusable(false);
            profileName.setCursorVisible(false);

            //Close Keyboard
            InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            updateInfo();
        });
    }

    private void checkPermissionCamera() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CAMERA}, Constant.CAMERA_PERMISSION);
        } else {
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), Constant.CAPTURE_PROFILE_IMAGE);
        }
    }

    private void checkPermissionGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.FILE_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, Constant.CHOOSE_PROFILE_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.CAPTURE_PROFILE_IMAGE && resultCode == RESULT_OK && data != null) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(captureImage);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            captureImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), captureImage, "Title", null);

            imageUri = Uri.parse(path);
        }
        try {
            if (requestCode == Constant.CHOOSE_PROFILE_IMAGE && resultCode == RESULT_OK && data != null) {
                imageUri = data.getData();
                String[] FILE = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(imageUri,
                        FILE, null, null, null);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(FILE[0]);
                String ImageDecode = cursor.getString(columnIndex);
                cursor.close();

                Bitmap selectedImg = BitmapFactory.decodeFile(ImageDecode);
                profileImage.setImageBitmap(selectedImg);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Please try again", Toast.LENGTH_LONG).show();
        }

        if (data != null) {
            uploadImage();
        }
    }


    private void uploadImage() {
        String filePath = FilePath.getPath(getContext(), imageUri);
        String filename = Utilities.getInstance().getFileName();

        try {
            Utilities.getInstance().uploadFile(filename, "file:" + filePath, getContext(), new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        try {
                            NetworkHandler.getInstance().updateInfo(UserModel.current.token, UserModel.current.name, Utilities.getInstance().s3UrlString(filename), new NetworkingClosure() {
                                @Override
                                public void completion(JSONObject object, String message) {
                                    if (object == null){
                                        Utilities.getInstance().createSingleActionAlert(message, "Okay", getContext(), null).show();
                                    }else{
                                        UserModel.current.profilePhoto = Utilities.getInstance().s3UrlString(filename);
                                        Utilities.getInstance().createSingleActionAlert("Successfully updated profile picture", "Okay", getContext(), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).show();
                                        refreshDb();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            Utilities.getInstance().createSingleActionAlert(e.getLocalizedMessage(), "Okay", getContext(), null).show();
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) { }

                @Override
                public void onError(int id, Exception ex) {
                    Utilities.getInstance().createSingleActionAlert(ex.getLocalizedMessage(), "Okay", getContext(), null).show();
                }
            });
        } catch (Exception e) {
            Utilities.getInstance().createSingleActionAlert(e.getLocalizedMessage(), "Okay", getContext(), null).show();
        }

    }

    private void updateInfo(){
        try {
            NetworkHandler.getInstance().updateInfo(UserModel.current.token, UserModel.current.name, UserModel.current.profilePhoto, new NetworkingClosure() {
                @Override
                public void completion(JSONObject object, String message) {
                    if (object == null){
                        Utilities.getInstance().createSingleActionAlert(message, "Okay", getContext(), null).show();
                    }else{
                        refreshDb();
                    }
                }
            });
        } catch (Exception e) {
            Utilities.getInstance().createSingleActionAlert(e.getLocalizedMessage(), "Okay", getContext(), null).show();
        }
    }

    private void refreshDb(){
        try {
            UserModel.current.saveToDefaults(getActivity().getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Please try again", Toast.LENGTH_LONG).show();
        }
    }

}