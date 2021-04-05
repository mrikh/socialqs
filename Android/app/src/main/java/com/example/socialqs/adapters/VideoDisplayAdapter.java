package com.example.socialqs.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Parcelable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialqs.R;

import com.example.socialqs.activities.home.AnswerQuestionActivity;
import com.example.socialqs.activities.home.MainMenuActivity;
import com.example.socialqs.activities.home.VideoRepliesActivity;
import com.example.socialqs.activities.home.category_tabs.TabFragment;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.models.VideoItemModel;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * List of video items
 */
public class VideoDisplayAdapter extends RecyclerView.Adapter<VideoDisplayAdapter.VideoViewHolder>{

    private List<VideoItemModel> videoItemModels;
    private Context context;
    private VideoViewHolder holder;

    public VideoDisplayAdapter(List<VideoItemModel> videoItemModels, Context context){
        this.videoItemModels = videoItemModels;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        this.holder = holder;
        this.holder.setVideoData(videoItemModels.get(position));

    }

    @Override
    public int getItemCount() { return videoItemModels.size(); }

    public void update(String id, String count) {
        
        int value = -1;
        VideoItemModel item = null;
        for(int i=0; i<videoItemModels.size(); i++){
            if(videoItemModels.get(i).getVideoID().equals(id)){
                value = i;
                item = videoItemModels.get(i);
            }
        }

        if(item != null) {
            //update number of answers
            item.setVideoReplyAmount(Integer.parseInt(count));
            holder.videoReplies.setText(item.getVideoReplyAmount());
            notifyItemChanged(value);
        }
    }

    /**
     *  Set up individual video item data
     */
    public class VideoViewHolder extends RecyclerView.ViewHolder{
        private VideoView videoView;
        private TextView authorName, videoQuestion, videoReplies;
        private ImageView authorImg, playBtn;
        private CardView bookmarkBtn, replyToVideoBtn;
        private String videoID, answerCount;
        private ProgressBar progressBar;

        public VideoViewHolder(@NonNull View itemView){
            super(itemView);
            authorImg = itemView.findViewById(R.id.video_author_imgView);
            authorName = itemView.findViewById(R.id.video_author_name);
            videoQuestion = itemView.findViewById(R.id.video_question);
            videoReplies = itemView.findViewById(R.id.video_replies);
            videoView = itemView.findViewById(R.id.video_view);
            playBtn = itemView.findViewById(R.id.play_btn);
            bookmarkBtn = itemView.findViewById(R.id.bookmark_cardview);
            replyToVideoBtn = itemView.findViewById(R.id.reply_to_video_cardview);

            progressBar = itemView.findViewById(R.id.progress);
            Sprite doubleBounce = new DoubleBounce();
            progressBar.setIndeterminateDrawable(doubleBounce);
            progressBar.setVisibility(View.VISIBLE);
        }

        @SuppressLint("ClickableViewAccessibility")
        void setVideoData(VideoItemModel videoItemModel) {
            videoID = videoItemModel.getVideoID();
            videoView.setVideoPath(videoItemModel.getVideoURL());
            videoQuestion.setText(videoItemModel.getVideoQuestion());
            authorName.setText(videoItemModel.getAuthorName());
            authorImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
            videoReplies.setText(videoItemModel.getVideoReplyAmount());
            answerCount = String.valueOf(videoItemModel.getCount());

            //adds profile images to videos
            if(!videoItemModel.getAuthorImg().isEmpty()){
                String img = videoItemModel.getAuthorImg();
                Picasso.with(context).load(img).into(authorImg);
            }

            //Hide bookmark feature if not logged in
            if(UserModel.current != null) {
                //Setting alpha if already bookmarked
                if (videoItemModel.isBookmarked()) {
                    bookmarkBtn.setAlpha((float) 1);
                }

                //Add/Remove Bookmark
                bookmarkBtn.setOnClickListener(v -> {
                    //update UI display
                    if(videoItemModel.isBookmarked()){
                        videoItemModel.setBookmarked(false);
                        bookmarkBtn.setAlpha((float) 0.6);
                        Toast.makeText(context, context.getText(R.string.unbookmark_question), Toast.LENGTH_LONG).show();
                    }else{
                        videoItemModel.setBookmarked(true);
                        bookmarkBtn.setAlpha((float) 1);
                        Toast.makeText(context, context.getText(R.string.bookmark_question), Toast.LENGTH_LONG).show();
                    }

                    //update database
                    try{
                        JSONObject params = new JSONObject();
                        params.put("questionId", videoItemModel.getVideoID());
                        params.put("isBookmarked", videoItemModel.isBookmarked());
                        NetworkHandler.getInstance().bookmarkQuestion(params, (object, message) -> { });

                    } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                });
            }else{
                bookmarkBtn.setVisibility(View.INVISIBLE);
            }


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

            //Navigate to Video Replies Screen
            videoReplies.setOnClickListener(v -> {
                if (UserModel.current == null) {
                    Utilities.getInstance().createSingleActionAlert("You must login to see the answers.", "Okay", context, null).show();
                } else {
                    Intent myIntent = new Intent(context, VideoRepliesActivity.class);
                    myIntent.putExtra("video_id", videoID);
                    context.startActivity(myIntent);
                }
            });

            //Hide if not logged in
            if(UserModel.current != null) {
                //Open Alert Dialog
                replyToVideoBtn.setOnClickListener(v -> {
                    videoView.pause();
                    videoOptions(videoID);
                });
            }else{
                replyToVideoBtn.setVisibility(View.INVISIBLE);
            }
        }

        private void videoOptions(String videoID){
            //Reply Option List
            final CharSequence[] options = { "Record Video", "Choose from Gallery","Cancel" };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);


            builder.setItems(options, (dialog, item) -> {
                final Intent[] myIntent = {null};

                if (options[item].equals("Record Video")) {
                    myIntent[0] = new Intent(context, AnswerQuestionActivity.class);
                    myIntent[0].putExtra("videoOption", "1");
                    myIntent[0].putExtra("questionID", videoID);
                    myIntent[0].putExtra("answerCount", answerCount);
                    context.startActivity(myIntent[0]);

                } else if (options[item].equals("Choose from Gallery")) {
                    myIntent[0] = new Intent(context, AnswerQuestionActivity.class);
                    myIntent[0].putExtra("videoOption", "2");
                    myIntent[0].putExtra("questionID", videoID);
                    myIntent[0].putExtra("answerCount", answerCount);
                    context.startActivity(myIntent[0]);

                } else {
                    dialog.dismiss();
                    videoView.start();
                }

            });
            builder.show();
        }
    }

}