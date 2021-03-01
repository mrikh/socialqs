package com.example.socialqs.activities.landing;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.example.socialqs.adapters.VideoDisplayAdapter;
import com.example.socialqs.adapters.VideoRepliesAdapter;
import com.example.socialqs.models.VideoItemModel;
import com.example.socialqs.models.VideoRepliesModel;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONObject;

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

        Intent intent = getIntent();
        String videoID = intent.getStringExtra("Video ID");

        System.out.println("VIDEO ID: " + videoID);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.video_replies_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        List<VideoRepliesModel> videoReplies = new ArrayList<>();

        NetworkHandler.getInstance().repliesListing(new NetworkingClosure() {
            @Override
            public void completion(JSONObject object, String message) {
//                if (object == null) {
//                    Utilities.getInstance().createSingleActionAlert((message == null) ? getText(R.string.something_wrong) : message, getText(R.string.okay), getApplicationContext(), null).show();
//                    return;
//                }

                try {
                    JSONArray arr = object.getJSONArray("result");
                    for (int i = 0; i < arr.length(); i++) {
                        VideoRepliesModel item = new VideoRepliesModel(arr.getJSONObject(i));
                        if(item.getVideoQuestionID().equals(videoID)) {
                            videoReplies.add(item);
                        }
                    }

                    recyclerView.setAdapter(new VideoRepliesAdapter(videoReplies));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        for(int i=0; i<videoReplies.size(); i++){
//            System.out.println("VIDEO REPLIES: " + videoReplies.get(i).videoQuestionID);
//
//            if(!videoReplies.get(i).videoQuestionID.equals(videoID)){
//                videoReplies.remove(i);
//            }
//        }
//
//        final VideoRepliesAdapter adapter = new VideoRepliesAdapter(this, videoReplies);
//        recyclerView.setAdapter(adapter);
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
