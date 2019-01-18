package com.cosmo.library.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author: botao.xu
 * @date: 2017/11/7 12:50
 * @desc: viewPage+fragment 公共Adapter
 */

public class CommonFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mList;
    private String[] titles;

    public CommonFragmentAdapter(FragmentManager fm, List<Fragment> mList) {
        super(fm);
        this.mList = mList;
    }

    public CommonFragmentAdapter(FragmentManager fm, List<Fragment> mList, String[] titles) {
        super(fm);
        this.mList = mList;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        if (mList != null) {
            return mList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && position < titles.length) {
            return titles[position];
        } else {
            return null;
        }
    }
}
