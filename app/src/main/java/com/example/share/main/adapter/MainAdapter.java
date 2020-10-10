package com.example.share.main.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MainAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fts;
    String[] titles=new String[]{"关注","推荐","我的"};
    
    public MainAdapter(@NonNull FragmentManager fm, ArrayList<Fragment> fts) {
        super(fm);
        this.fts=fts;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fts.get(position);
    }
    @Override
    public int getCount() {
        return fts.size();
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
