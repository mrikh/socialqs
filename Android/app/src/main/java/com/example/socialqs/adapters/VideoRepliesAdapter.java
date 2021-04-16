package com.example.socialqs.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.models.VideoRepliesModel;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

/**
 * List of video replies
 */
public class VideoRepliesAdapter extends RecyclerView.Adapter<VideoRepliesAdapter.RepliesViewHolder> {

    private List<VideoRepliesModel> replyList;
    private Context context;
    private VideoRepliesAdapter.OnDataChangeListener onDataChangeListener;

    private Boolean automaticPause = false;

    public VideoRepliesAdapter(Context context, List<VideoRepliesModel> replyList) {
        this.replyList = replyList;
        this.context = context;
    }

    public interface OnDataChangeListener{void onDataChanged(int size);}

    public void setOnDataChangeListener(VideoRepliesAdapter.OnDataChangeListener onDataChangeListener){
        this.onDataChangeListener = onDataChangeListener;
    }

    //update layout immediately
    private void updateActivityLayout(){
        if(onDataChangeListener != null){
            onDataChangeListener.onDataChanged(replyList.size());
        }
    }

    @NonNull
    @Override
    public VideoRepliesAdapter.RepliesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoRepliesAdapter.RepliesViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_video_reply, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoRepliesAdapter.RepliesViewHolder holder, int position) {
        holder.setData(replyList.get(position), automaticPause, position);
    }

    public void autoUpdateVideoView(Boolean playing){
        automaticPause = !playing;
        notifyAll();
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    /**
     *  Set up individual replies data
     */
    public class RepliesViewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private LinearLayout correctAnswer;
        private CardView likesBtn, dislikesBtn;
        private TextView authorName, noOfLikes, noOfDislikes, replyDate;
        private ImageView authorImg, playBtn;
        private boolean showProgressBar;
        private ProgressBar progressBar;
        private ImageButton menuBtn;

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
            menuBtn = itemView.findViewById(R.id.reply_menu);

            showProgressBar = true;

            progressBar = itemView.findViewById(R.id.progress);
            Sprite doubleBounce = new DoubleBounce();
            progressBar.setIndeterminateDrawable(doubleBounce);
            progressBar.setVisibility(View.INVISIBLE);
        }

        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        void setData(VideoRepliesModel videoReplies, Boolean automaticPause, int position) {
            videoView.setVideoPath(videoReplies.getVideoURL());
            setLikesBtn(videoReplies);
            setDislikesBtn(videoReplies);
            authorName.setText(videoReplies.getAuthorName());

            //adds profile images to videos
            if(!videoReplies.getAuthorImg().isEmpty()){
                String img = videoReplies.getAuthorImg();
                Picasso.with(context).load(img).into(authorImg);
            }else{
                authorImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
            }

            replyDate.setText(videoReplies.getTime());

            //Show Menu if answer is from current user only
            if(UserModel.current.id.equals(videoReplies.getAuthorID())){
                menuBtn.setVisibility(View.VISIBLE);
                menuBtn.setOnClickListener(v -> {
                    PopupMenu menu = new PopupMenu(itemView.getContext(), menuBtn, Gravity.END);
                    menu.getMenuInflater().inflate(R.menu.video_reply_menu, menu.getMenu());

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.delete_answer) {
                                //Delete Confirmation
                                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                                builder.setMessage(R.string.delete_answer_warning).setTitle(R.string.delete_answer)
                                        .setCancelable(false)
                                        //Delete
                                        .setPositiveButton(R.string.title_delete, (dialog, which) -> {
                                            //fix for error when size = 1 (position = 1 instead of 0)
                                            if(replyList.size() == position){
                                                replyList.remove(0);
                                            }else{
                                                replyList.remove(position);
                                            }

                                            notifyItemRemoved(position);
                                            String answerID = videoReplies.getReplyID();
                                            NetworkHandler.getInstance().deleteAnswer(answerID, (object, message) -> { });
                                            updateActivityLayout();
                                        })
                                        //Cancel
                                        .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
                                builder.create().show();
                            }
                            return false;
                        }
                    });
                    menu.show();
                });
            }

            if(videoReplies.isCorrect()){ correctAnswer.setVisibility(View.VISIBLE); }

            //Prepare Video
            videoView.requestFocus();
            videoView.setOnPreparedListener(mp -> progressBar.setVisibility(View.INVISIBLE));

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

            videoView.setOnCompletionListener(MediaPlayer::start);

            if (automaticPause){ pause(); }

            likesBtn.setOnClickListener(v -> likeAnswer(videoReplies));

            dislikesBtn.setOnClickListener(v -> dislikeAnswer(videoReplies));
        }

        //play video & show progress bar for 3 seconds while it loads
        private void play(){
            videoView.start();
            playBtn.setVisibility(View.INVISIBLE);
            if(showProgressBar) {
                progressBar.setVisibility(View.VISIBLE);

                progressBar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 1000 * 3);
            }
        }

        //pause video
        private void pause(){
            videoView.pause();
            playBtn.setVisibility(View.VISIBLE);
            showProgressBar = false;

            //Play Button Animation
            playBtn.animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).withEndAction(new Runnable() {
                @Override
                public void run() {
                    playBtn.animate().scaleX(1).scaleY(1).setDuration(300);
                }
            });
        }

        //add or remove like count
        private void likeAnswer(VideoRepliesModel videoReplies){

            if (videoReplies.hasUserLiked()){
                videoReplies.removeFromLike();
            }else{
                videoReplies.removeFromDisLike();
                videoReplies.updateLikes();
            }

            commonButtonHandling(videoReplies);
        }

        //add or remove dislike count
        private void dislikeAnswer(VideoRepliesModel videoReplies){

            if (videoReplies.hasUserDisliked()){
                videoReplies.removeFromDisLike();
            }else{
                videoReplies.removeFromLike();
                videoReplies.updateDislikes();
            }

            commonButtonHandling(videoReplies);
        }

        //call like/dislike button display & upload current like/dislike data to database
        private void commonButtonHandling(VideoRepliesModel videoReplies){

            setLikesBtn(videoReplies);
            setDislikesBtn(videoReplies);

            try{
                JSONObject params = new JSONObject();
                params.put("answerId", videoReplies.getReplyID());
                params.put("like", videoReplies.hasUserLiked());
                params.put("dislike", videoReplies.hasUserDisliked());
                NetworkHandler.getInstance().updateAnswer(params, (object, message) -> { });

            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

        //Update like button visual display
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

        //update dislike button visual display
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
