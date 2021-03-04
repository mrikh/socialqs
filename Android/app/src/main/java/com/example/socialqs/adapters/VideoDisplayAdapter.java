package com.example.socialqs.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialqs.R;

import com.example.socialqs.activities.home.VideoRepliesActivity;
import com.example.socialqs.models.VideoItemModel;

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
        }

        @SuppressLint("ClickableViewAccessibility")
        void setVideoData(VideoItemModel videoItemModel) {
            videoID = videoItemModel.getVideoID();
            System.out.println("video id: " + videoID);
            videoView.setVideoPath(videoItemModel.getVideoURL());
            videoQuestion.setText(videoItemModel.getVideoQuestion());
            videoReplies.setText(videoItemModel.getVideoReplyAmount());
            //TODO SET CORRECT IMAGE WHEN DATABASE IS UPDATED
            authorImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
            authorName.setText(videoItemModel.getAuthorName());

            // TODO NAVIGATE TO 'CREATE' TO REPLY TO VIDEO POST

            // TODO BOOKMARK

            //Prepare Video
            videoView.setOnPreparedListener(mp -> {
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

            //Loop Video When Finished
            videoView.setOnCompletionListener(mp -> mp.start());

            videoReplies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(context, VideoRepliesActivity.class);
                    myIntent.putExtra("Video ID", videoID);
                    context.startActivity(myIntent);
                }
            });
        }

    }
}