package org.uDevelop.newyearapp;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class LoadScreenActivity extends Activity {
	private final static long sDelay = 2200; //delay in ms
	private Timer mTimer;
	private Context mContext; 
	private StorageAdapter mStorage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_screen);
		mTimer = new Timer(); 
		mContext = this;
		mStorage = new JSonStorageAdapter(this);
		mStorage.syncronize();
		mTimer.schedule(new TimerBody(), sDelay); 		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mStorage.close();
	}
	
	private class TimerBody extends TimerTask {
		@Override
	    public void run() {
			mTimer.cancel();
			Intent intent = new Intent(mContext, MainActivity.class);
			startActivity(intent);
			finish();
	    }
	}
}
