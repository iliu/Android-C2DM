package com.liuapps.c2dmsample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

// extend BroadcastReceiver to receive intents
public class C2DMReceiver extends BroadcastReceiver{
	static private final String TAG = C2DMReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		// The action for registration reply is com.google.android.c2dm.intent.REGISTRATION
		if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
			Log.d(TAG, "Received C2DM Registration Packet");
			// Call the handleRegistration function to handle registration
			handleRegistration(context, intent);

			// The action for data reply is com.google.android.c2dm.intent.RECEIVE
		} else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
			Log.d(TAG, "Received C2DM Data Packet");
			// Call the handleData function to handle c2dm packet
			handleData(context, intent); 
		}
	}

	private void handleData(Context context, Intent intent) {
		String app_name = (String)context.getText(R.string.app_name);
		String message =  intent.getStringExtra("message");

		// Use the Notification manager to send notification
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// Create a notification using android stat_notify_chat icon. 
		Notification notification = new Notification(android.R.drawable.stat_notify_chat, app_name + ": " + message, 0);

		// Create a pending intent to call the HomeActivity when the notification is clicked
		PendingIntent pendingIntent = PendingIntent.getActivity(context, -1, new Intent(context, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT); // 
		notification.when = System.currentTimeMillis();  
		notification.flags |= Notification.FLAG_AUTO_CANCEL; 
		// Set the notification and register the pending intent to it
		notification.setLatestEventInfo(context, app_name, message, pendingIntent); //
		
		// Trigger the notification
		notificationManager.notify(0, notification);
	}

	private void handleRegistration (Context context, Intent intent) {
		// These strings are sent back by google
		String regId = intent.getStringExtra("registration_id");
		String error = intent.getStringExtra("error"); 
		String unregistered = intent.getStringExtra("unregistered");
		
		if (error != null) {
			// If there is an error, then we log the error
			Log.e(TAG, String.format("Received error: %s\n", error));
			if (error.equals("ACCOUNT MISSING")) {
				// ACCOUNT MISSING is sent back when the device doesn't have a google account registered
				Toast.makeText(context, "Please add a google account to your device.", Toast.LENGTH_LONG).show();
			} else {
				// Other errors
				Toast.makeText(context, "Registration Error: " + error, Toast.LENGTH_LONG).show();
			}
		} else if (unregistered != null) {
			// This is returned when you are unregistering your device from c2dm
			Log.d(TAG, String.format("Unregistered: %s\n", unregistered));
			Toast.makeText(context, "Unregistered: " + unregistered, Toast.LENGTH_LONG).show();
			
			// TODO: send POST to web server to unregister device from sending list

			//Clear the shared prefs
			((C2DMSampleApplication)context.getApplicationContext()).setRegId(null);

			//Update our Home Activity
			updateHome(context);
		} else if (regId != null) {
			// You will get a regId if nothing goes wrong and you tried to register a device
			Log.d(TAG, String.format("Got regId: %s", regId));
			
			// TODO send regID to server in ANOTHER THREAD

			//Store it into shared prefs
			((C2DMSampleApplication)context.getApplicationContext()).setRegId(regId);

			//Update our Home Activity
			updateHome(context);
			
		} 
	}
	
	private void updateHome(Context context) {
		//Update our Home Activity by sending it an intent
		Intent updateIntent = new Intent(C2DMSampleApplication.NEW_REGID_INTENT);
		context.sendBroadcast(updateIntent, C2DMSampleApplication.INTENT_RECEIVE_PERMISSION);
	}

}
