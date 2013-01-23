package org.uDevelop.newyearapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends FragmentActivity {
	private StorageAdapter mStorageAdapter; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
		super.onCreate(null);
        setContentView(R.layout.activity_main);
        
        //mStorageAdapter = new DatabaseAdapter(this);
        mStorageAdapter = new JSonStorageAdapter(this);
        Page[] pages = getPages();
        FragmentPagerAdapter adapter = new PagesAdapter(this.getSupportFragmentManager(), pages);
        
        ViewPager pager = (ViewPager)findViewById(R.id.pages);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.page_indicator);
        indicator.setViewPager(pager);        
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
    public void onDestroy(){
		mStorageAdapter.close();
		super.onDestroy();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
		
	public void onClick(View view) {
		if (view.getId() == R.id.list_view) {
			
		}
		
	}
	
	private Page[] getPages() {
    	int tabCount = mStorageAdapter.getCategoryCount();
        Page[] pages = new Page[tabCount];                
        for(int i = 0; i < tabCount; i++) {
        	String icon = mStorageAdapter.getCategoryIcon(i);
        	Class res = R.drawable.class;
            int imageId = 0;
            try {
            	imageId= res.getField(icon).getInt(null);
            }
            catch (Exception ex) {
            	Log.w("MainActivity[getResIdByName]", ex.getMessage());
            }
            pages[i] = new Page();
        	pages[i].iconId = imageId;
        	pages[i].page = new PageFragment(this, mStorageAdapter, i);
        } 
        return pages;
    }
}


