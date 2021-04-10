package com.example.socialqs.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.example.socialqs.activities.profile.ViewQuestionActivity;
import com.example.socialqs.models.VideoItemModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *  List of bookmarked items or current user created questions
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder> {

    private List<VideoItemModel> videoItemModels;
    private Context context;
    private String tabTitle;

    public ProfileAdapter(List<VideoItemModel> videoItemModels, Context context, String tabTitle){
        this.videoItemModels = videoItemModels;
        this.context = context;
        this.tabTitle = tabTitle;
    }

    @NonNull
    @Override
    public ProfileAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProfileAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_profile_questions, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.MyViewHolder holder, int position) {
        holder.setData(videoItemModels.get(position));
    }

    @Override
    public int getItemCount() {
        return videoItemModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView questionTitle;
        private ImageView profilePhoto;

        public MyViewHolder(View itemView) {
            super(itemView);
            // Bind Ui with id
            questionTitle = itemView.findViewById(R.id.item_title);
            profilePhoto = itemView.findViewById(R.id.item_profile_img);
        }

        public void setData(VideoItemModel videoItemModel){
             questionTitle.setText(videoItemModel.getVideoQuestion());
             profilePhoto.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);

            if (!videoItemModel.getAuthorImg().isEmpty()) {
                String img = videoItemModel.getAuthorImg();
                Picasso.with(context).load(img).into(profilePhoto);
            }

            questionTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewQuestionActivity.class);
                    intent.putExtra("questionID", videoItemModel.getVideoID());
                    context.startActivity(intent);
                }
            });
        }


    }
}
