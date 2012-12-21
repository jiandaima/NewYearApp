package org.uDevelop.newyearapp;

import java.util.List;
import com.viewpagerindicator.IconPagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class PagesAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
	private Page[] mPages;
	PagesAdapter(FragmentManager fm, Page[] pages) {
        super(fm);
        mPages = pages;
    }
    
    @Override
    public Fragment getItem(int position) {
        return mPages[position].page;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override public int getIconResId(int position) {
      return mPages[position].iconId;
    }

  @Override
    public int getCount() {
      return mPages.length;
    }   
}