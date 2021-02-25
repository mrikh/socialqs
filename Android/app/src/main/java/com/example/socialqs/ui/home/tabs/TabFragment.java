package com.example.socialqs.ui.home.tabs;
;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.socialqs.R;
import com.example.socialqs.adapters.VideoDisplayAdapter;
import com.example.socialqs.models.VideoItem;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A category tab fragment containing video list.
 */
public class TabFragment extends Fragment {

    private  String categoryName;

    public TabFragment(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_home_tab, container, false);
        final ViewPager2 videoViewPager = root.findViewById((R.id.video_view_pager));

        List<VideoItem> videoList = new ArrayList<>();

        NetworkHandler.getInstance().questionListing(new NetworkingClosure() {
             @Override
             public void completion(JSONObject object, String message) {
                 if (object == null) {
                     Utilities.getInstance().createSingleActionAlert((message == null) ? getText(R.string.something_wrong) : message, getText(R.string.okay), getActivity(), null).show();
                     return;
                 }

                 try {
                     JSONArray arr = object.getJSONArray("result");
                     for (int i = 0; i < arr.length(); i++) {
                         VideoItem item = new VideoItem(arr.getJSONObject(i));
                         if(item.category.equals(categoryName)) {
                            videoList.add(item);
                         }
                     }
                     videoViewPager.setAdapter( new VideoDisplayAdapter(videoList, getContext()));
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         });

        return root;
    }

}