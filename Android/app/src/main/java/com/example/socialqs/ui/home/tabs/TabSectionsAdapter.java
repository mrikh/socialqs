package com.example.socialqs.ui.home.tabs;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TabSectionsAdapter extends FragmentStatePagerAdapter {

    private final Context mContext;
    private List<String> categoryNames;

    public TabSectionsAdapter(Context context, FragmentManager fm, List<String> categoryNames) {
        super(fm);
        mContext = context;
        this.categoryNames = categoryNames;
    }

    @Override
    public Fragment getItem(int position) {
        return TabFragment.newInstance(position, categoryNames.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return categoryNames.get(position);
    }

    @Override
    public int getCount() {
        return categoryNames.size();
    }

}