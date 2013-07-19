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

	Button billButton;
	TextView passengerName, balanceValue, fareValue;
	String mBalance, mName;
	int fare = 40;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		billButton = (Button) findViewById(R.id.bill_passenger);
		passengerName = (TextView) findViewById(R.id.passenger_name);
		balanceValue = (TextView) findViewById(R.id.balance_value);
		fareValue = (TextView) findViewById(R.id.fare_value);
	}

	@Override
	public void onResume() {
		super.onResume();
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.bill_passenger) {
			if(mBalance != null) {
				
			}
		}
	}
	
	void processIntent(Intent intent) {
		String payload;
		
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		payload = new String(msg.getRecords()[0].getPayload());
		mName = payload.split("-")[1].split(",")[0];
		mBalance = payload.split("-")[1].split(",")[1];
		
		passengerName.setText(mName);
		balanceValue.setText(mBalance);
		fareValue.setText(Integer.toString(fare));
	}
}
