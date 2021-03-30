package com.example.socialqs.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
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
import com.example.socialqs.activities.home.VideoRepliesActivity;
import com.example.socialqs.models.VideoItemModel;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import org.json.JSONObject;

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
        private ImageView authorImg, playBtn, replyToVideoBtn;
        private CardView bookmarkBtn;
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
            bookmarkBtn = itemView.findViewById(R.id.bookmark_cardview);
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
            authorImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
            authorName.setText(videoItemModel.getAuthorName());

            bookmarkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(videoItemModel.isBookmarked()){
                        videoItemModel.setBookmarked(false);
                        bookmarkBtn.setAlpha((float) 0.6);
                        Toast.makeText(context, context.getText(R.string.unbookmark_question), Toast.LENGTH_LONG).show();
                    }else{
                        videoItemModel.setBookmarked(true);
                        bookmarkBtn.setAlpha((float) 1);
                        Toast.makeText(context, context.getText(R.string.bookmark_question), Toast.LENGTH_LONG).show();
                    }

                    try{
                        JSONObject params = new JSONObject();
                        params.put("questionId", videoItemModel.getVideoID());
                        params.put("isBookmarked", videoItemModel.isBookmarked());
                        NetworkHandler.getInstance().bookmarkQuestion(params, (object, message) -> { });

                    } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                }
            });

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
                        myIntent[0].putExtra("questionID", videoID);
                        context.startActivity(myIntent[0]);

                    } else if (options[item].equals("Choose from Gallery")) {
                        myIntent[0] = new Intent(context, AnswerQuestionActivity.class);
                        myIntent[0].putExtra("videoOption", "2");
                        myIntent[0].putExtra("questionID", videoID);
                        context.startActivity(myIntent[0]);
                    } else {
                        dialog.dismiss();
                        videoView.start();
                    }

                }
            });
            builder.show();
        }
    }
}