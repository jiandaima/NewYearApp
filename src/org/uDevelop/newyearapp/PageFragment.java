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
    //private static final String KEY_CONTENT = "TestFragment:Content";
    private Context mContext;
    private StorageAdapter mStorageAdapter;
    private int index;
    
    
    public PageFragment(Context context, StorageAdapter adapter, int _index) {
    	super();
    	mContext = context;
    	mStorageAdapter = adapter;
    	index = _index;
    }

        //private String mContent = "???";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View page = inflater.inflate(R.layout.bodylayout, null);
    	ListView list = (ListView)page.findViewById(R.id.list_view);        	
    	CustomListAdapter cAdapter = new CustomListAdapter(mContext, mStorageAdapter, index);
    	list.setAdapter(cAdapter); 
    	list.setOnItemClickListener(this); 
        return page;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString(KEY_CONTENT, mContent);
    }
    
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CustomListAdapter.ViewHolder holder = (CustomListAdapter.ViewHolder) view.getTag();
		Intent intent = new Intent(mContext, ContentActivity.class);
		intent.putExtra(Consts.CATETORY, holder.category);
		intent.putExtra(Consts.ITEM_INDEX, position);
		startActivity(intent);
	}    
}
