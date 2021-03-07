package com.example.socialqs.activities.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialqs.R;

public class AnswerQuestionActivity extends AppCompatActivity{

    private static int VIDEO_RECORD = 1;
    private static final int GET_FROM_GALLERY = 2;
    private Uri videoUri = null;
    private VideoView video;
    private ImageView backBtn, confirmBtn;

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);
        getSupportActionBar().hide();

        video = findViewById(R.id.video_recording);
        backBtn = findViewById(R.id.back_btn);
        confirmBtn = findViewById(R.id.confirm_btn);
        String code = "1";
        code = getIntent().getStringExtra("videoOption");

        if(Integer.parseInt(code) == VIDEO_RECORD){
            startActivityForResult(new Intent(MediaStore.ACTION_VIDEO_CAPTURE), VIDEO_RECORD);
        }else{
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("video/*");
            startActivityForResult(galleryIntent, GET_FROM_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == VIDEO_RECORD || requestCode == GET_FROM_GALLERY) {
            MediaController mediaController = new MediaController(video.getContext()){
                @Override
                public void show(int timeout){
                    if(timeout == 3000) timeout = 200000; //Set to desired number
                    super.show(timeout);
                }
            };
            mediaController.setAnchorView(video);
            video.setMediaController(mediaController);

            //If user cancels gallery upload
            if(data != null){
                videoUri = data.getData();
                video.setVideoURI(videoUri);
                video.start();
            }else{
                finish();
            }

            mediaController.requestFocus();
            video.setOnPreparedListener(mp -> {mediaController.show();
                int duration=mp.getDuration()/1000;
                int hours = duration / 3600;
                int minutes = (duration / 60) - (hours * 60);
                int seconds = duration - (hours * 3600) - (minutes * 60);

                //if video is too short
                if(seconds <= 2){
                    finish();
                    startActivity(getIntent());
                    Toast.makeText(this, "Video must be longer than 2 seconds", Toast.LENGTH_LONG).show();
                }
            });

            video.setOnCompletionListener(mp -> mediaController.show());

            backBtn.setOnClickListener(v -> {
                finish();
                if(requestCode == VIDEO_RECORD) {
                    startActivity(getIntent());
                }
            });

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO SAVE ANSWER TO DATABASE
                    finish();
                }
            });
        }else {
            finish();
        }
    }
}
