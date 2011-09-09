package com.liuapps.c2dmsample;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * @author liuisaac
 *
 * This class is used to store the shared information in this app. In this case
 * it's the shared preferences
 */
public class C2DMSampleApplication extends Application {
	
	private SharedPreferences prefs;
	public static final String NEW_REGID_INTENT = "com.liuapps.c2dmsample.NEW_REGID";
	public static final String REGID_VAL_INTENT = "com.liuapps.c2dmsample.REGID_VAL";
	public static final String INTENT_SEND_PERMISSION = "com.liuapps.c2dmsample.SEND_NOTIFICATIONS";
	public static final String INTENT_RECEIVE_PERMISSION = "com.liuapps.c2dmsample.RECEIVE_NOTIFICATIONS";

	@Override
	public void onCreate() {
		super.onCreate();
		// get the shared preferences for the app
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	
	/**
	 * @return regId or null
	 * 
	 * This function returns the regId string if it's present, null if not
	 */
	public String getRegId() {
		return prefs.getString("regId", null);
	}

	/**
	 * @param regId - null or a String representing the registration id
	 * 
	 * This function can set or clear the regId preference. If null is received 
	 * then the preference is cleared, or else is it set
	 */
	public void setRegId(String regId) {
		SharedPreferences.Editor editor = prefs.edit();
		if (regId == null)
			editor.remove("regId");
		else 
			editor.putString("regId", regId);
		editor.commit();
	}
}
