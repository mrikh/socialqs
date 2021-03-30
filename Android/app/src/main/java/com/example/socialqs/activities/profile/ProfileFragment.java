package com.example.socialqs.activities.profile;

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
import android.view.inputmethod.InputMethod;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.socialqs.R;
import com.example.socialqs.activities.friends.FollowingViewModel;
import com.example.socialqs.constant.Constant;
import com.example.socialqs.models.UserModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private ImageView profileImage, settingsButton, cameraButton;
    private EditText profileName;
    private ImageView setNameBtn, editNameBtn;
    private View background;

    // TODO DELETE
    String url = "https://www.worldfuturecouncil.org/wp-content/uploads/2020/06/blank-profile-picture-973460_1280-1-300x300.png";

    //CardView collapsedCardView, expandedCardView;
    //ImageView collapsedArrow, expandedArrow;
    //ConstraintLayout collapsedLayout, expandedLayout;

    String[] option = new String[]{"Open Camera", "Open Gallery"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        profileImage = view.findViewById(R.id.profileImageView);

        if (UserModel.current.profilePhoto.isEmpty()) {
            Picasso.with(getContext()).load(url).into(profileImage);
        } else {
            try {
                byte[] encodeByte = Base64.decode(UserModel.current.profilePhoto, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                        encodeByte.length);
                profileImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Please try again", Toast.LENGTH_LONG)
                        .show();
            }
        }

        cameraButton = view.findViewById(R.id.cameraButton);
        settingsButton = view.findViewById(R.id.settingsButton);

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

    @SuppressLint("ResourceAsColor")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Change Profile Picture
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

                builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //Open Settings Screen
        settingsButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_settings));

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

            refreshDb();
        });
    }

    private void checkPermissionCamera() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CAMERA}, Constant.CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constant.CAPTURE_PROFILE_IMAGE);
        }
    }

    private void checkPermissionGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.FILE_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, Constant.CHOOSE_PROFILE_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap imageBitMap = null;
        if(requestCode == Constant.CAPTURE_PROFILE_IMAGE  && resultCode == RESULT_OK
                && data != null) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            imageBitMap = captureImage;
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

                imageBitMap = BitmapFactory.decodeFile(ImageDecode);
                profileImage.setImageBitmap(imageBitMap);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Please try again", Toast.LENGTH_LONG)
                    .show();
        }
        uploadImage(imageBitMap);
    }


    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream  = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray  = byteArrayOutputStream .toByteArray();
        UserModel.current.profilePhoto = Base64.encodeToString(byteArray , Base64.DEFAULT);
        refreshDb();
    }

    private void refreshDb() {
        try {
            UserModel.current.saveToDefaults(getActivity().getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Please try again", Toast.LENGTH_LONG).show();
        }
    }
}