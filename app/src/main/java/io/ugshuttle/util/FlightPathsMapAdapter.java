package io.ugshuttle.util;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlightPathsMapAdapter {
	private static final String TAG = FlightPathsMapAdapter.class.getName();
	
	private Polyline polyline;
	private final Activity activity;
	private final GoogleMap map;
	
	private final List<LatLng> polylinePoints;
	
	public FlightPathsMapAdapter(Activity activity, GoogleMap map) {
		this.activity = activity;
		this.map = map;
		this.polylinePoints = new ArrayList<>();
	}
	
	public void locationUpdated(Map<String, String> newLocation) {
		if (newLocation.containsKey("lat") && newLocation.containsKey("lng")) {
			String lat = newLocation.get("lat");
			String lng = newLocation.get("lng");
			
			doUiUpdate(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
		} else {
			Log.w(TAG, "message ignored: " + newLocation);
		}
	}
	
	private void doUiUpdate(LatLng location) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				polylinePoints.add(location);
				
				if (polyline != null) {
					polyline.setPoints(polylinePoints);
				} else {
					polyline = map.addPolyline(new PolylineOptions().addAll(polylinePoints));
				}
				
				map.moveCamera(CameraUpdateFactory.newLatLng(location));
			}
		});
	}
}
