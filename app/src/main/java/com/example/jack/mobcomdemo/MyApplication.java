package com.example.jack.mobcomdemo;

import android.util.Log;

import com.mob.MobApplication;

public class MyApplication extends MobApplication {
	private static final String TAG = "MyApplication";

	@Override
	public void onCreate() {
		Log.d(TAG, "Application onCreated");
		super.onCreate();
	}
}
