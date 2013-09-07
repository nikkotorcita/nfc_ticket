package com.example.tripidnfc;

public class TripidConstants {
	public static final String userId = "tripid201@gmail.com";
	public static final String passwd = "tripid";
	public static final String accountType = "com.tripid";
	public static final String appId = "991e9208868e25be68303c262aae27f88352df16091548f573c5bc8e17708d4d";
	public static final String secret = "6333e5637bee9bd0e915f9cd5d5fc81f6082d2c4b0a3c1c36d183a7cba5bbdbe";
	
	public static final String BASE_URL = "http://staging.tripid.com.ph";
	public static final String OAUTH_URL = BASE_URL + "/oauth/token";
	public static final String TOKEN_URL = BASE_URL + "/api/user?access_token=";
//	public static final String TRIPS_URL = BASE_URL + "/api/user/trips?sort=departure&access_token=";
	public static final String TRIPS_URL = BASE_URL + "/api/user/trips?type=upcoming&access_token=";
	public static final String TICKETS_URL = BASE_URL + "/api/trips/";
	public static final String HEADER = "Content-Type: application/json";
	
	public static final String GET_TOKEN = "get_token";
	public static final String VALIDATE_TOKEN = "validate_token";
	public static final String CONFIRM_PASSENGER = "confirm_passenger";
}