package org.uDevelop.newyearapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends FragmentActivity {
	private DatabaseAdapter mDbAdapter; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbAdapter = new DatabaseAdapter(this);
        Page[] pages = getPages();
        FragmentPagerAdapter adapter = new PagesAdapter(this.getSupportFragmentManager(), pages);
        
        ViewPager pager = (ViewPager)findViewById(R.id.pages);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.page_indicator);
        indicator.setViewPager(pager);          
    }
	
	@Override
    public void onDestroy(){
		mDbAdapter.close();
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
    	int tabCount = mDbAdapter.getCategoryCount();
        Page[] pages = new Page[tabCount];                
        for(int i = 0; i < tabCount; i++) {
        	String icon = mDbAdapter.getCategoryIcon(i+1);
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
        	pages[i].page = new PageFragment(this, mDbAdapter, i+1);
        } 
        return pages;
    }
}


