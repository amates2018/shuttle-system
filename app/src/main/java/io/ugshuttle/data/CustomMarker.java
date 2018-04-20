package io.ugshuttle.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Custom marker to facilitate sending data to a new intent as a parcel
 */
public class CustomMarker implements Parcelable {
	
	private String title;
	private String snippet;
	private double lat;
	private double lng;
	
	public CustomMarker() {
	}
	
	public CustomMarker(String title, String snippet, double lat, double lng) {
		this.title = title;
		this.snippet = snippet;
		this.lat = lat;
		this.lng = lng;
	}
	
	protected CustomMarker(Parcel in) {
		title = in.readString();
		snippet = in.readString();
		lat = in.readDouble();
		lng = in.readDouble();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(snippet);
		dest.writeDouble(lat);
		dest.writeDouble(lng);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Creator<CustomMarker> CREATOR = new Creator<CustomMarker>() {
		@Override
		public CustomMarker createFromParcel(Parcel in) {
			return new CustomMarker(in);
		}
		
		@Override
		public CustomMarker[] newArray(int size) {
			return new CustomMarker[size];
		}
	};
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSnippet() {
		return snippet;
	}
	
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	
	public double getLat() {
		return lat;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public double getLng() {
		return lng;
	}
	
	public void setLng(double lng) {
		this.lng = lng;
	}
}
