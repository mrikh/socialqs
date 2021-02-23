package com.example.socialqs.ui.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.socialqs.R;

import com.example.socialqs.activities.landing.MainMenuActivity;
import com.example.socialqs.ui.home.tabs.SectionsPagerAdapter;
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

    private HomeViewModel homeViewModel;
    private SectionsPagerAdapter adapter;
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
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        ((MainMenuActivity) getActivity()).getSupportActionBar().hide(); //Hide action bar at top

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        tabs = (TabLayout) view.findViewById(R.id.tabLayout);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);

        List<String> categoryNames = new ArrayList<>();

        NetworkHandler.getInstance().categoryListing(new NetworkingClosure() {
            @Override
            public void completion(JSONObject object, String message) {
                if (object == null){
                    Utilities.getInstance().createSingleActionAlert((message == null) ? getText(R.string.something_wrong): message, getText(R.string.okay), getActivity(), null).show();
                    return;
                }

                try{
                    JSONArray arr = object.getJSONArray("categories");
                    String name;
                    for (int i = 0; i < arr.length(); i++){
                        name = arr.getJSONObject(i).getString("name");
                        categoryNames.add(name);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                Collections.sort(categoryNames);
                adapter = new SectionsPagerAdapter(getContext(), getChildFragmentManager(), categoryNames);
                viewPager.setAdapter(adapter);
                tabs.setupWithViewPager(viewPager);
            }
        });
    }

}