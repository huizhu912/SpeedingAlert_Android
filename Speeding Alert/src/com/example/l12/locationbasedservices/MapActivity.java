package com.example.l12.locationbasedservices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;
import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MapActivity extends Activity {
	static final LatLng SCU = new LatLng(37.3498, -121.9395);
	private GoogleMap googleMap;
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		setUpMapIfNeeded();
		
		//map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
		  //      .getMap();
		    //Marker scu = map.addMarker(new MarkerOptions().position(SCU)
		      //  .title("SCU").snippet("College").icon(BitmapDescriptorFactory
			    //        .fromResource(R.drawable.ic_launcher)));

		    // Move the camera instantly to SCU with a zoom of 15.
		    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(SCU, 15));

		    // Zoom in, animating the camera.
		    //map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		
	}
	
	  
	  
	private void setUpMapIfNeeded() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (googleMap == null) {
	        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	                            .getMap();
	        // Check if we were successful in obtaining the map.
	        if (googleMap != null) {
	            // The Map is verified. It is now safe to manipulate the map.
	        	setUpMap();

	        }
	    }
	}



	private void setUpMap() {
		//Enable MyLocation Layer of Google Map
		googleMap.setMyLocationEnabled(true);
		//Get Location Manager object from System Service Location_Service
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		//Create a criteria object to retrieve provider
		Criteria criteria = new Criteria();
		
		//Get the name of the best provider
		String provider = locationManager.getBestProvider(criteria,  true);
		
		//Get Current Location
		Location myLocation = locationManager.getLastKnownLocation(provider);
		
		//set map type
		//googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		
		//Get Latitude of the current location
		double latitude = myLocation.getLatitude();
		
		//Get Longitude of the current location
		double longitude = myLocation.getLongitude();
		
		//Create a Latling object for the current location
		LatLng latlng = new LatLng(latitude, longitude);
		
		//Show the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
		
		//Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
		googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!"));
		
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//if (item.getItemId() == R.id.action_search) {
			//Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
	//	} else
			if (item.getItemId() == R.id.map) {
			// Toast.makeText(this, "Take Photo", Toast.LENGTH_SHORT)
			// .show();
			
			Intent intent = new Intent(MapActivity.this, MapActivity.class);
			startActivity(intent);
		//} else if (item.getItemId() == R.id.action_settings) {
			//Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
		}
		return super.onOptionsItemSelected(item);
	}

}
