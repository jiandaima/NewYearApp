package org.uDevelop.newyearapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends Activity implements OnItemClickListener {
	private DatabaseAdapter mDbAdapter; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		mDbAdapter = new DatabaseAdapter(this);
        fillActivity();      
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
	
	void fillActivity() {
		LayoutInflater inflater = LayoutInflater.from(this); 
        List<View> pages = new ArrayList<View>(); 
        
        int tabCount = mDbAdapter.getCategoryCount();
        
        View page = null;
        ListView list = null;
        CustomListAdapter cAdapter = null;        
        for(int i = 0; i < tabCount; i++) {
        	page = inflater.inflate(R.layout.bodylayout, null);
        	list = (ListView)page.findViewById(R.id.list_view);        	
        	cAdapter = new CustomListAdapter(this, mDbAdapter, i+1);
        	list.setAdapter(cAdapter); 
        	list.setOnItemClickListener(this);
        	pages.add(page); 
        }        
        PagesAdapter adapter = new PagesAdapter(pages, mDbAdapter);        
        ViewPager pager = (ViewPager)findViewById(R.id.pages);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);         
        TabPageIndicator iconIndicator = (TabPageIndicator) findViewById(R.id.page_indicator);
        iconIndicator.setViewPager(pager);		
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CustomListAdapter.ViewHolder holder = (CustomListAdapter.ViewHolder) view.getTag();
		Intent intent = new Intent(this, ContentActivity.class);
		intent.putExtra(Consts.CATETORY, holder.category);
		intent.putExtra(Consts.ITEM_INDEX, position);
		startActivity(intent);
	}
		

}


