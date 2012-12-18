package org.uDevelop.newyearapp;

import java.util.List;
import com.viewpagerindicator.IconPagerAdapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class PagesAdapter extends PagerAdapter implements IconPagerAdapter {
	private List<View> mPages = null;
	private DatabaseAdapter mDbAdapter;
    
    public PagesAdapter(List<View> pages, DatabaseAdapter databaseAdapter) {
        mPages = pages;
        mDbAdapter = databaseAdapter;
    }
    
    @Override
    public Object instantiateItem(View collection, int position) {
        View view = mPages.get(position);
        ((ViewPager) collection).addView(view, 0);
        return view;
    }
    
    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
    }
    
    @Override
    public int getCount() {
        return mPages.size();
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
    
    public CharSequence getPageTitle(int pPosition) {
    	return ""; 
    }
    
    @Override
	public int getIconResId(int index) {
		String icon = mDbAdapter.getCategoryIcon(/*index+1*/1);
		Class res = R.drawable.class;
        int imageId = 0;
        try {
        	imageId = res.getField(icon).getInt(null);
        }
        catch (Exception ex) {
        	Log.w("IconAdapter", ex.getMessage());
        }
        return imageId;
	}

    

   
}