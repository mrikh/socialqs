package com.example.socialqs.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Parcelable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.example.socialqs.activities.home.MainMenuActivity;
import com.example.socialqs.activities.home.VideoRepliesActivity;
import com.example.socialqs.activities.home.category_tabs.TabFragment;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.models.VideoItemModel;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * List of video items
 */
public class VideoDisplayAdapter extends RecyclerView.Adapter<VideoDisplayAdapter.VideoViewHolder>{

    private List<VideoItemModel> videoItemModels;
    private Context context;
    private int position;
    private VideoViewHolder holder;
    private VideoItemModel model;

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
        try {
            this.position = position;
            this.holder = holder;

            model = videoItemModels.get(position);
            holder.setVideoData(model);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() { return videoItemModels.size(); }

    /**
     *  Set up individual video item data
     */
    public class VideoViewHolder extends RecyclerView.ViewHolder{
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
        void setVideoData(VideoItemModel videoItemModel) throws UnsupportedEncodingException {
            videoID = videoItemModel.getVideoID();
            videoView.setVideoPath(videoItemModel.getVideoURL());
            videoQuestion.setText(videoItemModel.getVideoQuestion());
            authorName.setText(videoItemModel.getAuthorName());

            videoReplies.setText(videoItemModel.getVideoReplyAmount());

            //This code snippet will be updated/changed when profile photo is uploaded to database
            //For now it adds the users profile image and other users have a 'no image' icon
            if(videoItemModel.getAuthorName().equals(UserModel.current.name)){
                String img = UserModel.current.profilePhoto;
                if(img.equals("")) {
                    authorImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
                }else{
                    byte[] data = Base64.decode(img, Base64.DEFAULT);
                    Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                    authorImg.setImageBitmap(bm);
                }
            }else{
                authorImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
            }

            //Setting alpha if already bookmarked
            if(videoItemModel.isBookmarked()){
                bookmarkBtn.setAlpha((float) 1);
            }

            //Add/Remove Bookmark
            bookmarkBtn.setOnClickListener(v -> {
                //update UI display
                if(videoItemModel.isBookmarked()){
                    videoItemModel.setBookmarked(false);
                    bookmarkBtn.setAlpha((float) 0.6);
                    Toast.makeText(context, context.getText(R.string.unbookmark_question), Toast.LENGTH_LONG).show();
                }else{
                    videoItemModel.setBookmarked(true);
                    bookmarkBtn.setAlpha((float) 1);
                    Toast.makeText(context, context.getText(R.string.bookmark_question), Toast.LENGTH_LONG).show();
                }

                //update database
                try{
                    JSONObject params = new JSONObject();
                    params.put("questionId", videoItemModel.getVideoID());
                    params.put("isBookmarked", videoItemModel.isBookmarked());
                    NetworkHandler.getInstance().bookmarkQuestion(params, (object, message) -> { });

                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            });

            //Prepare Video
            videoView.requestFocus();
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

            //Navigate to Video Replies Screen
            videoReplies.setOnClickListener(v -> {
                Intent myIntent = new Intent(context, VideoRepliesActivity.class);
                myIntent.putExtra("video_id", videoID);
                context.startActivity(myIntent);
            });

            //Open Alert Dialog
            replyToVideoBtn.setOnClickListener(v -> {
                videoView.pause();
                if (UserModel.current == null){
                    //just as a safety
                    Utilities.getInstance().createSingleActionAlert("You need to login before using this feature.", "Okay", context, null).show();
                }else {
                    videoOptions(videoID);
                }
            });
        }

        private void videoOptions(String videoID){
            //Reply Option List
            final CharSequence[] options = { "Record Video", "Choose from Gallery","Cancel" };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);


            builder.setItems(options, (dialog, item) -> {
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

            });
            builder.show();
        }
    }

}