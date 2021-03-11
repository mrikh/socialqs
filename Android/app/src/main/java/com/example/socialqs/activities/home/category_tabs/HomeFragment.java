package com.example.socialqs.activities.home.category_tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.socialqs.R;

import com.example.socialqs.activities.home.MainMenuActivity;
import com.example.socialqs.models.CategoryModel;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Landing home screen fragment that displays the video category tabs
 */
public class HomeFragment extends Fragment {

    private TabSectionsAdapter adapter;
    private TabLayout tabs;
    private ViewPager2 viewPager;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainMenuActivity)getActivity()).updateActionBarBack(false);
        ((MainMenuActivity)getActivity()).setActionBarTitle(null, "#ffffff", R.color.black);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainMenuActivity) getActivity()).getSupportActionBar().hide(); //Hide action bar at top

        viewPager = (ViewPager2) view.findViewById(R.id.view_pager);
        tabs = (TabLayout) view.findViewById(R.id.tabLayout);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);

        progressBar = view.findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.VISIBLE);

        ArrayList<CategoryModel> categoryList = new ArrayList<>();

        NetworkHandler.getInstance().categoryListing(new NetworkingClosure() {
            @Override
            public void completion(JSONObject object, String message) {
                progressBar.setVisibility(View.INVISIBLE);
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

                    //put model with all as text to the front
                    CategoryModel allModel = categoryList.remove(allCategoryIndex);
                    categoryList.add(0, allModel);

                    adapter = new TabSectionsAdapter(HomeFragment.this, categoryList);
                    viewPager.setAdapter(adapter);

                    ((MainMenuActivity) getActivity()).categoryList = categoryList;

                    //Adding the category names to the tabs
                    new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(categoryList.get(position).name)).attach();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}