package com.liuapps.c2dmsample;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author liuisaac
 *
 * This class is the main activity of the app. It shows two buttons and a text
 */
public class HomeActivity extends Activity {
	private static final String TAG = HomeActivity.class.getSimpleName();
	
	C2DMSampleApplication app; // This is where our shared pref is
	Button buttonReg, buttonUnreg; // To access the two buttons
	TextView textView; // To access the on screen text
	IntentFilter filter; // Used to catch a new regId intent sent from C2DMReceiver
	IdReceiver receiver; // The receiver to receiver intents from C2DMReceiver

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        app = (C2DMSampleApplication) getApplication();
        buttonReg = (Button) findViewById(R.id.buttonReg);
        buttonUnreg = (Button) findViewById(R.id.buttonUnReg);
        textView = (TextView) findViewById(R.id.textStatus);
        filter = new IntentFilter(C2DMSampleApplication.NEW_REGID_INTENT);
        receiver = new IdReceiver();
    
        // Add an on click listener to the button Register. When it's clicked
        // we want to send out the intent to register our device
        buttonReg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Create registration intent
				Intent regIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
				// Identify your app
				regIntent.putExtra("app", PendingIntent.getBroadcast(HomeActivity.this, 0, new Intent(), 0));
				// Identify role account server will use to send
				regIntent.putExtra("sender", "roleemail@gmail.com");
				// Start the registration process
				startService(regIntent);
			}
		});

        // Add an on click listener to the button Unregister. When it's clicked
        // we want to send out the intent to unregister our device
        buttonUnreg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Create registration intent
				Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
				// Identify your app
				unregIntent.putExtra("app", PendingIntent.getBroadcast(HomeActivity.this, 0, new Intent(), 0));
				// Start the registration process
				startService(unregIntent);
			}
		});
    }

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		// We only want to register this intent receiver if the activity is active
		registerReceiver(receiver, filter, C2DMSampleApplication.INTENT_SEND_PERMISSION, null);
		// Configure what's shown on screen
		setDisplay();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// If the activity is paused, then we don't want to receive intents anymore
		unregisterReceiver(receiver);
	}

	private void setDisplay() {
		// Get the shared pref from the C2DMSampleApplication class
		String regId = app.getRegId();
		
		if (regId == null) {
			//If there isn't a registration id yet, then prompt the user to register
			textView.setText("Device is not registered, click the register button to register device");
			//Disable unregister button
			buttonUnreg.setEnabled(false);
		}
		else {
			//If there is a registration id, show it, log it, copy it to clipboard
			textView.setText(String.format("Device is registered with id: %s", regId));
			// Enable unregister button
			buttonUnreg.setEnabled(true);
			//print out regID in log 
			Log.d(TAG, String.format("Reg Id: %s", regId));
			//Copy to clipboard
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
			clipboard.setText(regId);
			//Notify user via Toast
			Toast.makeText(this, "Registration ID copied to clipboard", Toast.LENGTH_LONG).show();
		}
	}

	
	/**
	 * @author liuisaac
	 * This is our broadcast receiver class to receive intents that's sent from C2DMReceiver
	 */
	class IdReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("IdReceiver", "onReceived");
			// Refresh the display if we receive an intent from C2DMReceiver
			setDisplay();
		}
		
	}
}