package org.uDevelop.newyearapp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DbOpenHelper extends SQLiteOpenHelper {
	private final static int BUFFER_SIZE =  8192;
	private String mDbFolder;
	private String mDbName;
	private int mDatabaseVersion;
	private Context mContext;
	
	public DbOpenHelper(Context context, String name, CursorFactory factory, 
			int version) {
		super(context, name, factory, version);
		mContext = context;
		mDbFolder = Environment.getDataDirectory() + "/data/" + mContext.getPackageName() + "/databases/";
		mDbName = name;
		mDatabaseVersion = version;
	}
	
	private void updateOrCreateDatabasebIfNeed() {
		File dbFile = new File(mDbFolder + mDbName);
		if (!dbFile.exists()) {
			copyDBfromAssets("", mDbFolder, mDbName);
		}
		else {
			try {
				SQLiteDatabase database = mContext.openOrCreateDatabase(mDbName, 
						Context.MODE_WORLD_READABLE, null);
				int databaseVersion = database.getVersion();
				database.close();
				if (databaseVersion < mDatabaseVersion) {
					try {
						dbFile.delete();
					}
					catch (Exception exception) {
						Log.w("DbOpenHelper", exception.getMessage());
					}
					copyDBfromAssets("", mDbFolder, mDbName);					
				}
			}
			catch (Exception e) {
				Log.w("DbOpenHelper", e.getMessage());
			}
		}	
	}
	
	@Override 
	public SQLiteDatabase getReadableDatabase() {
		updateOrCreateDatabasebIfNeed();
		return mContext.openOrCreateDatabase(mDbName, Context.MODE_WORLD_READABLE, 
					null); 			
	}
	
	@Override 
	public SQLiteDatabase getWritableDatabase() {
		updateOrCreateDatabasebIfNeed();
		return mContext.openOrCreateDatabase(mDbName, Context.MODE_WORLD_WRITEABLE, 
					null); 			
	}	
		
	private void copyDBfromAssets(String inFolder, String outFolder, String dbName) { //копируем бд
		InputStream inStream = null;
        OutputStream outStream = null;
        File dir = new File(outFolder);
        if (!dir.exists()) {	//почекаем папку для базы на наличие
        	dir.mkdir();
        }
        try {
        	inStream = new BufferedInputStream(mContext.getAssets().open(inFolder 
        			+ dbName), BUFFER_SIZE);
        }
        catch (IOException ex) {
        	Log.w("DBOpenHelper", ex.getMessage());
        }
        try {            
            outStream = new BufferedOutputStream(new FileOutputStream(outFolder 
            		+ dbName), BUFFER_SIZE);
            byte[] buf = new byte[BUFFER_SIZE];
            int length;
            while ((length = inStream.read(buf)) > 0) {
            	outStream.write(buf, 0, length);
            }
            outStream.flush();
            outStream.close();            
        }
        catch (IOException ex) {
        	Log.w("DBOpenHelper", ex.getMessage());
        }
       try {
       	inStream.close();
       }
       catch (IOException ex) {
    	   Log.w("DBOpenHelper", ex.getMessage());
       }
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
				
	}
	
	@Override 
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		
	}
}
