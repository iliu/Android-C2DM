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

public class HomeActivity extends Activity {
	private static final String TAG = HomeActivity.class.getSimpleName();
	C2DMSampleApplication app;
	Button buttonReg, buttonUnreg;
	TextView textView;
	IntentFilter filter;
	IdReceiver receiver;

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
		registerReceiver(receiver, filter, C2DMSampleApplication.INTENT_SEND_PERMISSION, null);
		setRegIdText();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	private void setRegIdText() {
		String regId = app.getRegId();
		if (regId == null) {
			textView.setText("Device is not registered, click the button to register device");
			buttonUnreg.setEnabled(false);
		}
		else {
			textView.setText(String.format("Device is registered with id: %s", regId));
			buttonUnreg.setEnabled(true);
			//print out regID in log 
			Log.d(TAG, String.format("Reg Id: %s", regId));
			//Copy to clipboard
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
			clipboard.setText(regId);
			Toast.makeText(this, "Registration ID copied to clipboard", Toast.LENGTH_LONG).show();
		}
	}
	
	
	class IdReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("IdReceiver", "onReceived");
			setRegIdText();
		}
		
	}
}