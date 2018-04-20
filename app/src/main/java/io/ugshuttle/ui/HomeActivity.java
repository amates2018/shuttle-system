package io.ugshuttle.ui;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocomplete.IntentBuilder;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.peanutsdk.widget.CircularImageView;
import io.ugshuttle.R;
import io.ugshuttle.api.DriverLocationDataManager;
import io.ugshuttle.data.CustomMarker;
import io.ugshuttle.data.Driver;
import io.ugshuttle.util.Helper;
import io.ugshuttle.util.LocationHelper;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Project : shuttle-system
 * Package name : io.ugshuttle.ui
 * <p>
 * Home activity for the student interface
 */
public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
	private static final int TRACKING_CODE = 23;
	@BindView(R.id.home_toolbar)
	Toolbar toolbar;
	@BindView(R.id.container)
	ViewGroup container;
	
	//Misc
	private GoogleMap map;
	
	//helpers
	public static final int LOCATION_REQ_CODE = 12;
	public static final int LOCATION_PERMISSION = 13;
	private LatLng lastKnownLocation;
	private SharedPreferences preferences;
	private LocationHelper locationHelper;
	
	//Data manager
	private DriverLocationDataManager dataManager;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		
		//Add title to toolbar
		toolbar.setTitle(R.string.app_name);
		toolbar.setTitleTextAppearance(this, R.style.TextAppearance_DribbbleShotDescription);
		toolbar.setTitleTextColor(getResources().getColor(R.color.text_primary_dark));
		
		//Init data manager
		dataManager = new DriverLocationDataManager(this) {
			@Override
			public void onDataLoaded(List<Driver> data) {
				if (data.isEmpty()) {
					Snackbar.make(container, "Sorry!. UG Shuttle service is currently unavailable",
							Snackbar.LENGTH_INDEFINITE).show();
				} else {
					addMarkers(data);
				}
			}
		};
		
		//Location helper
		locationHelper = new LocationHelper(this, this);
		locationHelper.connect();
		
		//Shared preferences
		preferences = getSharedPreferences(Helper.SHUTTLE_PREFS, Context.MODE_PRIVATE);
		getUserLocation();
		
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}
	
	private void getUserLocation() {
		if (preferences.contains(Helper.USER_LOCATION_PREFS)) {
			float lat = preferences.getFloat(Helper.USER_LAT, 0.0f);
			float lng = preferences.getFloat(Helper.USER_LNG, 0.0f);
			
			//Set last known location
			lastKnownLocation = new LatLng(lat, lng);
			
			if (map != null) {
				//Move camera to user's location
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 13.0f));
				map.addMarker(new MarkerOptions().title("You").position(lastKnownLocation).alpha(1.0f));
			}
		} else if (lastKnownLocation != null) {
			//Move camera to user's location
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 13.0f));
			map.addMarker(new MarkerOptions().title("You").position(lastKnownLocation).alpha(1.0f));
		}
	}
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		//Assign global variable: GoogleMap, to the new item
		this.map = googleMap;
		
		//Add custom map style
		map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.zuber_map_style));
		
		//Camera update
		CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
				.bearing(90.0f)
				.target(Helper.LEGON_LOCATION)
				.tilt(45.0f)
				.zoom(13.0f)
				.build());
		
		//Animate camera to user's current location
		map.animateCamera(cameraUpdate);
		
		//Check permission
		if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager
				.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, permission
				.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			map.setMyLocationEnabled(true);
			updateUI();
		} else {
			if (VERSION.SDK_INT >= VERSION_CODES.M) {
				requestPermissions(new String[]{permission.ACCESS_COARSE_LOCATION, permission
						.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
			}
		}
		
		//Loads all drivers currently online
		dataManager.loadAllDrivers();
	}
	
	//Add markers for all drivers retrieved
	private void addMarkers(List<Driver> drivers) {
		if (map != null && !drivers.isEmpty()) {
			for (int i = 0; i < drivers.size(); i++) {
				//Get driver model
				Driver driver = drivers.get(i);
				
				//Add marker
				GeoPoint driverLocation = driver.getGeoPoint();
				MarkerOptions options = new MarkerOptions()
						.title(driver.getCarNumber())
						.position(new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude()));
				map.addMarker(options);
				
				map.setOnMarkerClickListener(new OnMarkerClickListener() {
					@Override
					public boolean onMarkerClick(Marker marker) {
						// Get custom view
						View v = getLayoutInflater().inflate(R.layout.driver_popup, null, false);
						
						//Assign props
						TextView username = v.findViewById(R.id.driver_username);
						CircularImageView profile = v.findViewById(R.id.driver_profile);
						ImageView status = v.findViewById(R.id.driver_status);
						TextView shuttle = v.findViewById(R.id.driver_bus_number);
						ViewGroup viewGroup = v.findViewById(R.id.group);
						
						//Init props
						Glide.with(getApplicationContext())
								.load(driver.getProfile())
								.apply(RequestOptions.circleCropTransform())
								.apply(RequestOptions.placeholderOf(R.drawable.avatar_placeholder))
								.apply(RequestOptions.errorOf(R.drawable.avatar_placeholder))
								.apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
								.transition(withCrossFade())
								.into(profile);
						
						username.setText(driver.getDriver());   //Driver's username
						shuttle.setText(driver.getCarNumber()); //Driver's car number
						
						//Attach to dialog
						MaterialDialog materialDialog = new Builder(HomeActivity.this)
								.customView(v, true)
								.negativeText("Dismiss")
								.onPositive((dialog, which) -> {
									dialog.dismiss();
									enableTracking(marker);
								})
								.onNegative((dialog, which) -> dialog.dismiss())
								.build();
						
						if (driver.isStatus()) {
							status.setImageResource(android.R.color.holo_green_light);  //Online
							
							//Enable tracking when driver is online
							materialDialog.getBuilder().positiveText("Track")
									.onPositive((dialog, which) -> {
										dialog.dismiss();
										enableTracking(marker);
									});
						} else {
							//Tracking is disabled
							status.setImageResource(android.R.color.holo_red_light);    //Offline
						}
						
						materialDialog.show();
						return true;
					}
				});
			}
			
		}
		
	}
	
	@TargetApi(VERSION_CODES.M)
	@SuppressLint("MissingPermission")
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == LOCATION_PERMISSION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				map.setMyLocationEnabled(true);
				updateUI();
			} else if (shouldShowRequestPermissionRationale(permission.ACCESS_FINE_LOCATION)) {
				requestPermissions(new String[]{permission.ACCESS_COARSE_LOCATION, permission
						.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
			}
		}
	}
	
	private void updateUI() {
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setOnMyLocationClickListener(new OnMyLocationClickListener() {
			@Override
			public void onMyLocationClick(@NonNull Location location) {
				//Set last known location
				lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
				
				//Store locally
				Editor editor = preferences.edit();
				editor.putString(Helper.USER_LOCATION_PREFS, getString(R.string.app_name));
				editor.putFloat(Helper.USER_LAT, Float.parseFloat(String.valueOf(lastKnownLocation.latitude)));
				editor.putFloat(Helper.USER_LNG, Float.parseFloat(String.valueOf(lastKnownLocation.longitude)));
				editor.apply();
				
				//Move camera to user's location
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 13.0f));
				map.addMarker(new MarkerOptions().title("You").position(lastKnownLocation).alpha(1.0f));
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_search) {
			try {
				//Filter by address
				AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
						.setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
						.build();
				Intent intent = new IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
						.setFilter(typeFilter)
						.build(this);
				startActivityForResult(intent, LOCATION_REQ_CODE);
			} catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
				showMessage(e.getLocalizedMessage());
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case LOCATION_REQ_CODE:
					if (data != null) {
						Log.d(HomeActivity.class.getCanonicalName(), String.valueOf(data.getData()));
//						Place place = PlaceAutocomplete.getPlace(this, data);
//						addNewMarker(place.getLatLng(), place.getAddress());
					} else {
						showMessage("Could not get your location");
					}
					break;
			}
		}
	}
	
	//Show message to screen
	private void showMessage(CharSequence message) {
		if (message == null) {
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		} else {
			Snackbar.make(container, message, Snackbar.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onDestroy() {
		if (locationHelper != null) locationHelper.disconnect();
		dataManager.cancelLoading();
		super.onDestroy();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			//Set last known location
			lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
			
			//Store locally
			Editor editor = preferences.edit();
			editor.putString(Helper.USER_LOCATION_PREFS, getString(R.string.app_name));
			editor.putFloat(Helper.USER_LAT, Float.parseFloat(String.valueOf(lastKnownLocation.latitude)));
			editor.putFloat(Helper.USER_LNG, Float.parseFloat(String.valueOf(lastKnownLocation.longitude)));
			editor.apply();
			
			map.addMarker(new MarkerOptions().title("You").position(lastKnownLocation).alpha(1.0f));
		}
	}
	
	private void enableTracking(Marker marker) {
		CustomMarker customMarker = new CustomMarker(marker.getTitle(), marker.getSnippet(), marker.getPosition().latitude,
				marker.getPosition().longitude);
		Intent intent = new Intent(this, TrackingActivity.class);
		intent.putExtra(TrackingActivity.EXTRA_MARKER, customMarker);
		startActivityForResult(intent, TRACKING_CODE);
	}
}
