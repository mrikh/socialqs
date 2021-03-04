package com.example.socialqs.activities.home.tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.socialqs.models.CategoryModel;

import java.util.List;

/**
 * Category Tab Adapter that returns a fragment corresponding to
 * one of the tabs.
 */
public class TabSectionsAdapter extends FragmentStateAdapter {

    private List<CategoryModel> categories;

    public TabSectionsAdapter(Fragment fragment, List<CategoryModel> categories) {
        super(fragment);
        this.categories = categories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new TabFragment(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

}