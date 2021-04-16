package com.example.socialqs.activities.home;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.example.socialqs.adapters.NotificationAdapter;
import com.example.socialqs.adapters.VideoDisplayAdapter;
import com.example.socialqs.adapters.VideoRepliesAdapter;
import com.example.socialqs.constant.Constant;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.models.VideoItemModel;
import com.example.socialqs.models.VideoRepliesModel;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Video post replies activity
 *
 * Displays all video answers
 */
public class VideoRepliesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout noRepliesLayout;
    private ProgressBar progressBar;
    private ImageView answerQuestionBtn;
    private VideoRepliesAdapter adapter;
    private String videoID;
    private List<VideoRepliesModel> videoReplies;

    //refresh answer list when new answer created
    private BroadcastReceiver answerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && (intent.getAction().equalsIgnoreCase("CreatedAnswerIntent"))){
                fetchData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_replies);

        updateActionBar();

        Intent intent = getIntent();
        videoID = intent.getStringExtra("video_id");

        recyclerView = findViewById(R.id.video_replies_recycler);
        noRepliesLayout = findViewById(R.id.no_replies_layout);

        progressBar = findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.VISIBLE);

        answerQuestionBtn = findViewById(R.id.answer_question_img_view);

        //recycler view set up
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        fetchData();

        //answer a question
        answerQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserModel.current == null){
                    Utilities.getInstance().createSingleActionAlert("You need to login before using this feature.", "Okay", getApplicationContext(), null).show();
                }else {
                    videoOptions(videoID);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(answerReceiver, new IntentFilter("CreatedAnswerIntent"));
    }

    //get list of question answers from database
    private void fetchData(){
        videoReplies = new ArrayList<>();

        NetworkHandler.getInstance().repliesListing(videoID, (object, message) -> {
            progressBar.setVisibility(View.INVISIBLE);
            if (object == null) {
                Utilities.getInstance().createSingleActionAlert((message == null) ? getText(R.string.something_wrong) : message, getText(R.string.okay), getApplicationContext(), null).show();
                return;
            }

            try {
                //Question answer list
                JSONArray arr = object.getJSONArray("result");
                for (int i = 0; i < arr.length(); i++) {
                    VideoRepliesModel item = new VideoRepliesModel(arr.getJSONObject(i));
                    videoReplies.add(item);
                }

                if(videoReplies.size() == 0){
                    noRepliesLayout.setVisibility(View.VISIBLE);
                }else {
                    noRepliesLayout.setVisibility(View.INVISIBLE);
                    //Update layout when list size changes (added or deleted)
                    adapter = new VideoRepliesAdapter(getApplicationContext(), videoReplies);
                    adapter.setOnDataChangeListener(size -> {
                        if(size == 0){
                            noRepliesLayout.setVisibility(View.VISIBLE);
                        }else{
                            noRepliesLayout.setVisibility(View.INVISIBLE);
                        }
                    });
                    recyclerView.setAdapter(adapter);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void videoOptions(String videoID){
        //Answer Video Options
        final CharSequence[] options = { "Record Video", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoRepliesActivity.this);

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                final Intent[] myIntent = {null};

                if (options[item].equals("Record Video")) {
                    if(checkStoragePermission("Record Video")) {
                        myIntent[0] = new Intent(VideoRepliesActivity.this, AnswerQuestionActivity.class);
                        myIntent[0].putExtra("videoOption", "1");
                        myIntent[0].putExtra("questionID", videoID);
                        myIntent[0].putExtra("answerCount", String.valueOf(videoReplies.size()));
                        startActivity(myIntent[0]);
                    }

                } else if (options[item].equals("Choose from Gallery")) {
                    if(checkStoragePermission("Choose from Gallery")) {
                        myIntent[0] = new Intent(VideoRepliesActivity.this, AnswerQuestionActivity.class);
                        myIntent[0].putExtra("videoOption", "2");
                        myIntent[0].putExtra("questionID", videoID);
                        myIntent[0].putExtra("answerCount", String.valueOf(videoReplies.size()));
                        startActivity(myIntent[0]);
                    }

                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private boolean checkStoragePermission(String option) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (option.equalsIgnoreCase("Record Video")) {
                int camera = getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA);

                if (camera == PackageManager.PERMISSION_GRANTED) {
                    return true;

                } else {
                    Utilities.getInstance().createSingleActionAlert("Permission to access camera required", "Okay", VideoRepliesActivity.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(VideoRepliesActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.CAMERA_PERMISSION);
                        }
                    }).show();
                }
            }else if (option.equalsIgnoreCase("Choose from Gallery")) {
                int storage = getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

                if (storage == PackageManager.PERMISSION_GRANTED) {
                    return true;

                } else {
                    Utilities.getInstance().createSingleActionAlert("Permission to access storage required", "Okay", VideoRepliesActivity.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(VideoRepliesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.FILE_ACCESS_PERMISSION);
                        }
                    }).show();
                }
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            Intent newIntent = new Intent("Update");
            newIntent.putExtra("questionID", videoID);
            newIntent.putExtra("count", String.valueOf(videoReplies.size()));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(newIntent);

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateActionBar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
    }

}
