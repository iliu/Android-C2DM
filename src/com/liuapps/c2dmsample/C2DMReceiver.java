package com.liuapps.c2dmsample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class C2DMReceiver extends BroadcastReceiver{
	static private final String TAG = C2DMReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
			Log.d(TAG, "Received C2DM Registration Packet");
			handleRegistration(context, intent);
		} else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
			Log.d(TAG, "Received C2DM Data Packet");
			handleData(context, intent); 
		}
	}
	
	private void handleData(Context context, Intent intent) {
		String app_name = (String)context.getText(R.string.app_name);
		String message =  intent.getStringExtra("message");
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(android.R.drawable.stat_notify_chat, app_name + ": " + message, 0);
	   
		PendingIntent pendingIntent = PendingIntent.getActivity(context, -1, new Intent(context, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT); // 
		notification.when = System.currentTimeMillis();  
		notification.flags |= Notification.FLAG_AUTO_CANCEL;  
		notification.setLatestEventInfo(context, app_name, message, pendingIntent); // 
		notificationManager.notify(0, notification);
	}
	
	private void handleRegistration (Context context, Intent intent) {
		String regId = intent.getStringExtra("registration_id");
		String error = intent.getStringExtra("error"); 
		String unregistered = intent.getStringExtra("unregistered");

		if (error != null) {
			Log.e(TAG, String.format("Received error: %s\n", error));
			if (error.equals("ACCOUNT MISSING")) {
				Toast.makeText(context, "Please add a google account to your device.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, "Registration Error: " + error, Toast.LENGTH_LONG).show();
			}
		} else if (unregistered != null) {
			Log.e(TAG, String.format("Unregistered: %s\n", unregistered));
			Toast.makeText(context, "Unregistered: " + unregistered, Toast.LENGTH_LONG).show();
			// TODO: send POST to server to unregister device from C2DM
		} else if (regId != null) {
			// TODO send regID to server in ANOTHER THREAD				
			Log.d(TAG, String.format("Sending regId: %s to server...", regId));
			
			
			//Update our Home Activity
			Intent updateIntent = new Intent(C2DMSampleApplication.NEW_REGID_INTENT);
			updateIntent.putExtra(C2DMSampleApplication.REGID_VAL_INTENT, regId);
			context.sendBroadcast(updateIntent, C2DMSampleApplication.INTENT_RECEIVE_PERMISSION);
			
			//store it into shared prefs
			((C2DMSampleApplication)context.getApplicationContext()).setRegId(regId);
		} 
	}

	
}
