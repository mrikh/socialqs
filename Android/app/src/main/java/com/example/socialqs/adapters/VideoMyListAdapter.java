package com.example.socialqs.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.bumptech.glide.Glide;
import com.example.socialqs.constant.Constant;

import java.io.File;
import java.util.ArrayList;

/**
 *  List of users phone gallery video items
 */
public class VideoMyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ItemClickListener myItemClickListener;
    private ArrayList<File> allMediaList;

    public VideoMyListAdapter(Context mContext, ArrayList<File> media) {
        this.allMediaList = media;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_list, parent, false);
        return new FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FileLayoutHolder) holder).videoTitle.setText(allMediaList.get(position).getName());
        //we will load thumbnail using glid library
        Uri uri = Uri.fromFile(allMediaList.get(position));

        Glide.with(mContext).load(uri).thumbnail(0.1f).into(((FileLayoutHolder) holder).thumbnail);
    }

    @Override
    public int getItemCount() {
        return allMediaList.size();
    }

    class FileLayoutHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView thumbnail;
        TextView videoTitle;

        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            videoTitle = itemView.findViewById(R.id.videotitle);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (myItemClickListener != null) {
                myItemClickListener.onItemClick(v, getLayoutPosition(), mContext);
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick (View view, int position, Context context);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.myItemClickListener = itemClickListener;
    }
}
