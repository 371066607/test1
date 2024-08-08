package com.example.test1.adpter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeAdapter extends FragmentPagerAdapter {

    private HashMap<Integer, String> mTitles;
    private ArrayList<Fragment> mFragments;
    public HomeAdapter(@NonNull FragmentManager fm, HashMap<Integer, String> mTitles, ArrayList<Fragment> mFragments) {
        super(fm);
        this.mTitles = mTitles;
        this.mFragments = mFragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int index = 0;
        for (Map.Entry<Integer, String> entry : mTitles.entrySet()) {
            if (index == position) {
                return entry.getValue();
            }
            index++;
        }
        return "";
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
