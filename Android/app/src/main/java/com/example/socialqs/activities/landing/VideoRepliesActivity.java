package com.example.socialqs.activities.landing;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.example.socialqs.adapters.VideoRepliesAdapter;
import com.example.socialqs.models.VideoRepliesModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Video post replies activity
 */
public class VideoRepliesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_replies);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.video_replies_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        List<VideoRepliesModel> videoReplies = new ArrayList<>();

        VideoRepliesModel videoItem1 = new VideoRepliesModel();
        videoItem1.videoURL= url;
        videoItem1.authorName = "John Smith";
        videoItem1.authorImg = R.drawable.com_facebook_profile_picture_blank_portrait;
        videoItem1.noOfLikes = 9;
        videoItem1.noOfDislikes = 2;
        videoReplies.add(videoItem1);

        VideoRepliesModel videoItem2 = new VideoRepliesModel();
        videoItem2.videoURL= url;
        videoItem2.authorName = "Sarah Fox";
        videoItem2.authorImg = R.drawable.com_facebook_profile_picture_blank_portrait;
        videoItem2.noOfLikes = 2;
        videoItem2.noOfDislikes = 15;
        videoReplies.add(videoItem2);

        final VideoRepliesAdapter adapter = new VideoRepliesAdapter(this, videoReplies);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
