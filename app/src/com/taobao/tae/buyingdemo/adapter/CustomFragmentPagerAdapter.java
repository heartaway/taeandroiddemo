package com.taobao.tae.buyingdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

public class CustomFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    protected List<Fragment> fragmentsList;

    public CustomFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public CustomFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragmentsList = fragments;
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragmentsList.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

}
