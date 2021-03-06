package com.example.socialqs.activities.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.example.socialqs.R;
import com.example.socialqs.activities.create.CreateActivity;
import com.example.socialqs.constant.Constant;
import com.example.socialqs.models.QuestionModel;
import com.example.socialqs.models.VideoItemModel;
import com.example.socialqs.models.VideoRepliesModel;
import com.example.socialqs.utils.FilePath;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import org.json.JSONObject;

/**
 * Answer Question Activity
 * Option to record video or choose from gallery
 */
public class AnswerQuestionActivity extends AppCompatActivity{

    private static int VIDEO_RECORD = 1;
    private static final int GET_FROM_GALLERY = 2;

    private Uri videoUri = null;
    private VideoView video;
    private ImageView backBtn, confirmBtn;
    private ProgressBar progressBar;
    private String questionID, answerCount, videoOption = "1";

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);
        getSupportActionBar().hide();

        video = findViewById(R.id.video_recording);
        backBtn = findViewById(R.id.back_btn);
        confirmBtn = findViewById(R.id.confirm_btn);

        progressBar = findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.INVISIBLE);

        videoOption = getIntent().getStringExtra("videoOption"); //1=Record; 2=Gallery
        questionID = getIntent().getStringExtra("questionID");
        answerCount = getIntent().getStringExtra("answerCount");

        //open phone camera or gallery
        if (Integer.parseInt(videoOption) == VIDEO_RECORD) {
            startActivityForResult(new Intent(MediaStore.ACTION_VIDEO_CAPTURE), VIDEO_RECORD);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("video/*");
            startActivityForResult(galleryIntent, GET_FROM_GALLERY);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == VIDEO_RECORD || requestCode == GET_FROM_GALLERY) {

            MediaController mediaController = new MediaController(video.getContext());
            mediaController.setAnchorView(video);
            video.setMediaController(mediaController);

            if(data != null){
                videoUri = data.getData();
                video.setVideoURI(videoUri);
                video.start();
            }else{
                //If user cancels gallery upload
                finish();
            }

            mediaController.requestFocus();
            video.setOnPreparedListener(mp -> {
                mediaController.show();
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

            backBtn.setOnClickListener(v -> {
                finish();
                if(requestCode == VIDEO_RECORD) {
                    startActivity(getIntent());
                }
            });

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String filePath = FilePath.getPath(getApplicationContext(), videoUri);
                    uploadVideo(Utilities.getInstance().getFileName(), filePath);
                    //stops user interrupting upload
                    btnBoolean(false);
                }
            });
        }else {
            finish();
        }
    }

    //progress bar visibility
    public void updateProgress(int visibility){
        progressBar.setVisibility(visibility);
    }

    //confirm & back button usability
    private void btnBoolean(boolean clickable){
        confirmBtn.setClickable(clickable);
        backBtn.setClickable(clickable);
    }

    //upload video answer to database
    public void uploadVideo(String filename, String filePath){
        try {
            updateProgress(View.VISIBLE);
            Utilities.getInstance().uploadFile(filename, "file:" + filePath, this, new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.IN_PROGRESS){
                        updateProgress(View.VISIBLE);
                    }else if (state == TransferState.COMPLETED) {
                        JSONObject params = new JSONObject();
                        try {
                            params.put("questionId", questionID);
                            params.put("videoUrl", Utilities.getInstance().s3UrlString(filename));

                            NetworkHandler.getInstance().createAnswer(params, new NetworkingClosure() {
                                @Override
                                public void completion(JSONObject object, String message) {
                                    updateProgress(View.INVISIBLE);
                                    if (object == null){
                                        Utilities.getInstance().createSingleActionAlert(message, "Okay", AnswerQuestionActivity.this, null).show();
                                    }else{
                                        Intent newIntent = new Intent("CreatedAnswerIntent");
                                        LocalBroadcastManager.getInstance(AnswerQuestionActivity.this).sendBroadcast(newIntent);
                                        Utilities.getInstance().createSingleActionAlert("Successfully answered the question", "Okay", AnswerQuestionActivity.this, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //update answer count on question screen
                                                Intent newIntent = new Intent("Update");
                                                newIntent.putExtra("questionID", questionID);

                                                int i = Integer.parseInt(answerCount) +1;
                                                newIntent.putExtra("count", String.valueOf(i));

                                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(newIntent);

                                                finish();
                                            }
                                        }).show();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            updateProgress(View.INVISIBLE);
                            Utilities.getInstance().createSingleActionAlert(e.getLocalizedMessage(), "Okay", AnswerQuestionActivity.this, null).show();
                            btnBoolean(true);
                        }
                    }else{
                        updateProgress(View.INVISIBLE);
                        btnBoolean(true);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) { }

                @Override
                public void onError(int id, Exception ex) {
                    updateProgress(View.INVISIBLE);
                    Utilities.getInstance().createSingleActionAlert(ex.getLocalizedMessage(), "Okay", AnswerQuestionActivity.this, null).show();
                }
            });
        } catch (Exception e) {
            Utilities.getInstance().createSingleActionAlert(e.getLocalizedMessage(), "Okay", this, null).show();
            btnBoolean(true);
        }
    }
}
