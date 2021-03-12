package com.example.socialqs.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.example.socialqs.activities.home.AnswerQuestionActivity;
import com.example.socialqs.models.VideoRepliesModel;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VideoRepliesAdapter extends RecyclerView.Adapter<VideoRepliesAdapter.RepliesViewHolder> {

    private List<VideoRepliesModel> replyList;
    private Context context;

    private Boolean automaticPause = false;

    public VideoRepliesAdapter(Context context, List<VideoRepliesModel> replyList) {
        this.replyList = replyList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoRepliesAdapter.RepliesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoRepliesAdapter.RepliesViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_video_reply, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoRepliesAdapter.RepliesViewHolder holder, int position) {
        holder.setData(replyList.get(position), automaticPause);
    }

    public void autoUpdateVideoView(Boolean playing){

        automaticPause = !playing;
        notifyAll();
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    public class RepliesViewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private LinearLayout correctAnswer;
        private CardView likesBtn, dislikesBtn;
        private TextView authorName, noOfLikes, noOfDislikes, replyDate;
        private ImageView authorImg, playBtn;
        private boolean isCorrect;

        public RepliesViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.reply_video_view);
            authorName = itemView.findViewById(R.id.reply_user_name);
            authorImg = itemView.findViewById(R.id.reply_user_img);
            noOfLikes = itemView.findViewById(R.id.reply_like_textview);
            likesBtn = itemView.findViewById(R.id.reply_likes_cardview);
            noOfDislikes = itemView.findViewById(R.id.reply_dislike_textview);
            dislikesBtn = itemView.findViewById(R.id.reply_dislikes_cardview);
            playBtn = itemView.findViewById(R.id.reply_play_btn);
            correctAnswer = itemView.findViewById(R.id.correct_answer);
            replyDate = itemView.findViewById(R.id.reply_post_time);
        }

        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        void setData(VideoRepliesModel videoReplies, Boolean automaticPause) {
            videoView.setVideoPath(videoReplies.getVideoURL());
            setLikesBtn(videoReplies);
            setDislikesBtn(videoReplies);
            //TODO UPDATE IMAGE WHEN DATABASE IS ADDED
            authorImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
            authorName.setText(videoReplies.getAuthorName());
            replyDate.setText(videoReplies.getTime());

            if(videoReplies.isCorrect()){ correctAnswer.setVisibility(View.VISIBLE); }

            playBtn.setVisibility(View.VISIBLE);

            //Play / Pause Video
            videoView.setOnTouchListener((v, event) -> {
                int action = event.getActionMasked();

                if(action == MotionEvent.ACTION_DOWN){ return true; }

                if(action == MotionEvent.ACTION_UP) {
                    if (videoView.isPlaying()) {
                        pause();
                    } else {
                        play();
                    }
                    return true;
                }
                return false;
            });

            if (automaticPause){
                videoView.pause();
            }

            likesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likeAnswer(videoReplies);
                }
            });

            dislikesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dislikeAnswer(videoReplies);
                }
            });
        }

        void play(){
            videoView.start();
            playBtn.setVisibility(View.INVISIBLE);
        }

        void pause(){
            videoView.pause();
            playBtn.setVisibility(View.VISIBLE);

            //Play Button Animation
            playBtn.animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).withEndAction(new Runnable() {
                @Override
                public void run() {
                    playBtn.animate().scaleX(1).scaleY(1).setDuration(300);
                }
            });
        }

        private void likeAnswer(VideoRepliesModel videoReplies){

            if (videoReplies.hasUserLiked()){
                videoReplies.removeFromLike();
            }else{
                videoReplies.removeFromDisLike();
                videoReplies.updateLikes();
            }

            commonButtonHandling(videoReplies);
        }

        private void dislikeAnswer(VideoRepliesModel videoReplies){

            if (videoReplies.hasUserDisliked()){
                videoReplies.removeFromDisLike();
            }else{
                videoReplies.removeFromLike();
                videoReplies.updateDislikes();
            }

            commonButtonHandling(videoReplies);
        }

        private void commonButtonHandling(VideoRepliesModel videoReplies){

            setLikesBtn(videoReplies);
            setDislikesBtn(videoReplies);

            try{
                JSONObject params = new JSONObject();
                params.put("answerId", videoReplies.getReplyID());
                params.put("like", videoReplies.hasUserLiked());
                params.put("dislike", videoReplies.hasUserDisliked());
                NetworkHandler.getInstance().updateAnswer(params, new NetworkingClosure() {
                    @Override
                    public void completion(JSONObject object, String message) {

                    }
                });
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

        @SuppressLint("SetTextI18n")
        private void setLikesBtn(VideoRepliesModel videoReplies){

            noOfLikes.setText(videoReplies.getNoOfLikes() + " Like(s)");

            if (videoReplies.hasUserLiked()){
                likesBtn.setAlpha((float)1.0);
                noOfLikes.setAlpha((float)1.0);
            }else{
                likesBtn.setAlpha((float)0.4);
                noOfLikes.setAlpha((float)0.4);
            }
        }

        @SuppressLint("SetTextI18n")
        private void setDislikesBtn(VideoRepliesModel videoReplies){

            noOfDislikes.setText(videoReplies.getNoOfDislikes() + " Dislike(s)");

            if (videoReplies.hasUserDisliked()){
                dislikesBtn.setAlpha((float)1.0);
                noOfDislikes.setAlpha((float)1.0);
            }else{
                dislikesBtn.setAlpha((float)0.4);
                noOfDislikes.setAlpha((float)0.4);
            }
        }
    }
}
