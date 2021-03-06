package com.example.socialqs.activities.home.category_tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.socialqs.R;
import com.example.socialqs.activities.home.MainMenuActivity;
import com.example.socialqs.adapters.VideoDisplayAdapter;
import com.example.socialqs.models.CategoryModel;
import com.example.socialqs.models.VideoItemModel;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A category tab fragment containing video list.
 */
public class TabFragment extends Fragment {

    private CategoryModel category;
    private ProgressBar progressBar;
    private LinearLayout noQuestionsLayout;
    private ViewPager2 videoViewPager;
    private VideoDisplayAdapter adapter;
    private List<VideoItemModel> videoList;

    public TabFragment(CategoryModel category) {
        this.category = category;
    }

    //refresh list with new question added
    private BroadcastReceiver messagesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && (intent.getAction().equalsIgnoreCase("CreatedQuestionIntent"))){
                fetchListing();
            }
        }
    };

    //fetch listing with just search word
    private BroadcastReceiver searchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && (intent.getAction().equalsIgnoreCase("Searched"))) {
                fetchListing();
            }
        }
    };

    //Update video answer count
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isStateSaved()) {
                if (intent != null && (intent.getAction().equalsIgnoreCase("Update"))) {
                    //Update answer value
                    String id = intent.getStringExtra("questionID");
                    String count = intent.getStringExtra("count");
                    adapter.update(id, count);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(searchReceiver, new IntentFilter("Searched"));
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(messagesReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(messagesReceiver, new IntentFilter("CreatedQuestionIntent"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(updateReceiver, new IntentFilter("Update"));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_home_tab, container, false);
        noQuestionsLayout = root.findViewById(R.id.no_questions_layout);

        videoViewPager = root.findViewById((R.id.video_view_pager));

        progressBar = root.findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.VISIBLE);

        fetchListing();

        return root;
    }

    //Get category question list
    private void fetchListing(){
        videoList = new ArrayList<>();

        String categoryId = null;
        if (!this.category.name.equalsIgnoreCase("all")){
            categoryId = this.category.id;
        }

        progressBar.setVisibility(View.VISIBLE);
        NetworkHandler.getInstance().questionListing(((MainMenuActivity)getActivity()).searchString, categoryId, new NetworkingClosure() {
            @Override
            public void completion(JSONObject object, String message) {
                progressBar.setVisibility(View.INVISIBLE);
                if (object == null) {
                    Utilities.getInstance().createSingleActionAlert((message == null) ? getText(R.string.something_wrong) : message, getText(R.string.okay), getActivity(), null).show();
                    return;
                }

                try {
                    videoList.clear();
                    JSONArray arr = object.getJSONArray("result");
                    for (int i = 0; i < arr.length(); i++) {
                        VideoItemModel item = new VideoItemModel(arr.getJSONObject(i));
                        videoList.add(item);
                    }
                    //update background depending on list size
                    if(videoList.size() == 0){
                        noQuestionsLayout.setVisibility(View.VISIBLE);
                    }else{
                        noQuestionsLayout.setVisibility(View.INVISIBLE);
                    }

                    adapter = new VideoDisplayAdapter(videoList, getContext());
                    videoViewPager.setAdapter(adapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}