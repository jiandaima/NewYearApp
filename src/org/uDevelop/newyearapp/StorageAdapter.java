package org.uDevelop.newyearapp;

public interface StorageAdapter {
	public int getCategoryCount();
	public String getCategoryName(int id);
	public String getCategoryIcon(int id);
	public CategoryInfo getCategoryInfo(int id);
	public int getContentItemCountByCategory(int categoryId);
	public ItemInfo getContentItem(int categoryId, int id);
	public void close();
}
