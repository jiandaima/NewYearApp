package org.uDevelop.newyearapp;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class ContentActivity extends Activity implements OnItemClickListener {
	private static final int sListItemsCount = 3;
	private DatabaseAdapter mDbAdapter; 
	private int mCategoryId;
	private int mIndex;
	int[] mItemsId = new int[sListItemsCount];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);
		mCategoryId = this.getIntent().getIntExtra(Consts.CATETORY, -1);
		mIndex = this.getIntent().getIntExtra(Consts.ITEM_INDEX, -1);
		mDbAdapter = new DatabaseAdapter(this);	
		fillActivity();
		fillList();
	}
	
	@Override
    public void onDestroy(){
		mDbAdapter.close();
		super.onDestroy();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_content, menu);
		return true;
	}
	
	private void fillActivity(){
		ItemInfo item = mDbAdapter.getContentItem(mCategoryId, mIndex);
		Class res = R.drawable.class;
		
		int imageId = 0;
        try {
        	imageId= res.getField(item.icon).getInt(null);
        }
        catch (Exception ex) {
        	Log.w("ContentActivity", ex.getMessage());
        }
        ImageView img = (ImageView) findViewById(R.id.list_view_item_icon);
        img.setImageResource(imageId);
        
        TextView text = (TextView) findViewById(R.id.list_view_item_text);
        text.setText(item.name);     
        
        text = (TextView) findViewById(R.id.list_view_like_num);
        text.setText(Integer.toString(item.likeCount));	
        
        try {
        	imageId= res.getField(item.picture).getInt(null);
        }
        catch (Exception ex) {
        	Log.w("ContentActivity", ex.getMessage());
        }
        img = (ImageView) findViewById(R.id.picture);
        img.setImageResource(imageId);
        
        text = (TextView) findViewById(R.id._text);
        text.setText(item.text);        
        fillList();
        
	}
	
	private boolean member(int[] array, int elem, int arraySize) {
		for (int i = 0; i < arraySize; i++) {
			if (array[i] == elem) {
				return true;
			}
		}
		return false;
	}
	
	private void fillList() {
		ListView list = (ListView)findViewById(R.id.other_items_list);
		int itemsCount = mDbAdapter.getContentItemCountByCategory(mCategoryId);
		Random rand = new Random();
		for(int i = 0; i < sListItemsCount; i++) {
			int index = rand.nextInt(itemsCount);
			while ((index == mIndex) || member(mItemsId, index, i)) {
				index = rand.nextInt(itemsCount);
			}
			mItemsId[i] = index;			
		}
		SecondListAdapter adapter = new SecondListAdapter(this, mDbAdapter, mItemsId, mCategoryId); 
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mIndex = mItemsId[position];
		fillActivity();
		ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
		scroll.fullScroll(ScrollView.FOCUS_UP);		
	}

}
