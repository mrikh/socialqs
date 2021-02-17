package com.example.socialqs.adapters;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialqs.R;

import com.example.socialqs.models.VideoItem;

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
        private VideoView postVideo;
        private TextView authorName, postQuestion;
        private ImageView authorImg;

        public VideoViewHolder(@NonNull View itemView){
            super(itemView);
            authorImg = itemView.findViewById(R.id.author_img_view);
            authorName = itemView.findViewById(R.id.author_name);
            postQuestion = itemView.findViewById(R.id.question_title);
            postVideo = itemView.findViewById(R.id.video_view);
        }

        void setVideoData(VideoItem videoItem){
            postQuestion.setText(videoItem.postQuestion);
            postVideo.setVideoPath(videoItem.videoURL);
            authorImg.setImageResource(videoItem.authorImg);
            authorName.setText(videoItem.authorName);

            postVideo.start();

            postVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(postVideo.isPlaying()) {
                        postVideo.pause();
                    }else{
                        postVideo.start();
                    }
                }
            });
        }
    }
}