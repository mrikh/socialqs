package com.example.socialqs.ui.home.tabs;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.socialqs.R;

import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private final Context mContext;
    private List<String> categoryNames;

    public SectionsPagerAdapter(Context context, FragmentManager fm, List<String> categoryNames) {
        super(fm);
        mContext = context;
        this.categoryNames = categoryNames;
    }


    @Override
    public Fragment getItem(int position) {
        return PlaceholderFragment.newInstance(position + 1);
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