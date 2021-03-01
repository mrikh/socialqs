package com.example.socialqs.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.example.socialqs.models.VideoItemModel;
import com.example.socialqs.models.VideoRepliesModel;

import java.util.List;

public class VideoRepliesAdapter extends RecyclerView.Adapter<VideoRepliesAdapter.RepliesViewHolder> {

    private List<VideoRepliesModel> replyList;
    private LinearLayout noRepliesLayout;

    public VideoRepliesAdapter(List<VideoRepliesModel> replyList) {
        this.replyList = replyList;
    }

    @NonNull
    @Override
    public VideoRepliesAdapter.RepliesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoRepliesAdapter.RepliesViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_video_reply, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoRepliesAdapter.RepliesViewHolder holder, int position) {
        holder.setData(replyList.get(position));
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    public class RepliesViewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private LinearLayout correctAnswer;
        private TextView authorName, likes, dislikes;
        private ImageView authorImg, playBtn;
        private String videoQuestionID;
        private boolean isCorrect;

        public RepliesViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.reply_video_view);
            authorName = itemView.findViewById(R.id.reply_user_name);
            authorImg = itemView.findViewById(R.id.reply_user_img);
            likes = itemView.findViewById(R.id.reply_like_textview);
            dislikes = itemView.findViewById(R.id.reply_dislike_textview);
            playBtn = itemView.findViewById(R.id.reply_play_btn);
            correctAnswer = itemView.findViewById(R.id.correct_answer);
        }

        @SuppressLint({"ClickableViewAccessibility"})
        void setData(VideoRepliesModel videoReplies) {
            videoQuestionID = videoReplies.getVideoQuestionID();
            videoView.setVideoPath(videoReplies.getVideoURL());
            likes.setText(videoReplies.getNoOfLikes());
            dislikes.setText(videoReplies.getNoOfDislikes());
            //TODO UPDATE IMAGE WHEN DATABASE IS ADDED
            authorImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
            authorName.setText(videoReplies.getAuthorName());

            if(videoReplies.isCorrect()){
                correctAnswer.setVisibility(View.VISIBLE);
            }

            //Prepare Video
            videoView.setOnPreparedListener(mp -> {
                playBtn.setVisibility(View.VISIBLE);
            });

            //Play / Pause Video
            videoView.setOnTouchListener((v, event) -> {
                int action = event.getActionMasked();

                if(action == MotionEvent.ACTION_DOWN){ return true; }

                if(action == MotionEvent.ACTION_UP) {
                    if (videoView.isPlaying()) {
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
                    } else {
                        videoView.start();
                        playBtn.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
                return false;
            });
        }
    }
}
