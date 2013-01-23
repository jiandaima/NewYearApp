package org.uDevelop.newyearapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

public class LikeStorage {
	
	private final static String sFileName = "likeStorage";
	
	private String mFolder;
	private Context mContext;
	private StorageAdapter mStorage;
	private Like[][] mLikeStorage;
	private File mLikeFile;
	
	
	
	public LikeStorage(Context context, StorageAdapter storage) {
		mContext = context;
		mStorage = storage;
		mFolder =  "/data/data/" + mContext.getPackageName();
		mLikeFile = new File(mFolder+'/'+sFileName);
		if (!mLikeFile.exists()) {
			createLikeArr(); //Test, Test, test
			likeArrToFile();
		}
		else {
			likeFileToArr();
		}
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
	
	public void setLiked(int categoryId, int elementId) {
		if (mLikeStorage[categoryId][elementId].state == Like.NOT_LIKE) {
			setLikedLocal(categoryId, elementId);
			//TODO: сделать сетевую отправку
		}
	}	
	
	
	
	
	
	
	

}
