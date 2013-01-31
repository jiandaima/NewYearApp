package org.uDevelop.newyearapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public final class PageFragment extends Fragment implements OnItemClickListener {
    private Context mContext;
    private StorageAdapter mStorageAdapter;
    private CustomListAdapter mCAdapter;   
    
    
    public PageFragment(Context context, StorageAdapter adapter, int index) {
    	mContext = context;
    	mStorageAdapter = adapter;
    	mCAdapter = new CustomListAdapter(mContext, mStorageAdapter, index);
    }
       
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View page = inflater.inflate(R.layout.bodylayout, null);
    	ListView list = (ListView)page.findViewById(R.id.list_view);        	
    	list.setAdapter(mCAdapter); 
    	list.setOnItemClickListener(this); 
        return page;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);        
    }
    
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CustomListAdapter.ViewHolder holder = (CustomListAdapter.ViewHolder) view.getTag();
		Intent intent = new Intent(mContext, ContentActivity.class);
		intent.putExtra(Consts.CATETORY, holder.category);
		intent.putExtra(Consts.ITEM_INDEX, position);
		startActivity(intent);
	}     
}
