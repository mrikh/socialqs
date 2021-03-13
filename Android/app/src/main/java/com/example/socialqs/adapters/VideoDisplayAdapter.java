package com.example.socialqs.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialqs.R;

import com.example.socialqs.activities.home.AnswerQuestionActivity;
import com.example.socialqs.activities.home.VideoRepliesActivity;
import com.example.socialqs.models.VideoItemModel;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import java.util.List;

public class VideoDisplayAdapter extends RecyclerView.Adapter<VideoDisplayAdapter.VideoViewHolder>{

    private List<VideoItemModel> videoItemModels;
    private Context context;

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
        holder.setVideoData(videoItemModels.get(position));
    }

    @Override
    public int getItemCount() { return videoItemModels.size(); }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private TextView authorName, videoQuestion, videoReplies;
        private ImageView authorImg, playBtn, bookmarkBtn, replyToVideoBtn;
        private String videoID;
        private ProgressBar progressBar;

        public VideoViewHolder(@NonNull View itemView){
            super(itemView);
            authorImg = itemView.findViewById(R.id.video_author_imgView);
            authorName = itemView.findViewById(R.id.video_author_name);
            videoQuestion = itemView.findViewById(R.id.video_question);
            videoReplies = itemView.findViewById(R.id.video_replies);
            videoView = itemView.findViewById(R.id.video_view);
            playBtn = itemView.findViewById(R.id.play_btn);
            bookmarkBtn = itemView.findViewById(R.id.bookmark_img_view);
            replyToVideoBtn = itemView.findViewById(R.id.reply_to_video_img_view);

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
            videoReplies.setText(videoItemModel.getVideoReplyAmount());
            //TODO SET CORRECT IMAGE WHEN DATABASE IS UPDATED
            authorImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
            authorName.setText(videoItemModel.getAuthorName());

            // TODO BOOKMARK

            videoView.requestFocus();
            //Prepare Video
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

            videoReplies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(context, VideoRepliesActivity.class);
                    myIntent.putExtra("video_id", videoID);
                    context.startActivity(myIntent);
                }
            });

            replyToVideoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.pause();
                    videoOptions(videoID);
                }
            });
        }

        private void videoOptions(String videoID){
            final CharSequence[] options = { "Record Video", "Choose from Gallery","Cancel" };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    final Intent[] myIntent = {null};

                    if (options[item].equals("Record Video")) {
                        myIntent[0] = new Intent(context, AnswerQuestionActivity.class);
                        myIntent[0].putExtra("videoOption", "1");

                    } else if (options[item].equals("Choose from Gallery")) {
                        myIntent[0] = new Intent(context, AnswerQuestionActivity.class);
                        myIntent[0].putExtra("videoOption", "2");
                    } else {
                        dialog.dismiss();
                        videoView.start();
                    }

                    myIntent[0].putExtra("questionID", videoID);
                    context.startActivity(myIntent[0]);
                }
            });

            builder.show();
        }

    }
}