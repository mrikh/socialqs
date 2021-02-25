package com.example.socialqs.ui.home.tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * Category Tab Adapter that returns a fragment corresponding to
 * one of the tabs.
 */
public class TabSectionsAdapter extends FragmentStateAdapter {

    private List<String> categoryNames;

    public TabSectionsAdapter(Fragment fragment, List<String> categoryNames) {
        super(fragment);
        this.categoryNames = categoryNames;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new TabFragment(categoryNames.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryNames.size();
    }

}