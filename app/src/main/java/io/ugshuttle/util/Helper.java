package io.ugshuttle.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Project : shuttle-system
 * Package name : io.ugshuttle.util
 * <p>
 * Application utility helper class
 */
public class Helper {
	//Shuttle database reference
	public static final String SHUTTLE_DB = "shuttle";
	
	//Legon default location
	public static final LatLng LEGON_LOCATION = new LatLng(5.6505578, -0.266265);
	
	//Shared preferences
	public static final String SHUTTLE_PREFS = "SHUTTLE_PREFS";
	public static final String USER_LOCATION_PREFS = "USER_LOCATION_PREFS";
	public static final String USER_LAT = "USER_LAT";
	public static final String USER_LNG = "USER_LNG";
	
	//Pubnub Keys and Secret
	public static final String PUBNUB_PUBLISH_KEY = "pub-c-c51c4039-309d-488e-a6cc-759aed59f172";
	public static final String PUBNUB_SUBSCRIBE_KEY = "sub-c-a863b6aa-3a8c-11e8-8394-86efddfa61f5";
	public static final String PUBNUB_SECRET = "sec-c-YTEyNTg0MzYtOTI3NS00OTVmLWE3MzAtOWRiNmY4NzkzM2Zi";
	
	//University of Ghana latlng bounds
	public static final LatLngBounds LEGON_BOUNDS = new LatLngBounds(LEGON_LOCATION, new LatLng(5.6544868, -0.1963988));
	
	//PubNub Channel
	public static final String SUBSCRIBE_CHANNEL_NAME = "SUBSCRIBE_CHANNEL_NAME";
	public static final String PUBLISH_CHANNEL_NAME = "PUBLISH_CHANNEL_NAME";
	public static final String FLIGHTPATHS_CHANNEL_NAME = "FLIGHTPATHS_CHANNEL_NAME";
	
}
