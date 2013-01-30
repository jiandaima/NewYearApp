package org.uDevelop.newyearapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;


public class JSonStorageAdapter implements StorageAdapter {
	private Context mContext;
	private CategoryInfo[] mCategoryInfo;
	private ItemInfo[][] mItems;
	private LikeStorage mLikeStorage;
	
	 
	
	public JSonStorageAdapter(Context context) {
		mContext = context;
		parse();
		mLikeStorage  = new LikeStorage(context, this);
	}
	
	private void parse() {
		InputStream input = null;
		try {
			input = mContext.getAssets().open(Consts.DATA_FILE_NAME);
		}
		catch (IOException ex) {
			Log.w("JSONAdapter", ex.getMessage());
		}
		byte [] buffer = null;
		try {
			buffer = new byte[input.available()];
			while (input.read(buffer) != -1);
		}
		catch (IOException ex) {
			Log.w("JSONAdapter get from Buffer", ex.getMessage());
		}
        String jsonData = new String(buffer);
        try {
        	JSONObject root = new JSONObject(jsonData);
        	root = root.getJSONObject("new_year_data");
        	JSONArray categoryArray = root.getJSONArray("category");
        	int categoryCount = categoryArray.length();
        	mCategoryInfo = new CategoryInfo[categoryCount];
        	mItems = new ItemInfo[categoryCount][]; 
        	for(int i = 0; i < categoryCount; i++) {
        		JSONObject category = categoryArray.getJSONObject(i);
        		mCategoryInfo[i] = new CategoryInfo();
        		mCategoryInfo[i].name = category.getString("-name");
        		mCategoryInfo[i].icon = category.getString("-icon");
        		JSONArray itemsArray = category.getJSONArray("item");
        		int itemsCount =  itemsArray.length();
        		mItems[i] = new ItemInfo[itemsCount];
        		for(int j = 0; j < itemsCount; j++) {
        			JSONObject item = itemsArray.getJSONObject(j); 
        			mItems[i][j] = new ItemInfo();
        			mItems[i][j].icon = item.getString("-icon"); 
        			mItems[i][j].name = item.getString("-name");
        			mItems[i][j].picture = item.getString("-picture");
        			mItems[i][j].text = item.getString("#text");
        			mItems[i][j].text = getRightText(mItems[i][j].text);        			
        		}           		
        	}       	
        }
        catch (JSONException ex) {
        	Log.w("JSONAdapter[Err]:", ex.getMessage());        	
        }	
	}
	
	private String getRightText(String input) {
		return input.replaceAll("/br/", "\n\n");						
	}
	
	
	@Override
	public void close() {
		
	}
	
	@Override
	public int getCategoryCount() {
		return mCategoryInfo.length;
	}
	
	@Override
	public String getCategoryIcon(int id) {
		return mCategoryInfo[id].icon;
	}
	
	@Override
	public CategoryInfo getCategoryInfo(int id) {
		return mCategoryInfo[id];
	}
	
	@Override
	public String getCategoryName(int id) {
		return mCategoryInfo[id].name;
	}
	
	@Override
	public int getContentItemCountByCategory(int categoryId) {
		return mItems[categoryId].length;
	}
	
	@Override
	public ItemInfo getContentItem(int categoryId, int id) {		
		return mItems[categoryId][id];
	}
	
	@Override
	public Like getItemLike(int categoryId, int id) {
		return mLikeStorage.getLike(categoryId, id);		
	}
	
	public void setLiked(int categoryId, int id) {
		mLikeStorage.setLiked(categoryId, id);
	}
	
	@Override
	public void registerDataListener(DataListener listener) {
		mLikeStorage.registerDataListener(listener);
	}
	
	@Override
	public void unregisterDataListener(DataListener listener) {
		mLikeStorage.unregisterDataListener(listener);
	}
	
	@Override
	public void syncronize() {
		mLikeStorage.SyncLikeStorage();
	}

}
