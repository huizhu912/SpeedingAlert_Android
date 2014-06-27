package com.example.l12.locationbasedservices;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private LocationClient mLocationClient;
	public double mlat;
	public double mlon;
	
	String stringUrl;
	InputStream is = null;
	String speedLimit = "";
	int speedLimitVal;
	float detectedSpeed;
	private static final String DEBUG_TAG = "HttpExample";
	double lat;
	double lon;
	
	TextView tv;
	TextView tvDetected;
	TextView tvAddress;
	private TextToSpeech tts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 1st parameter (Context) MainActivity
		// 2nd paramter (ConnectionCallbacks) MainActivity
		// 3rd paramter (OnConnectionFailedListener) MainActivity
		mLocationClient = new LocationClient(this, this, this);
		
		tv = (TextView)findViewById(R.id.speed_limit);
		tvDetected = (TextView)findViewById(R.id.current_speed);
		tvAddress = (TextView)findViewById(R.id.location);
		
	}
	
	 private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
	        @Override
	        protected String doInBackground(String... urls) {
	              
	            // params comes from the execute() call: params[0] is the url.
	            try {
	                return downloadUrl(urls[0]);
	                
	            } catch (IOException e) {
	                return "Unable to retrieve web page. URL may be invalid.";
	            }
	        }
	        // onPostExecute displays the results of the AsyncTask.
	        @Override
	        protected void onPostExecute(String result) {
	        	
	        	// displays the string of response from API
	        	speedLimitVal = Integer.valueOf(result);
	            tv.setText("speed limit: " + result); 
	            
	       }
	    }
	
	private String downloadUrl(String myurl) throws IOException {
	      
	      // Only display the first 500 characters of the retrieved
	      // web page content.
	          
	      try {
	          URL url = new URL(myurl);
	          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	          conn.setReadTimeout(60000 /* milliseconds */);
	          conn.setConnectTimeout(65000 /* milliseconds */);
	          conn.setRequestMethod("GET");
	          conn.setDoInput(true);
	          // Starts the query
	          conn.connect();
	          int response = conn.getResponseCode();
	          Log.d(DEBUG_TAG, "The response is: " + response);
	          is = conn.getInputStream();
	          try {
				speedLimit = (String)new Parser().parse(is).get(0);
	        	//speedLimit = (String)Collections.max(new Parser().parse(is));

	        			  
			  } catch (XmlPullParserException e) {
				e.printStackTrace();
			  }
				
				Log.d("COEN268", "Speed limit is: " + speedLimit);
				return speedLimit;

	      } finally {
	          if (is != null) {
	              is.close();
	          } 
	      }
	  }
	
	  //Reads an InputStream and converts it to a String.
	  public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
		  Reader reader = null;
		  reader = new InputStreamReader(stream, "UTF-8");        
		  char[] buffer = new char[len];
		  reader.read(buffer);
		  return new String(buffer);
	  }
	
	  private class GetAddressTask extends AsyncTask<Location, Void, String> {
			Context mContext;

			public GetAddressTask(Context context) {
				super();
				mContext = context;
			}

			/**
			 * Get a Geocoder instance, get the latitude and longitude look up the
			 * address, and return it
			 * 
			 * @params params One or more Location objects
			 * @return A string containing the address of the current location, or
			 *         an empty string if no address can be found, or an error
			 *         message
			 */
			@Override
			protected String doInBackground(Location... params) {
				Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
				// Get the current location from the input parameter list
				Location loc = params[0];
				// Create a list to contain the result address
				List<Address> addresses = null;
				try {
					/*
					 * Return 1 address.
					 */
					addresses = geocoder.getFromLocation(mlat,
							mlon, 1);
				} catch (IOException e1) {
					Log.e("LocationSampleActivity",
							"IO Exception in getFromLocation()");
					e1.printStackTrace();
					return ("IO Exception trying to get address");
				} catch (IllegalArgumentException e2) {
					// Error message to post in the log
					String errorString = "Illegal arguments "
							+ mlat + " , "
							+ mlon
							+ " passed to address service";
					Log.e("LocationSampleActivity", errorString);
					e2.printStackTrace();
					return errorString;
				}
				// If the reverse geocode returned an address
				if (addresses != null && addresses.size() > 0) {
					// Get the first address
					Address address = addresses.get(0);
					/*
					 * Format the first line of address (if available), city, and
					 * country name.
					 */
					String addressText = String.format(
							"%s, %s, %s",
							// If there's a street address, add it
							address.getMaxAddressLineIndex() > 0 ? address
									.getAddressLine(0) : "",
							// Locality is usually a city
							address.getLocality(),
							// The country of the address
							address.getCountryName());
					// Return the text
					return addressText;
				} else {
					return "No address found";
				}
			}
			
			protected void onPostExecute(String address) {
				// Display the address and road type 
				String roadType = getRoadType(address);	
				speedLimitVal = getSpeedLimit(roadType);
	            tv.setText("Speed limit: " + speedLimitVal); 
				tvAddress.setText("Location:  " + address + "(road type: " + roadType + ")");
				
			}
			
	  }

	  
	  /// code borrowed from sample app L12.LocationBasedService
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }
    
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected(Bundle arg0) {
		
		// get latitude and longitude
		Location location = mLocationClient.getLastLocation();
		
		if (location ==  null) {
			onStart();
		}
		
		Log.d("LOC", "My location: " 
		   + location.getLatitude() + "," + location.getLongitude());
		//Toast.makeText(MainActivity.this, "My location: " + mLocationClient.getLastLocation(), Toast.LENGTH_SHORT).show();
		
		LocationRequest mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(3);
		mLocationClient.requestLocationUpdates(mLocationRequest, new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				Log.d("coen268", String.format("Location update: %s, %s", 
		                location.getLatitude(), location.getLongitude()));
				mlat = location.getLatitude();
				mlon = location.getLongitude();
				Toast.makeText(MainActivity.this, "Location update: " + mlat + ", " + mlon , Toast.LENGTH_SHORT).show();
				
			// display detected speed
				detectedSpeed = location.getSpeed() * 2;
				tvDetected.setText("Current speed: " + detectedSpeed);
				
			// parse wikispeedia API and return speed limit 
				
				// dynamic lat and lon 
		        //stringUrl = "http://www.wikispeedia.org/a/marks_bb2.php?name=12345&nelat=" + Double.toString(mlat+0.002) + "&swlat=" + Double.toString(mlat-0.002) + "&nelng=" + Double.toString(mlon+0.002) + "&swlng=" + Double.toString(mlon-0.002);
		         
				// hardcoded lat and lon
				//stringUrl = "http://www.wikispeedia.org/a/marks_bb2.php?name=12345&nelat=35.198676&swlat=35.194676&nelng=-89.56558&swlng=-89.56958";
				//stringUrl = "http://www.wikispeedia.org/a/marks_bb2.php?name=12345&nelat=35.19800509&swlat=35.19800509&nelng=-89.56761047&swlng=-89.56761047";
				//stringUrl = "http://www.wikispeedia.org/a/marks_bb2.php?name=12345&nelat=37.3745&swlat=37.3735&nelng=-121.927&swlng=-121.928";
				//stringUrl = "http://www.wikispeedia.org/a/marks_bb2.php?name=12345&nelat=37.5&swlat=36.5&nelng=-121&swlng=-122";
				
		        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					//new DownloadWebpageTask().execute(stringUrl);					
					new GetAddressTask(MainActivity.this).execute(location);
				} else {
					Log.d("coen268","No network connection available.");
					tv.setText("No network connection available.");
				}
				
			// trigger the voice alarm when detectedSpeed >  speedLimitVal
				  if ((detectedSpeed > 0.0) && (speedLimitVal != 0) && (detectedSpeed > speedLimitVal)) {
	                    voiceAlert();
	              }
				
			}
		});

	}
	
	private void voiceAlert() {
		this.tts = new TextToSpeech(this, new OnInitListener(){

			@Override
			public void onInit(int arg0) {
				tts.setLanguage(Locale.US);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, "true");
				tts.speak("You are going too fast and above the speed limit.", TextToSpeech.QUEUE_ADD, map);				
			}
			
		});
	}
	
	// get type of road based on the keyword from the location address string
	private String getRoadType(String address) {
        String str = "";
        if (address.matches("(.*)(Hwy|Fwy|Highway|Freeway)(.*)")) {
            str = "highway";
        }
        else if (address.matches("(.*)(Express way|Expy|Expressway)(.*)")) {
            str = "express way";
        }
        else {
            str = "local";
        }
        return str;
    }
	
	// get speed limit based on type of road
	public int getSpeedLimit(String roadType) {
		if (roadType.equals("highway")) {
			speedLimitVal = 65;
		} else if (roadType.equals("express way")) {
			speedLimitVal = 45;
		} else {
			speedLimitVal = 30;
		}
		
		return speedLimitVal;
	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub	
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
		//} else
			if (item.getItemId() == R.id.map) {
			// Toast.makeText(this, "Take Photo", Toast.LENGTH_SHORT)
			// .show();
			Intent intent = new Intent(MainActivity.this, MapActivity.class);
			startActivity(intent);
		//} else if (item.getItemId() == R.id.action_settings) {
			//Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
		}
		return super.onOptionsItemSelected(item);
	}
}
