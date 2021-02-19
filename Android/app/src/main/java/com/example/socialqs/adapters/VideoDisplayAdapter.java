package com.example.socialqs.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialqs.R;

import com.example.socialqs.models.VideoItem;
import com.google.android.material.animation.AnimationUtils;

import java.util.List;

public class VideoDisplayAdapter extends RecyclerView.Adapter<VideoDisplayAdapter.VideoViewHolder>{

    private List<VideoItem> videoItems;
    public VideoDisplayAdapter(List<VideoItem> videoItems){
        this.videoItems = videoItems;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.setVideoData(videoItems.get(position));
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private TextView authorName, videoQuestion, videoReplies;
        private ImageView authorImg, playBtn;

        public VideoViewHolder(@NonNull View itemView){
            super(itemView);
            authorImg = itemView.findViewById(R.id.video_author_imgView);
            authorName = itemView.findViewById(R.id.video_author_name);
            videoQuestion = itemView.findViewById(R.id.video_question);
            videoReplies = itemView.findViewById(R.id.video_replies);
            videoView = itemView.findViewById(R.id.video_view);
            playBtn = itemView.findViewById(R.id.play_btn);
        }

        @SuppressLint("ClickableViewAccessibility")
        void setVideoData(VideoItem videoItem){
            videoView.setVideoPath(videoItem.videoURL);
            videoQuestion.setText(videoItem.videoQuestion);
            videoReplies.setText(videoItem.replyAmount1);
            authorImg.setImageResource(videoItem.authorImg);
            authorName.setText(videoItem.authorName);

            videoView.start();

            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(videoView.isPlaying()) {
                        videoView.pause();
                        playBtn.setVisibility(View.VISIBLE);

                        // TODO Can this work if added to the res anim folder?
                        //Play Button Animation
                        playBtn.animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                playBtn.animate().scaleX(1).scaleY(1).setDuration(300);
                            }
                        });

                    }else{
                        videoView.start();
                        playBtn.setVisibility(View.INVISIBLE);
                    }
                }
            });

        }

    }


}