package com.taobao.tae.buyingdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

/**
 * <p></p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/21
 * Time: 下午3:51
 */
public class TitleFragmentPagerAdapter extends CustomFragmentPagerAdapter {

    private List<String> titleList;

    public TitleFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titleList) {
        super(fm);
        this.fragmentsList = fragments;
        this.titleList = titleList;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (titleList.size() > position) ? titleList.get(position) : "";
    }
}
