package org.uDevelop.newyearapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Environment;
import android.util.Log;

public class LikeStorage {
	
	private final static String FILE_NAME = "likeStorage";
	private final static String SERVER_URL = "http://193.232.50.9/likes/get.json";
	private final static String SEND_PREFIX = "http://193.232.50.9/likes/add_like.json?name=";
	private final static int READ_TIMEOUT = 5000;
	private final static int CONNECT_TIMEOUT = 10000;
	
	private String mFolder;
	private Context mContext;
	private StorageAdapter mStorage;
	private static Like[][] mLikeStorage;
	private File mLikeFile;
	private static NetworkStorage netStorage;
	private ArrayList<DataListener> mDataListeners;
		
	public LikeStorage(Context context, StorageAdapter storage) {
		mDataListeners = new ArrayList<DataListener>();
		mContext = context;
		mStorage = storage;
		mFolder = Environment.getDataDirectory() + "/data/" + mContext.getPackageName();
		mLikeFile = new File(mFolder + '/' + FILE_NAME);
		if (!mLikeFile.exists()) {
			createLikeArr();
			likeArrToFile();
		}
		else {
			likeFileToArr();
		}
		if (netStorage == null) {
			netStorage = new NetworkStorage();
		}
	}
			
	public void registerDataListener(DataListener listener) {
		mDataListeners.add(listener);
	}
	
	public void unregisterDataListener(DataListener listener) {
		if (mDataListeners.contains(listener)) {
			mDataListeners.remove(listener);
		}		
	}
	
	private void notifyDataSetChanged() {
		for(DataListener listener: mDataListeners) {
			listener.onUpdateData();
		}
	}
	
	private void createLikeArr() {
		int categoryCount = mStorage.getCategoryCount();
		mLikeStorage =  new Like[categoryCount][];
		for(int i = 0; i < categoryCount; i++) {
			int count = mStorage.getContentItemCountByCategory(i);
			mLikeStorage[i] = new Like[count];
			for(int j = 0; j < count; j++) {
				Like like = new Like();
				like.count = 0;
				like.state = Like.NOT_LIKE;
				mLikeStorage[i][j] = like;				
			}
		}		
	}	
	
	private  void likeArrToFile() {
		File dir = new File(mFolder);
        if (!dir.exists()) {	
        	dir.mkdir();
        }
        mLikeFile.delete();
        FileWriter writer = null;
        try {
        	mLikeFile.createNewFile();  
        	writer = new FileWriter(mLikeFile);
        	int categoryCount = mLikeStorage.length;
        	writer.write(categoryCount);
        	for(int i = 0; i < categoryCount; i++) {
        		int count = mLikeStorage[i].length;
        		writer.write(count);
        		for(int j = 0; j < count; j++) {
        			int likeCount = mLikeStorage[i][j].count;
        			int state = mLikeStorage[i][j].state;
        			writer.write(likeCount);
        			writer.write(state);
        		}        		
        	}
        	writer.close(); 
        }
        catch (IOException ex) {
        	Log.w("LikeStorage", ex.getMessage());
        }      					
	}
	
	private void likeFileToArr() {
		FileReader reader = null;
		try {
			reader = new FileReader(mLikeFile);
		}
		catch(FileNotFoundException ex) {
			Log.w("LikeStorage", ex.getMessage());
			createLikeArr();
			return;
		}
		try {
			int categoryCount = reader.read();
			mLikeStorage = new Like[categoryCount][];
			for(int i = 0; i < categoryCount; i++) {
				int count = reader.read();
				mLikeStorage[i] = new Like[count];
				for(int j = 0; j < count; j++) {
					Like like = new Like();
					like.count = reader.read();
					like.state = (byte) reader.read();
					mLikeStorage[i][j] = like;
				}
			}
			reader.close();			
		}
		catch(IOException ex) {
			Log.w("LikeStorage", ex.getMessage());
		}		
	}
	
	public Like getLike(int categoryId, int elementId) {
		return mLikeStorage[categoryId][elementId];
	}
	
	private void setLikedLocal(int categoryId, int elementId) {
		mLikeStorage[categoryId][elementId].count++;
		mLikeStorage[categoryId][elementId].state = Like.SEND_LIKE;		
	}
	
	public void SyncLikeStorage() {
		notifyDataSetChanged();
		Status status = netStorage.getStatus();
		if (status != Status.RUNNING) {
			netStorage = new NetworkStorage();
			netStorage.execute(mLikeStorage);
		}		
	}
	
	public void setLiked(int categoryId, int elementId) {
		if (mLikeStorage[categoryId][elementId].state == Like.NOT_LIKE) {
			notifyDataSetChanged();
			setLikedLocal(categoryId, elementId);
			SyncLikeStorage();
		}
	}	
	
	public boolean isConnected() {
		ConnectivityManager cm = 
				(ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}
		else {
			return false;
		}
	}
			
	private class NetworkStorage extends AsyncTask<Like[][], Void, Void> {
		
		@Override
        protected Void doInBackground(Like[][]... params) {
			if (params.length > 0) {
				Like[][] likeArr = params[0];
				if (isConnected()) {
					pushToServer(likeArr);
					pullFromServer(likeArr);
				}								
			}			
			return null;           
        }
		
		@Override
        protected void onPostExecute(Void result) {
			likeArrToFile();
			notifyDataSetChanged() ;
		}
		
		private void pushToServer(Like[][] likeArr) {
			int categoryCount = likeArr.length;
			for(int i = 0; i < categoryCount; i++) {
				int count = likeArr[i].length;
				for (int j = 0; j < count; j++) {
					Like like = likeArr[i][j];
					if (like.state == Like.SEND_LIKE) {
						ItemInfo item = mStorage.getContentItem(i, j);
						if (pushLikeToServer(item.name)) {
							likeArr[i][j].state = Like.LIKE;
						}
						else {
							return;
						}
					}
				}				
			}
		}		
		
		private boolean pushLikeToServer(String name) {
			String trueName = name.replace(' ', '+');
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(SEND_PREFIX+trueName);
			try {
				HttpResponse response = client.execute(request);
				Log.w("LikeStorage[sendLike][response]", response.getEntity().toString());
			} 
			catch (ClientProtocolException e) {
				Log.w("LikeStorage[sendLike]", e.getMessage());
				return false;				
			} 
			catch (IOException e) {
				Log.w("LikeStorage[sendLike]", e.getMessage());
				return false;	
			}
			return true;
		}		
		
		private boolean pullFromServer(Like[][] likeArr) {
			boolean result = true;
			InputStream input = null;
		   	try {
		   		URL url = new URL(SERVER_URL);
		   		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		   		connection.setReadTimeout(READ_TIMEOUT);
		   		connection.setConnectTimeout(CONNECT_TIMEOUT);
		   		connection.setRequestMethod("GET");
		   		connection.setDoInput(true);
		   		connection.connect();
		   		input = connection.getInputStream();
		   		parseAndAdd(input, likeArr);
		   		connection.disconnect();
		    } 
		   	catch (Exception ex) {
		   		Log.w("LikeStorage[PullFromServer]", ex.getMessage());
		   		result = false;
		   	}
		   	finally {
		   		if (input != null) {
		   			try {
		   				input.close();
		   			}
		   			catch(IOException ex) {
		   				Log.w("LikeStorage", ex.getMessage());
		   			}		        	
		        } 
		    }
		   	return result;
		}
		
		private void parseAndAdd(InputStream input, Like[][] likeArr) {
			InputStreamReader reader = null;
			try {
				reader = new InputStreamReader(input, "UTF-8");
			} 
			catch(UnsupportedEncodingException ex) {
				Log.w("LikeStorage", ex.getMessage());
				return;
			}
			String jsonData = "";
			try {				
				int in = reader.read(); 			
				while (in > -1 ) {
					jsonData = jsonData + (char) in;
					in = reader.read();
				}				
			}
			catch (IOException ex) {
				Log.w("LikeStorage", ex.getMessage());
				return;
			}	        
	        try {
	        	JSONArray root = new JSONArray(jsonData);
	        	int count = root.length();
	        	for(int i = 0; i < count; i++) {
	        		JSONArray record = root.getJSONArray(i);
	        		String name = record.getString(0);
	        		int likeCount = (int) Math.round(record.getDouble(1));
	        		int[] indexes = getIndexesByName(name);
	        		if (indexes != null) {
	        			int categoryId = indexes[0];
	        			int elemId = indexes[1];
	        			mLikeStorage[categoryId][elemId].count = likeCount;
	        		}
	        	}
	        }
	        catch(JSONException ex) {
	        	Log.w("LikeStorage[parseAndAdd]", ex.getMessage());
	        	return;
	        }			
		}
		
		private int[] getIndexesByName(String name) { //if found then [categoryId, elementId] else null
			int[] result = null;
			int categoryCount = mLikeStorage.length;
			for(int i = 0; i < categoryCount; i++) {
				int count = mLikeStorage[i].length;
				for(int j = 0; j < count; j++) {
					ItemInfo item = mStorage.getContentItem(i, j);
					if (name.equalsIgnoreCase(item.name)) {
						result = new int[2];
						result[0] = i;
						result[1] = j;
						return result;						
					}
				}
			}			
			return result;			
		}		
	}
}
