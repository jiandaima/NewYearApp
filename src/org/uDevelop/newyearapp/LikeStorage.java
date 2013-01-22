package org.uDevelop.newyearapp;

import java.io.File;
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
		createLikeArr(); //Test, Test, test
		likeArrToFile();		
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
        	writer.write(Integer.toString(categoryCount) + '\n');
        	for(int i = 0; i < categoryCount; i++) {
        		int count = mLikeStorage[i].length;
        		writer.write(Integer.toString(count) + '\n');
        		for(int j = 0; j < count; j++) {
        			String likeCount = Integer.toString(mLikeStorage[i][j].count);
        			String state = Integer.toString(mLikeStorage[i][j].state);
        			writer.write(likeCount+'\n');
        			writer.write(state+'\n');
        		}        		
        	}
        	writer.close();
        }
        catch (IOException ex) {
        	Log.w("LikeStorage", ex.getMessage());
        }          
						
	}

	
	
	
	
	
	
	
	

}
