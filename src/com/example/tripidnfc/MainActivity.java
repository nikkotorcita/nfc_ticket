package com.example.tripidnfc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.content.Intent;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.nfc.*;

public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "TRIPID Main";
	
	private AuthPreferences mPreferences;
	private AccountManager accountManager;
	
	private APItasks mAPItasks;
	
	private Boolean loggedIn = false;
	
	Button billButton;
	TextView passengerName, balanceValue, fareValue, uidValue, tokenValue;
	String mBalance, mName, mUid, mToken;
	int fare = 40;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		billButton = (Button) findViewById(R.id.bill_passenger);
		passengerName = (TextView) findViewById(R.id.passenger_name);
		fareValue = (TextView) findViewById(R.id.fare_value);
		tokenValue = (TextView) findViewById(R.id.token_field);
		uidValue = (TextView) findViewById(R.id.uid_field);
		
		billButton.setOnClickListener(this);
		
		mAPItasks = new APItasks(this);
		
		mPreferences = new AuthPreferences(this);
		if(mPreferences.getUser() != null && mPreferences.getToken() != null) {
			mAPItasks.execute(TripidConstants.VALIDATE_TOKEN);
		}
		else {
			Account userAccount = new Account(TripidConstants.userId, TripidConstants.accountType);
			
			mPreferences.setUser(userAccount.name);
			mAPItasks.execute(TripidConstants.GET_TOKEN);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.bill_passenger: new APItasks(this).execute(TripidConstants.CONFIRM_PASSENGER, mName, mToken);
		break;
		}
	}
	
	void processIntent(Intent intent) {
		String payload;
		
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		payload = new String(msg.getRecords()[0].getPayload());
		
		Log.d(TAG, "nfc payload = " + payload);
		
		mUid = payload.split("-")[1].split(",")[0];
		mToken = payload.split("-")[1].split(",")[1];
		mName = payload.split("-")[1].split(",")[2];
		
		passengerName.setText(mName);
		uidValue.setText(mUid);
		tokenValue.setText(mToken);
		fareValue.setText(Integer.toString(fare));
	}
}
