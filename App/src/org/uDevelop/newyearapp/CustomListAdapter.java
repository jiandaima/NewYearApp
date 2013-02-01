package org.uDevelop.newyearapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends BaseAdapter implements DataListener {
	private StorageAdapter mAdapter;
	private LayoutInflater mInflater;
	private int mCategoryId;
	
	static class ViewHolder {
        public TextView likeCountTextView;
        public TextView nameTextView;
        public ImageView icon;
        public int category;
    }
	
	
	public CustomListAdapter(Context context, StorageAdapter storageAdapter, int categoryId) {
		mInflater = LayoutInflater.from(context);
		mAdapter = storageAdapter;
		mAdapter.registerDataListener(this);
		mCategoryId = categoryId;	 
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view = convertView;
		ItemInfo item = mAdapter.getContentItem(mCategoryId, position);
		Like like = mAdapter.getItemLike(mCategoryId, position);
		if (view == null) {
			view = mInflater.inflate(R.layout.list_view_item, null);
            holder = new ViewHolder();
            holder.nameTextView = (TextView) view.findViewById(R.id.list_view_item_text);
            holder.icon = (ImageView) view.findViewById(R.id.list_view_item_icon);
            holder.likeCountTextView = (TextView) view.findViewById(R.id.list_view_like_num);
            holder.category = mCategoryId;
            view.setTag(holder);
        } 
		else {
			holder = (ViewHolder) view.getTag();
        }
		holder.nameTextView.setText(item.name);
		Class<R.drawable> res = R.drawable.class;
        int imageId = 0;
        try {
        	imageId= res.getField(item.icon).getInt(null);
        }
        catch (Exception ex) {
        	Log.w("CustomListAdapter", ex.getMessage());
        }
        holder.icon.setImageResource(imageId);
		holder.likeCountTextView.setText(Integer.toString(like.count));
        return view;
    }
	
	@Override
	  public int getCount() {
	    return mAdapter.getContentItemCountByCategory(mCategoryId); 
	  }

	 
	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public int getCategory() {
		return mCategoryId;
	}	
	
	@Override
	public void onUpdateData() {
		this.notifyDataSetChanged();
	}

}
