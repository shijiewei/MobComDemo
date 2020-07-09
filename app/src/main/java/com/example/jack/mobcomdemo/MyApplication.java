package com.example.jack.mobcomdemo;

import android.os.StrictMode;
import android.util.Log;

import com.mob.MobApplication;

public class MyApplication extends MobApplication {
	private static final String TAG = "MyApplication";

	@Override
	public void onCreate() {
		Log.d(TAG, "Application onCreated");
		super.onCreate();
		enableStrictMode();
	}

	private void enableStrictMode() {
//		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//				.detectDiskReads()
//				.detectDiskWrites()
//				.detectNetwork()   // or .detectAll() for all detectable problems
//				.penaltyLog()
//				.build());
//		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//				.detectLeakedSqlLiteObjects()
//				.detectLeakedClosableObjects()
//				.penaltyLog()
//				.penaltyDeath()
//				.build());
	}
}
