package io.ugshuttle.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import io.ugshuttle.R;
import io.ugshuttle.util.Helper;

/**
 * Project : shuttle-system
 * Package name : io.ugshuttle.ui
 * <p>
 * Home activity for the student interface
 */
public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
	//Misc
	private GoogleMap map;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		//Assign global variable: GoogleMap, to the new item
		this.map = googleMap;
		
		//Add custom map style
		map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.zuber_map_style));
		
		//Add bounds
		map.setLatLngBoundsForCameraTarget(Helper.LEGON_BOUNDS);
		
		//Camera update
		CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
				.bearing(90.0f)
				.target(Helper.LEGON_LOCATION)
				.tilt(90.0f)
				.zoom(16.0f)
				.build());
		
		//Add marker to simulate location of user
		map.addMarker(new MarkerOptions().position(Helper.LEGON_LOCATION).title(getString(R.string.app_name)));
		
		//Animate camera to user's current location
		map.animateCamera(cameraUpdate);
		
	}
}
