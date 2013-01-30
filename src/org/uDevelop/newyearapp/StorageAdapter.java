package org.uDevelop.newyearapp;

public interface StorageAdapter {
	public int getCategoryCount();
	public String getCategoryName(int id);
	public String getCategoryIcon(int id);
	public CategoryInfo getCategoryInfo(int id);
	public int getContentItemCountByCategory(int categoryId);
	public ItemInfo getContentItem(int categoryId, int id);
	public Like getItemLike(int categoryId, int id);
	public void setLiked(int categoryId, int id);
	public void close();
	public void registerDataListener(DataListener listener);
	public void unregisterDataListener(DataListener listener);
	public void syncronize();
}
