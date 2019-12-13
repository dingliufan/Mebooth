package com.mebooth.mylibrary.main.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
