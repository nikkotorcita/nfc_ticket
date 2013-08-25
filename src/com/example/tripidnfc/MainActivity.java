package com.example.tripidnfc;

import android.os.Bundle;
import android.os.Parcelable;
import android.content.Intent;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.nfc.*;

public class MainActivity extends Activity implements android.view.View.OnClickListener {

	private static final String TAG = "Tripid";
	
	private static final String userId = "tripid101@gmail.com";
	private static final String passwd = "tripid";
	private static final String accountType = "com.tripid";
	private static final String appId = "991e9208868e25be68303c262aae27f88352df16091548f573c5bc8e17708d4d";
	private static final String secret = "6333e5637bee9bd0e915f9cd5d5fc81f6082d2c4b0a3c1c36d183a7cba5bbdbe";
	
	private String accessToken;
	private String refreshToken;
	
	private AuthPreferences mPreferences;
	
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
		
		mPreferences = new AuthPreferences(this);
		if(mPreferences.getUser() != null && mPreferences.getToken() != null) {
			doAuthentication();
		}
		else {
			addAccount();
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
		if(v.getId() == R.id.bill_passenger) {
			if(mBalance != null) {
				
			}
		}
	}
	
	void addAccount() {
		Account userAccount = null;
		
	}
	
	void doAuthentication() {
		
	}
	
	void processIntent(Intent intent) {
		String payload;
		
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		payload = new String(msg.getRecords()[0].getPayload());
		mUid = payload.split("-")[1].split(",")[0];
		mToken = payload.split("-")[1].split(",")[1];
		mName = payload.split("-")[1].split(",")[2];
		
		passengerName.setText(mName);
		uidValue.setText(mUid);
		tokenValue.setText(mToken);
		fareValue.setText(Integer.toString(fare));
	}
}
