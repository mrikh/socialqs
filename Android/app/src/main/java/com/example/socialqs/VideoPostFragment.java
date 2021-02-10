package com.example.socialqs;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoPostFragment extends Fragment {

    private VideoView postVideo;
    private TextView authorName, postQuestion;
    private ImageView authorImg;

    public VideoPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoPostFragment newInstance(String param1, String param2) {
        VideoPostFragment fragment = new VideoPostFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle SaveInstanceState){
        super.onViewCreated(view, SaveInstanceState);

        bindUI();

        // TODO: REMOVE HARDCODED ELEMENTS
        authorName.setText("John Smith");
        System.out.println("Test");
        postQuestion.setText("What is the best way to cook pasta?");

        String videoPath = "android.resource://" + getContext().getPackageName() + "/" + R.raw.foodvideo;
        Uri uri = Uri.parse(videoPath);
        postVideo.setVideoURI(uri);
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

    private void bindUI(){
        authorImg = getView().findViewById(R.id.author_img_view);
        authorName = getView().findViewById(R.id.author_name);
        postQuestion = getView().findViewById(R.id.question_title);
        postVideo = getView().findViewById(R.id.video_view);
    }
}