package com.example.socialqs.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.socialqs.R;

import com.example.socialqs.activities.landing.MainMenuActivity;
import com.example.socialqs.ui.home.tabs.TabSectionsAdapter;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private TabSectionsAdapter adapter;
    private TabLayout tabs;
    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainMenuActivity) getActivity()).getSupportActionBar().hide(); //Hide action bar at top

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        tabs = (TabLayout) view.findViewById(R.id.tabLayout);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);

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
                    Collections.sort(categoryList);

                    adapter = new TabSectionsAdapter(getContext(), getChildFragmentManager(), categoryList);
                    viewPager.setAdapter(adapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}