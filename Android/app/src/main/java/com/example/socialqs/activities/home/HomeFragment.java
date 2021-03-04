package com.example.socialqs.activities.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.socialqs.R;

import com.example.socialqs.activities.landing.MainMenuActivity;
import com.example.socialqs.activities.home.tabs.TabSectionsAdapter;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Landing home screen fragment that displays the video category tabs
 */
public class HomeFragment extends Fragment {

    private TabSectionsAdapter adapter;
    private TabLayout tabs;
    private ViewPager2 viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainMenuActivity) getActivity()).getSupportActionBar().hide(); //Hide action bar at top

        viewPager = (ViewPager2) view.findViewById(R.id.view_pager);
        tabs = (TabLayout) view.findViewById(R.id.tabLayout);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);

        List<String> categoryList = new ArrayList<>();

        NetworkHandler.getInstance().categoryListing(new NetworkingClosure() {
            @Override
            public void completion(JSONObject object, String message) {
                if (object == null) {
                    Utilities.getInstance().createSingleActionAlert((message == null) ? getText(R.string.something_wrong) : message, getText(R.string.okay), getActivity(), null).show();
                    return;
                }

                try {
                    JSONArray arr = object.getJSONArray("categories");
                    String name;
                    for (int i = 0; i < arr.length(); i++) {
                        name = arr.getJSONObject(i).getString("name");
                        categoryList.add(name);
                    }
                    Collections.sort(categoryList); //Alphabetically Sorted

                    adapter = new TabSectionsAdapter(HomeFragment.this, categoryList);
                    viewPager.setAdapter(adapter);

                    //Adding the category names to the tabs
                    new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(categoryList.get(position))).attach();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}