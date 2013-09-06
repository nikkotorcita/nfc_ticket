package com.example.tripidnfc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AuthPreferences {
	
	private static final String KEY_USER = "user";
	private static final String KEY_TOKEN = "token";
	private static final String KEY_REFRESH_TOKEN = "refresh_token";
	
	private SharedPreferences preferences;
	
	public AuthPreferences(Context context) {
		preferences = context.getSharedPreferences("tripid.auth", Context.MODE_PRIVATE);
	}
	
	public void setUser(String user) {
		Editor editor = preferences.edit();
		editor.putString(KEY_USER, user);
		editor.commit();
	}
	
	public void setToken(String token) {
		Editor editor = preferences.edit();
		editor.putString(KEY_TOKEN, token);
		editor.commit();
	}
	
	public void setRefreshToken(String refreshToken) {
		Editor editor = preferences.edit();
		editor.putString(KEY_REFRESH_TOKEN, refreshToken);
		editor.commit();
	}
	
	public String getUser() {
		return preferences.getString(KEY_USER, null);
	}
	
	public String getToken() {
		return preferences.getString(KEY_TOKEN, null);
	}
	
	public String getRefreshToken() {
		return preferences.getString(KEY_REFRESH_TOKEN, null);
	}
}