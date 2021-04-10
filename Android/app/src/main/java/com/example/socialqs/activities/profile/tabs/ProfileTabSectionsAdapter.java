package com.example.socialqs.activities.profile.tabs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.socialqs.activities.home.category_tabs.TabFragment;
import com.example.socialqs.models.CategoryModel;
import com.example.socialqs.models.VideoItemModel;

import java.util.List;

/**
 * Tab Adapter that returns a one of 2 fragments (my questions & bookmarked)
 */
public class ProfileTabSectionsAdapter extends FragmentStateAdapter {

    int totalTabs;

    public ProfileTabSectionsAdapter(Fragment fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProfileTabFragment("myQuestions");
            case 1:
                return new ProfileTabFragment("bookmarked");
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return totalTabs;
    }

}