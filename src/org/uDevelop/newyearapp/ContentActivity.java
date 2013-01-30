package org.uDevelop.newyearapp;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class ContentActivity extends Activity implements OnItemClickListener {
	private static final int sPictureWidth = 684; //ширина картинки(большой) в px
	private static final int sShareButtonRightBorder = 14; 
	private static final int sShareButtonBottomBorder = 28; 
	private static final int sShareButtonWidth = 100; //ширина картинки кнопки шаринга в px
	private static final String sShareSubject = "Отличная идея  встречи Нового года!";
	private static final String sShareText = "Хочу поделиться отличной идеей для встречи Нового года: ";
	private static final String sShareWith ="Поделиться через";
	private static final int sListItemsCount = 3;
	float sMagicScaleConst = 1.0f;
	float sMagicMargin = 10f;
	private StorageAdapter mStorageAdapter; 
	private ItemInfo mItem;
	private int mCategoryId;
	private int mIndex;
	int[] mItemsId = new int[sListItemsCount];
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);
		mCategoryId = this.getIntent().getIntExtra(Consts.CATETORY, -1);
		mIndex = this.getIntent().getIntExtra(Consts.ITEM_INDEX, -1);
		//mStorageAdapter = new DatabaseAdapter(this);
        mStorageAdapter = new JSonStorageAdapter(this);	
		fillActivity();
		fillList();
	}
	
	@Override
    public void onDestroy(){
		mStorageAdapter.close();
		super.onDestroy();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_content, menu);
		return true;
	}
	
	private void fillActivity(){ //Заполняется все, кроме ЛистВью
		mItem = mStorageAdapter.getContentItem(mCategoryId, mIndex);
		Like like = mStorageAdapter.getItemLike(mCategoryId, mIndex);
		Class res = R.drawable.class;
		
		int imageId = 0;
        try {
        	imageId= res.getField(mItem.icon).getInt(null);
        }
        catch (Exception ex) {
        	Log.w("ContentActivity", ex.getMessage());
        }
        ImageView img = (ImageView) findViewById(R.id.list_view_item_icon);
        img.setImageResource(imageId);
        
        TextView text = (TextView) findViewById(R.id.list_view_item_text);
        text.setText(mItem.name);     
        
        text = (TextView) findViewById(R.id.list_view_like_num);
        text.setText(Integer.toString(like.count));	
        
        try {
        	imageId= res.getField(mItem.picture).getInt(null);
        }
        catch (Exception ex) {
        	Log.w("ContentActivity", ex.getMessage());
        }
        img = (ImageView) findViewById(R.id.picture);
        img.setImageResource(imageId);
        
        text = (TextView) findViewById(R.id._text);
        text.setText(mItem.text); 
        ImageButton likeBtn = (ImageButton) findViewById(R.id.like_button);
        if (like.state == Like.NOT_LIKE) {
        	likeBtn.setImageResource(R.drawable.button_like_normal);
        }
        else {
        	likeBtn.setImageResource(R.drawable.button_like_pressed);
        }        
        correctShareBtn();        
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
		int itemsCount = mStorageAdapter.getContentItemCountByCategory(mCategoryId);
		Random rand = new Random();
		for(int i = 0; i < sListItemsCount; i++) {
			int index = rand.nextInt(itemsCount);
			while ((index == mIndex) || member(mItemsId, index, i)) {
				index = rand.nextInt(itemsCount);
			}
			mItemsId[i] = index;			
		}
		SecondListAdapter adapter = new SecondListAdapter(this, mStorageAdapter, mItemsId, mCategoryId); 
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mIndex = mItemsId[position];
		fillActivity();
		fillList();
		ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
		scroll.fullScroll(ScrollView.FOCUS_UP);		
	}
	
	void correctShareBtn() { 
		Display display = getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();
		float scale =  sMagicScaleConst * screenWidth / sPictureWidth;
		ImageButton shareBtn = (ImageButton) findViewById(R.id.share_button);
		int rightBorder = (int) ((sShareButtonRightBorder + sMagicMargin) * scale);
		int bottomBorder = (int) ((sShareButtonBottomBorder + sMagicMargin) * scale);
		int btnWidth = (int) (sShareButtonWidth * scale);
		LayoutParams param = (LayoutParams) shareBtn.getLayoutParams();//setsetHeight(btnWidth);
		param.width = btnWidth;
		param.height = btnWidth;
		param.rightMargin = rightBorder;
		param.bottomMargin = bottomBorder;
		shareBtn.setLayoutParams(param);					
	}
	
	public void shareContent(View view) {
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, sShareSubject);
		String text = sShareText+"\""+mItem.name+"\".\n\n"+mItem.text;
		intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
		startActivity(Intent.createChooser(intent, sShareWith));
	}
	
	public void likeBtnClick(View view) {
		mStorageAdapter.setLiked(mCategoryId, mIndex);
		fillActivity();
	}
	

}
