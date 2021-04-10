package com.example.socialqs.activities.home.category_tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.socialqs.R;

import com.example.socialqs.activities.home.MainMenuActivity;
import com.example.socialqs.activities.home.notifications.NotificationActivity;
import com.example.socialqs.models.CategoryModel;
import com.example.socialqs.models.UserModel;
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

/**
 * Landing home screen fragment that displays the video category tabs
 */
public class HomeFragment extends Fragment {

    private TabSectionsAdapter adapter;
    private TabLayout tabs;
    private ViewPager2 viewPager;
    private ProgressBar progressBar;
    private ImageView notifications;
    private AutoCompleteTextView searchBar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainMenuActivity) getActivity()).getSupportActionBar().hide(); //Hide action bar at top
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notifications = (ImageView) view.findViewById(R.id.notification_img);
        if(UserModel.current == null){
            notifications.setVisibility(View.INVISIBLE);
        }else {
            notifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), NotificationActivity.class);
                    startActivity(intent);
                }
            });
        }

        searchBar = (AutoCompleteTextView) view.findViewById(R.id.search_bar);
        if (UserModel.current != null) {
            searchBar.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
            searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        ((MainMenuActivity) getActivity()).searchString = v.getText().toString();
                        Intent newIntent = new Intent("Searched");
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(newIntent);

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        return false;
                    }

                    return true;
                }
            });
        }else{searchBar.setFocusable(false);
            searchBar.setOnClickListener(v -> Utilities.getInstance().createSingleActionAlert("You must login to use this feature.", "Okay", getContext(), null).show());
        }


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

                    //put 'All' category at beginning
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