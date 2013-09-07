package com.example.tripidnfc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class APItasks extends AsyncTask<String, String, Void> {

	private static final String TAG = "TRIPID APItasks";
	
	private AuthPreferences mPreferences;
	
	String token, refreshToken;
	String cmd, arg1, arg2;
	String nfcToken, nfcName;
	String postExecMessage;
	Context mParent;
	
	int tripID;  
	
	public APItasks(Context context) {
		mPreferences = new AuthPreferences(context);
		mParent = context;
	}
	
	@Override
	protected Void doInBackground(String... params) {
		int count = params.length;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost;
		
		if(count == 1){
			cmd = params[0];
		}
		else if(count == 2) {
			cmd = params[0];
			arg1 = params[1];
		}
		else if(count == 3) {
			cmd = params[0];
			arg1 = params[1];
			arg2 = params[2];
		}
		
		if(cmd == TripidConstants.GET_TOKEN) {
			httppost = new HttpPost(TripidConstants.OAUTH_URL);
			JSONObject jsonObj;
	
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
				nameValuePairs.add(new BasicNameValuePair("username", TripidConstants.userId));
				nameValuePairs.add(new BasicNameValuePair("password", TripidConstants.passwd));
				nameValuePairs.add(new BasicNameValuePair("client_id", TripidConstants.appId));
				nameValuePairs.add(new BasicNameValuePair("client_secret", TripidConstants.secret));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
				HttpResponse response = httpclient.execute(httppost);
			
				jsonObj = jsonParser(response);
				
				token = jsonObj.getString("access_token");
				refreshToken = jsonObj.getString("refresh_token");
				
				mPreferences.setToken(token);
				mPreferences.setRefreshToken(refreshToken);
			
			} catch (JSONException e) {
				Log.e(TAG, "JSONException : " + e.toString());
			} catch (ClientProtocolException e) {
				Log.e(TAG, "ClientProtocolException : " + e.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(cmd == TripidConstants.VALIDATE_TOKEN) {
			String url = TripidConstants.TOKEN_URL + mPreferences.getToken();
			int statusCode;
			
			HttpGet httpget= new HttpGet(url);
			
			try {
				HttpResponse httpresponse = httpclient.execute(httpget);
				statusCode = httpresponse.getStatusLine().getStatusCode();
				Log.d(TAG, "status code = " + statusCode);
				if(statusCode == 200) {
					Log.d(TAG, "Token valid");
				}
				else if(statusCode == 401) {
					Log.d(TAG, "Token invalid. Will try refreshing token..");
					
					httppost = new HttpPost(TripidConstants.OAUTH_URL);
					JSONObject jsonObj;
					
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
					nameValuePairs.add(new BasicNameValuePair("refresh_token", mPreferences.getRefreshToken()));
					nameValuePairs.add(new BasicNameValuePair("client_id", TripidConstants.appId));
					nameValuePairs.add(new BasicNameValuePair("client_secret", TripidConstants.secret));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
					HttpResponse response = httpclient.execute(httppost);
					if(response.getStatusLine().getStatusCode() == 200) {
						jsonObj = jsonParser(response);
						
						try {
							token = jsonObj.getString("access_token");
							refreshToken = jsonObj.getString("refresh_token");
							
							mPreferences.setToken(token);
							mPreferences.setRefreshToken(refreshToken);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} catch (ClientProtocolException e) {
				Log.e(TAG, "ClientProtocolException : " + e.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(cmd == TripidConstants.CONFIRM_PASSENGER) {
			String tripID;
			
			tripID = getTripID();
			if(tripID != null)
				confirmTicket(tripID, arg1, arg2);
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		if(cmd == TripidConstants.GET_TOKEN) {
			Log.d(TAG, "access token = " + mPreferences.getToken());
			Log.d(TAG, "refresh token = " + mPreferences.getRefreshToken());
		}
		else if(cmd == TripidConstants.CONFIRM_PASSENGER) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mParent);
			builder.setMessage(postExecMessage)
			       .setCancelable(false)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                //do things
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	
	private JSONObject jsonParser(HttpResponse response) {
		JSONObject jsonObj = null;
		InputStream is = null;
		String json = "";
		
		HttpEntity httpentity = response.getEntity();
		
		try {
			is = httpentity.getContent();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			
			is.close();
			json = sb.toString();
			
			jsonObj = new JSONObject(json);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e(TAG, "Error parsing data " + e.toString());
		}
		
		return jsonObj;
	}
	
	private String getTripID() {
		String url = TripidConstants.TRIPS_URL + mPreferences.getToken();
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = null;
		int statusCode = 0;
		String tripID = null;
				
		try {
			response = httpclient.execute(httpget);
			statusCode = response.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		if(statusCode == 200) {
			try {
				String jsonStr = EntityUtils.toString(response.getEntity());
				JSONArray tripArray = new JSONArray(jsonStr);
				
				for(int i = 0; i < tripArray.length(); i++) {
					JSONObject tripObj = tripArray.getJSONObject(i);
					if(tripObj.getString("status").equals("active")) {
						tripID = tripObj.getString("id");
						Log.d(TAG, "active trip identified with ID : " + tripID);
					}
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			JSONArray jsonTrips = new JSONArray(copyFrom))
		}
		else {
			Log.d(TAG, "error status code = " + statusCode);
		}
		
		if(tripID == null) {
			Log.d(TAG, "no upcoming trips present");
			postExecMessage = "No upcoming trips present";
		}
		
		return tripID;
	}
	
	private boolean confirmTicket(String tripID, String nfcName, String token) {
		String url = TripidConstants.TICKETS_URL + tripID + "/tickets" + "?access_token=" + mPreferences.getToken();
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = null;
		HttpResponse confirmResponse = null;
		int statusCode = 0;
		int confirmStatusCode = 0;
		String passengerName = null;
		String ticketStatus = null;
		String ticketId = null;
		String ticketType = null;
		
		Log.d(TAG, "url = " + url);
		
		try {
			response = httpclient.execute(httpget);
			statusCode = response.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(statusCode == 200) {
			try {
				String jsonStr = EntityUtils.toString(response.getEntity());
				JSONArray ticketArray = new JSONArray(jsonStr);
			
				for(int i = 0; i < ticketArray.length(); i++) {
					JSONObject ticketObj = ticketArray.getJSONObject(i);
					JSONObject userObj = ticketObj.getJSONObject("user");
					JSONArray passengerArray = ticketObj.getJSONObject("trip").getJSONArray("passengers");
					
					ticketId = ticketObj.getString("id");
					passengerName = userObj.getString("first_name") + " " + userObj.getString("last_name");
					ticketStatus = ticketObj.getString("status");
					ticketType = ticketObj.getString("type");
					
					Log.d(TAG, "NAME = " + passengerName + ", ticketId = " + ticketId + ", status = " + ticketStatus + ", type = " + ticketType);
					
					if(passengerName.equals(nfcName ) && ticketType.equals("PassengerTicket")) {
						if(ticketStatus.equals("approved")) {
							Log.d(TAG, "Passenger matched with tagged NFC information.. confirming passenger ticket.");
							String confirmUrl = TripidConstants.TICKETS_URL + tripID + "/tickets/" + ticketId + "/confirm?access_token=" + mPreferences.getToken();
							
							Log.d(TAG, "confirmUrl = " + confirmUrl);
							
							HttpPut httpput = new HttpPut(confirmUrl);
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
							nameValuePairs.add(new BasicNameValuePair("token", token));
							httpput.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							
							confirmResponse = httpclient.execute(httpput);
							confirmStatusCode = confirmResponse.getStatusLine().getStatusCode();
							Log.d(TAG, "confirmStatusCode = " + confirmStatusCode);
							
							if(confirmStatusCode == 204) {
								Log.d(TAG, "Passenger " + passengerName + " has been confirmed on this trip.");
								postExecMessage = "Passenger " + passengerName + " has been confirmed on this trip.";
								return true;
							}
							
							else {
								Log.d(TAG, "Failed to confirm passenger " + passengerName);
								postExecMessage = "Failed to confirm passenger " + passengerName;
								return false;
							}
						}
						else if(ticketStatus.equals("confirmed")) {
							Log.d(TAG, "passenger " + passengerName + " is already confirmed!");
							postExecMessage = "passenger " + passengerName + " is already confirmed!";
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return false;
	}
}














