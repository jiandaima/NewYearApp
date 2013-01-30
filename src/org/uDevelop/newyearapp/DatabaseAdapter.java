package org.uDevelop.newyearapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAdapter implements StorageAdapter {
	private static final String sGetCategoryCount = "select count (*) from category";
	private static final String sGetCategoryName = "select name from category where _id =";
	private static final String sGetCategoryIcon = "select icon from category where _id =";
	private static final String sGetCategoryInfo = "select name, icon from category where _id =";
	private static final String sGetContentItemCount = "select count (*) from content";
	private static final String sGetContentItemCountByCategory = 
				"select count (*) from content where category_id =";
	private static final String sGetContentItem = 
				"select name, icon, likeCount, picture, _text from content where _id ="; 
			public String name;
	private static final String  sGetLikeCount = "select likeCount from content where _id =";
	private static final String sUpdateLikeCount = "update content set likeCount = ";
	private static final String sGetAllByCategory = 
				"select _id, name, icon, likeCount, picture, _text from content where category_id = ";
	private SQLiteDatabase mDatabase;	
	
	public DatabaseAdapter(Context context) {
		DbOpenHelper helper = new DbOpenHelper(context, Consts.DB_NAME, null, Consts.DB_VERSION);
		mDatabase = helper.getWritableDatabase();				
	}
	
	@Override
	public void close() {
		mDatabase.close();
	}
	
	@Override
	public int getCategoryCount() {
		Cursor cursor =	mDatabase.rawQuery(sGetCategoryCount, null);
		cursor.moveToFirst();
		int result = cursor.getInt(0);
		cursor.close();
		return result;
	}
	
	@Override
	public String getCategoryName(int id) {
		String query = sGetCategoryName+Integer.toString(id+1);
		Cursor cursor =	mDatabase.rawQuery(query, null);
		cursor.moveToFirst();
		String result = cursor.getString(0);
		cursor.close();
		return result;
	}
	
	@Override
	public String getCategoryIcon(int id) {
		String query = sGetCategoryIcon+Integer.toString(id+1);
		Cursor cursor =	mDatabase.rawQuery(query, null);
		cursor.moveToFirst();
		String result = cursor.getString(0);
		cursor.close();
		return result;
	}
	
	@Override
	public CategoryInfo getCategoryInfo(int id) {
		String query = sGetCategoryInfo+Integer.toString(id+1);
		Cursor cursor =	mDatabase.rawQuery(query, null);
		cursor.moveToFirst();
		CategoryInfo result = new CategoryInfo();
		result.name = cursor.getString(0);
		result.icon = cursor.getString(1);
		cursor.close();	
		return result;
	}
	
	public int getContentItemCount() {
		Cursor cursor =	mDatabase.rawQuery(sGetContentItemCount, null);
		cursor.moveToFirst();
		int result = cursor.getInt(0);
		cursor.close();
		return result;
	}
	
	@Override
	public int getContentItemCountByCategory(int categoryId) {
		String query = sGetContentItemCountByCategory+Integer.toString(categoryId+1); 
		Cursor cursor =	mDatabase.rawQuery(query, null);
		cursor.moveToFirst();
		int result = cursor.getInt(0);
		cursor.close();
		return result;
	}
	
	public ItemInfo getContentItem(int id) {
		ItemInfo item = new ItemInfo();
		String query = sGetContentItem+Integer.toString(id+1);
		Cursor cursor =	mDatabase.rawQuery(query, null);
		cursor.moveToFirst();
		item.name = cursor.getString(0);
		item.icon = cursor.getString(1);
		//item.likeCount = cursor.getInt(2);
		item.picture = cursor.getString(3);
		item.text = cursor.getString(4);
		cursor.close();		
		return item;		
	}
	
	@Override
	public ItemInfo getContentItem(int categoryId, int id) { 
		ItemInfo item = new ItemInfo();
		String query = sGetAllByCategory+Integer.toString(categoryId+1);
		Cursor cursor =	mDatabase.rawQuery(query, null);
		cursor.moveToFirst();
		for(int i = 0; i < id; i++) {
			cursor.moveToNext();
		}
				
		//item.id = cursor.getInt(0);
		item.name = cursor.getString(1);
		item.icon = cursor.getString(2);
		//item.likeCount = cursor.getInt(3);
		item.picture = cursor.getString(4);
		item.text = cursor.getString(5);
		cursor.close();		
		return item;		
	}	
	
	
	public void incLikeCount(int id) {
		String query = sGetLikeCount+Integer.toString(id+1);
		Cursor cursor = mDatabase.rawQuery(query, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		count++;
		query = sUpdateLikeCount+Integer.toString(count)+" where _id = "+Integer.toString(id+1);
		mDatabase.execSQL(query);		
	}
	
	public Cursor rawQuery(String query) {
		return mDatabase.rawQuery(query, null);
	}
	
	public void setLiked(int categoryId, int id) {
		//TODO: сделать реализацию метода
	}
	
	@Override
	public Like getItemLike(int categoryId, int id) {
		//TODO: сделать реализацию метода
		return null;
	}
	
	@Override
	public void registerDataListener(DataListener listener) {
		//TODO:
	}
	
	@Override
	public void unregisterDataListener(DataListener listener) {
		//TODO:
	}
	
	@Override
	public void syncronize() {
		//TODO:
	}
	
	
	
}
