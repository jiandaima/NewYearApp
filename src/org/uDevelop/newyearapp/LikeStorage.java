package org.uDevelop.newyearapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	private final static int READ_TIMEOUT = 5000;
	private final static int CONNECT_TIMEOUT = 10000;
	
	private String mFolder;
	private Context mContext;
	private StorageAdapter mStorage;
	private Like[][] mLikeStorage;
	private File mLikeFile;
	private NetworkStorage netStorage;
	
	
	
	public LikeStorage(Context context, StorageAdapter storage) {
		mContext = context;
		mStorage = storage;
		mFolder =  Environment.getDataDirectory()+"/data/" + mContext.getPackageName();
		mLikeFile = new File(mFolder+'/'+FILE_NAME);
		if (!mLikeFile.exists()) {
			createLikeArr(); 
			likeArrToFile();
		}
		else {
			likeFileToArr();
		}
		netStorage = new NetworkStorage();
		SyncLikeStorage();
	}	
	
	private void createLikeArr() {
		int categoryCount = mStorage.getCategoryCount();
		mLikeStorage =  new Like[categoryCount][];
		for(int i = 0 ; i < categoryCount; i++) {
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
			reader= new FileReader(mLikeFile);
		}
		catch(FileNotFoundException ex) {
			Log.w("LikeStorage", ex.getMessage());
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
		Status status = netStorage.getStatus();
		if (status != Status.RUNNING) {
			netStorage.execute(mLikeStorage);
		}
	}
	
	public void setLiked(int categoryId, int elementId) {
		if (mLikeStorage[categoryId][elementId].state == Like.NOT_LIKE) {
			setLikedLocal(categoryId, elementId);
			//TODO: сделать сетевую отправку
		}
	}	
	
	public boolean isConnected() {
		ConnectivityManager cm = 
				(ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}
		return false;		
	}
	
		
	private class NetworkStorage extends AsyncTask<Like[][], Void, Like[][]> {		
		@Override
        protected Like[][] doInBackground(Like[][]... params) {
			if (params.length > 0) {
				Like[][] likeArr = params[0];
				if (isConnected()) {
					//pushToServer
					pullFromServer(likeArr);
				}
				likeArrToFile();				
			}
			
			return null;           
        }
		
		/*@Override
        protected void onPostExecute(Like[][] result) {
            mLikeStorage = result;
            likeArrToFile();
       }*/
		
		private void PushToServer(Like[][] likeArr) {
			int categoryCount = likeArr.length;
			for(int i = 0; i < categoryCount; i++) {
				int count = likeArr[i].length;
				for (int j = 0; j < count; j++) {
					Like like = likeArr[i][j];
					if (like.state == Like.SEND_LIKE) {
						ItemInfo item = mStorage.getContentItem(i, j);
						pushLikeToServer(item.name);
					}
				}				
			}
		}		
		
		private void pushLikeToServer(String name) {
			//TODO: fill_it
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
			byte [] buffer = null;
			try {
				buffer = new byte[input.available()];
				while (input.read(buffer) != -1);
			}
			catch (IOException ex) {
				Log.w("JSONAdapter get from Buffer", ex.getMessage());
				return;
			}
	        String jsonData = new String(buffer);
	        try {
	        	JSONArray root = new JSONArray(jsonData); 
	        	int count = root.length(); 
	        }
	        catch(JSONException ex) {
	        	Log.w("LikeStorage[parseAndAdd]", ex.getMessage());
	        	return;
	        }
	        	
			
		}
	}
	
	

}
