package com.example.socialqs.activities.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.example.socialqs.adapters.VideoRepliesAdapter;
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
 */
public class VideoRepliesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout noRepliesLayout;
    private ProgressBar progressBar;
    private ImageView answerQuestionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_replies);

        updateActionBar();

        Intent intent = getIntent();
        String videoID = intent.getStringExtra("video_id");

        recyclerView = findViewById(R.id.video_replies_recycler);
        noRepliesLayout = findViewById(R.id.no_replies_layout);

        progressBar = findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.VISIBLE);

        answerQuestionBtn = findViewById(R.id.answer_question_img_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        List<VideoRepliesModel> videoReplies = new ArrayList<>();

        NetworkHandler.getInstance().repliesListing(videoID, new NetworkingClosure() {
            @Override
            public void completion(JSONObject object, String message) {
                progressBar.setVisibility(View.INVISIBLE);
                if (object == null) {
                    Utilities.getInstance().createSingleActionAlert((message == null) ? getText(R.string.something_wrong) : message, getText(R.string.okay), getApplicationContext(), null).show();
                    return;
                }

                try {
                    JSONArray arr = object.getJSONArray("result");
                    for (int i = 0; i < arr.length(); i++) {
                        VideoRepliesModel item = new VideoRepliesModel(arr.getJSONObject(i));
                        videoReplies.add(item);
                    }

                    if(videoReplies.size() == 0){
                        noRepliesLayout.setVisibility(View.VISIBLE);
                    }else {
                        recyclerView.setAdapter(new VideoRepliesAdapter(getApplicationContext(), videoReplies, videoID));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        answerQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoOptions(videoID);
            }
        });
    }

    private void videoOptions(String videoID){
        final CharSequence[] options = { "Record Video", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoRepliesActivity.this);

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                final Intent[] myIntent = {null};

                if (options[item].equals("Record Video")) {
                    myIntent[0] = new Intent(VideoRepliesActivity.this, AnswerQuestionActivity.class);
                    myIntent[0].putExtra("videoOption", "1");

                } else if (options[item].equals("Choose from Gallery")) {
                    myIntent[0] = new Intent(VideoRepliesActivity.this, AnswerQuestionActivity.class);
                    myIntent[0].putExtra("videoOption", "2");
                } else {
                    dialog.dismiss();
                }

                myIntent[0].putExtra("questionID", videoID);
                startActivity(myIntent[0]);
            }
        });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
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
