package com.liuapps.c2dmsample;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class C2DMSampleApplication extends Application {
	private SharedPreferences prefs;
	public static final String NEW_REGID_INTENT = "com.liuapps.c2dmsample.NEW_REGID";
	public static final String REGID_VAL_INTENT = "com.liuapps.c2dmsample.REGID_VAL";
	public static final String INTENT_SEND_PERMISSION = "com.liuapps.c2dmsample.SEND_NOTIFICATIONS";
	public static final String INTENT_RECEIVE_PERMISSION = "com.liuapps.c2dmsample.RECEIVE_NOTIFICATIONS";

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	public String getRegId() {
		return prefs.getString("regId", null);
	}

	public void setRegId(String regId) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("regId", regId);
		editor.commit();
	}
}
