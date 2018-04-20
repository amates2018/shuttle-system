package io.ugshuttle.data;

import com.google.firebase.firestore.GeoPoint;

/**
 * Project : shuttle-system
 * Package name : io.ugshuttle.data
 */
public class Driver {
	private String key;
	private String carNumber;
	private String driver;
	private String phone;
	private String profile;
	private boolean status;
	private GeoPoint geoPoint;
	
	public Driver() {
	}
	
	public Driver(String key, String carNumber, String driver, String phone, String profile, boolean status, GeoPoint geoPoint) {
		this.key = key;
		this.carNumber = carNumber;
		this.driver = driver;
		this.phone = phone;
		this.profile = profile;
		this.status = status;
		this.geoPoint = geoPoint;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getCarNumber() {
		return carNumber;
	}
	
	public String getDriver() {
		return driver;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public GeoPoint getGeoPoint() {
		return geoPoint;
	}
	
	public String getProfile() {
		return profile;
	}
	
	public boolean isStatus() {
		return status;
	}
}
