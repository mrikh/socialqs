package com.example.socialqs.activities.profile;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.socialqs.R;
import com.example.socialqs.activities.home.VideoRepliesActivity;
import com.example.socialqs.adapters.ProfileAdapter;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.models.VideoItemModel;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class ViewQuestionActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView name, title, answers;
    private ImageView authorImg, playBtn;
    private CardView bookmarkBtn, answerVideoBtn;
    private ProgressBar progressBar;
    private String questionID;

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_video);

        actionBar();
        bindUI();

        questionID = getIntent().getStringExtra("questionID");
        fetchData();
    }

    private void bindUI(){
        videoView = findViewById(R.id.video_view);
        name = findViewById(R.id.video_author_name);
        title = findViewById(R.id.video_question);
        answers = findViewById(R.id.video_replies);
        authorImg = findViewById(R.id.video_author_imgView);
        playBtn = findViewById(R.id.play_btn);
        bookmarkBtn = findViewById(R.id.bookmark_cardview);
        answerVideoBtn = findViewById(R.id.reply_to_video_cardview);
        progressBar = findViewById(R.id.progress);
    }

    private void fetchData(){
        NetworkHandler.getInstance().questionDetails(questionID, new NetworkingClosure() {
            @Override
            public void completion(JSONObject object, String message) {
                if (object == null) {
                    Utilities.getInstance().createSingleActionAlert((message == null) ? getText(R.string.something_wrong) : message, getText(R.string.okay), getApplicationContext(), null).show();
                    return;
                }

                try {
                    JSONArray arr = object.getJSONArray("result");
                    VideoItemModel item = new VideoItemModel(arr.getJSONObject(0));
                    answerVideoBtn.setVisibility(View.INVISIBLE);

                    //Video Details
                    title.setText(item.getVideoQuestion());
                    videoView.setVideoPath(item.getVideoURL());
                    videoSettings();

                    //Video Creator Details
                    name.setText(item.getAuthorName());
                    authorImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
                    if(!item.getAuthorImg().isEmpty()){
                        String img = item.getAuthorImg();
                        Picasso.with(getApplicationContext()).load(img).into(authorImg);
                    }

                    //Bookmark Button
                    bookmarkBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //update UI display
                            if(item.isBookmarked()){
                                item.setBookmarked(false);
                                bookmarkBtn.setAlpha((float) 0.6);
                                Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.unbookmark_question), Toast.LENGTH_LONG).show();
                            }else{
                                item.setBookmarked(true);
                                bookmarkBtn.setAlpha((float) 1);
                                Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.bookmark_question), Toast.LENGTH_LONG).show();
                            }

                            //update database
                            try{
                                JSONObject params = new JSONObject();
                                params.put("questionId", item.getVideoID());
                                params.put("isBookmarked", item.isBookmarked());
                                NetworkHandler.getInstance().bookmarkQuestion(params, (object, message) -> { });

                            } catch (Exception e) {
                                System.out.println(e.getLocalizedMessage());
                            }
                        }
                    });

                    //Number of Answers to Question
                    answers.setText(item.getVideoReplyAmount());
                    answers.setOnClickListener(v -> {
                        if (UserModel.current == null) {
                            Utilities.getInstance().createSingleActionAlert("You must login to see the answers.", "Okay", getApplicationContext(), null).show();
                        } else {
                            Intent myIntent = new Intent(getApplicationContext(), VideoRepliesActivity.class);
                            myIntent.putExtra("video_id", questionID);
                            startActivity(myIntent);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void videoSettings(){
        //Prepare Video
        videoView.requestFocus();
        videoView.setOnPreparedListener(mp -> {
            progressBar.setVisibility(View.INVISIBLE);
            mp.start();
            playBtn.setVisibility(View.INVISIBLE);
        });

        //Play / Pause Video
        videoView.setOnTouchListener((v, event) -> {
            int action = event.getActionMasked();

            if(action == MotionEvent.ACTION_DOWN){ return true; }

            if(action == MotionEvent.ACTION_UP) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    playBtn.setVisibility(View.VISIBLE);

                    //Play Button Animation
                    playBtn.animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            playBtn.animate().scaleX(1).scaleY(1).setDuration(300);
                        }
                    });
                } else {
                    videoView.start();
                    playBtn.setVisibility(View.INVISIBLE);
                }
                return true;
            }
            return false;
        });

        //Loop Video When Finished
        videoView.setOnCompletionListener(MediaPlayer::start);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionBar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        Spannable text = new SpannableString("Question");
        text.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
    }
}
