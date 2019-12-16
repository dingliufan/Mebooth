package com.mebooth.mylibrary.main.adapter;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MineOrderPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> myFragments = new ArrayList<>();
    private List<String> myFragmentTitles = new ArrayList<>();
    private Context mContext;


    public MineOrderPagerAdapter(FragmentManager fm, Context context, List<Fragment> myFragments, List<String> myFragmentTitles) {
        super(fm);
        this.mContext = context;
        this.myFragments = myFragments;
        this.myFragmentTitles = myFragmentTitles;

    }

    @Override
    public Fragment getItem(int i) {
        return myFragments.get(i);
    }

    @Override
    public int getCount() {
        return myFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return myFragmentTitles.get(position);
    }

}
