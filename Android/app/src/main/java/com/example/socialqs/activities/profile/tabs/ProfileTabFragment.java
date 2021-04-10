package com.example.socialqs.activities.profile.tabs;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.example.socialqs.activities.home.MainMenuActivity;
import com.example.socialqs.adapters.ProfileAdapter;
import com.example.socialqs.models.CategoryModel;
import com.example.socialqs.models.UserModel;
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

public class ProfileTabFragment extends Fragment {

    private ProgressBar progressBar;
    private LinearLayout noQuestionsLayout, noBookmarkLayout;
    private RecyclerView recyclerView;
    private ProfileAdapter adapter;
    private List<VideoItemModel> videoList;
    private String tabTitle;

    public ProfileTabFragment(String tabTitle) { this.tabTitle = tabTitle; }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile_tab, container, false);
        noQuestionsLayout = root.findViewById(R.id.profile_noQuestions_layout);
        noBookmarkLayout = root.findViewById(R.id.profile_noBookmark_layout);
        recyclerView = root.findViewById(R.id.profile_recycler);

        progressBar = root.findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.VISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        ArrayList<CategoryModel> categoryList = new ArrayList<>();

        NetworkHandler.getInstance().categoryListing(new NetworkingClosure() {
            @Override
            public void completion(JSONObject object, String message) {
                if (object == null) {
                    Utilities.getInstance().createSingleActionAlert((message == null) ? getText(R.string.something_wrong) : message, getText(R.string.okay), getActivity(), null).show();
                    return;
                }

                try {
                    JSONArray arr = object.getJSONArray("categories");

                    int allCategoryIndex = 0;
                    for (int i = 0; i < arr.length(); i++) {
                        CategoryModel model = new CategoryModel(arr.getJSONObject(i));
                        categoryList.add(model);

                        if (model.name.equalsIgnoreCase("all")){
                            allCategoryIndex = i;
                        }
                    }

                    //put 'All' category at beginning
                    CategoryModel allModel = categoryList.remove(allCategoryIndex);
                    categoryList.add(0, allModel);

                    ((MainMenuActivity) getActivity()).categoryList = categoryList;

                    fetchListing();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    //Get question list
    private void fetchListing() {
        videoList = new ArrayList<>();

        NetworkHandler.getInstance().questionListing((((MainMenuActivity)getActivity())).searchString, null, new NetworkingClosure() {
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
                    if(tabTitle.equalsIgnoreCase("myQuestions")) {
                        for (int i = 0; i < arr.length(); i++) {
                            VideoItemModel item = new VideoItemModel(arr.getJSONObject(i));
                            if (UserModel.current.name.equals(item.getAuthorName())) {
                                videoList.add(item);
                            }
                        }


                        if (videoList.size() == 0) {
                            noQuestionsLayout.setVisibility(View.VISIBLE);
                        } else {
                            noQuestionsLayout.setVisibility(View.INVISIBLE);
                        }
                    }else if(tabTitle.equalsIgnoreCase("bookmarked")){
                        for (int i = 0; i < arr.length(); i++) {
                            VideoItemModel item = new VideoItemModel(arr.getJSONObject(i));
                            if (item.isBookmarked()) {
                                videoList.add(item);
                            }
                        }
                        if (videoList.size() == 0) {
                            noBookmarkLayout.setVisibility(View.VISIBLE);
                        } else {
                            noBookmarkLayout.setVisibility(View.INVISIBLE);
                        }
                    }

                    adapter = new ProfileAdapter(videoList, getContext(), tabTitle);
                    recyclerView.setAdapter(adapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
