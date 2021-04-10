package com.example.socialqs.activities.create.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.socialqs.R;
import com.example.socialqs.activities.create.CreateActivity;
import com.example.socialqs.activities.home.AnswerQuestionActivity;
import com.example.socialqs.constant.Constant;
import com.example.socialqs.models.CategoryModel;
import com.example.socialqs.models.QuestionModel;
import com.example.socialqs.utils.FilePath;
import com.example.socialqs.utils.Utilities;

import java.util.ArrayList;

import okhttp3.internal.Util;

/**
 * User describes their question
 *      creates a title & chooses topic
 */
public class VideoDescriptionFragment extends Fragment {

    private ArrayList<CategoryModel> categories;
    private ImageView close, proceed;
    private EditText title;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_video_description, container, false);

        this.categories = ((CreateActivity) getActivity()).categories;

        for (CategoryModel model : categories){
            if (model.name.equalsIgnoreCase("all")){
                categories.remove(model);
                break;
            }
        }

        ((CreateActivity) getActivity()).getSupportActionBar().hide();

        close = view.findViewById(R.id.close);
        proceed = view.findViewById(R.id.proceed);

        title = view.findViewById(R.id.title);
        spinner = view.findViewById(R.id.spinner);

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QuestionModel question = ((CreateActivity)getActivity()).question;

                question.setqTitle(title.getText().toString());
                question.setqCategory((CategoryModel)spinner.getSelectedItem());

                if (!question.getqTitle().isEmpty() && question.getqCategory() != null) {
                    checkStoragePermission();
                    hideKeyboard();
                }else{
                    Utilities.getInstance().createSingleActionAlert("Make sure all details have been filled in", "Okay", getActivity(), null).show();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    static final Integer READ_STORAGE_PERMISSION_REQUEST_CODE=0x3;
    private void checkStoragePermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED){
                videoSource();
            }else{
                Utilities.getInstance().createSingleActionAlert("Permission to read storage necessary", "Okay", getContext(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                READ_STORAGE_PERMISSION_REQUEST_CODE);
                    }
                }).show();
            }
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Constant.CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(intent, Constant.CAMERA_PERMISSION);
            }
        }
    }

    //Choose Video Source: Record or Gallery
    private void videoSource(){
        final CharSequence[] options = { "Record Video", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Record Video")) {
                    //Get Camera Permission
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[] {Manifest.permission.CAMERA}, Constant.CAMERA_PERMISSION);
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        startActivityForResult(intent, Constant.CAMERA_PERMISSION);
                    }

                } else if (options[item].equals("Choose from Gallery")) {
                    Navigation.findNavController(getView()).navigate(R.id.action_videoDescription_to_videoList);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        title.clearFocus();
    }


}