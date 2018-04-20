package io.ugshuttle.ui;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import io.ugshuttle.BuildConfig;
import io.ugshuttle.R;
import io.ugshuttle.data.CustomMarker;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback {
	private static final String TAG = "TrackingActivity";
	public static final String EXTRA_MARKER = "EXTRA_MARKER";
	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracking);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}
	
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		
		Intent intent = getIntent();
		if (intent.hasExtra(EXTRA_MARKER)) {
			//Get intent data
			CustomMarker marker = intent.getParcelableExtra(EXTRA_MARKER);
			
			try {
				map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.zuber_map_style));
			} catch (NotFoundException e) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "onMapReady: " + e.getLocalizedMessage());
				}
			}
			
			//Get shuttle's position from intent data
			LatLng shuttlePosition = new LatLng(marker.getLat(), marker.getLng());
			
			//Add marker to map
			map.addMarker(new MarkerOptions()
					.position(shuttlePosition)
					.title(marker.getTitle())
					.snippet(marker.getSnippet())
			);
			
			//Animate camera to marker's position
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(shuttlePosition, 16.0f));
		}
		
	}
}
