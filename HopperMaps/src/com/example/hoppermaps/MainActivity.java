package com.example.hoppermaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	private GoogleMap mMap;
	
	LatLng fromCoor;
	LatLng toCoor;
	String fromName;
	String toName;
	Button searchBtt;
	ImageButton BttFromBusStop;
	ImageButton BttFromBuilding;
	ImageButton BttToBusStop;
	ImageButton BttToBuilding;
	
	public static String FROM_NAME="from_name";
	public static String TO_NAME="to_name";
	public static String LIST_BUS="list bus";
	public static String FROM="from";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 if (mMap == null) 
	            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
               // setUpMap();
            }
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(52.951883,-1.186656), 15.0f));
            mMap.setMyLocationEnabled(true);
           
            //get current location
            Location location = mMap.getMyLocation();
            
            Bundle extras = getIntent().getExtras();
    		if(extras !=null) {
    			fromName = extras.getString(FROM_NAME);
    			fromCoor = extras.getParcelable(ChooseActivity.FROM_COOR);
    			toName = extras.getString(TO_NAME);
    			toCoor = extras.getParcelable(ChooseActivity.TO_COOR);
    		} else {
    			if (location != null) {
                    fromCoor = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    toCoor = new LatLng(location.getLatitude(),
                            location.getLongitude());
                }
                fromName=toName="My location";
    		}
            
            
            
            TextView from = (TextView) findViewById(R.id.from);
            TextView to = (TextView) findViewById(R.id.to);
            
            from.setText(fromName);
            to.setText(toName);
            
            BttFromBusStop = (ImageButton) findViewById(R.id.fromBusStop);
            BttFromBusStop.setId(1);
            BttFromBusStop.setOnClickListener(new ShowOptionsListener());
            BttFromBuilding = (ImageButton) findViewById(R.id.fromBuilding);
            BttFromBuilding.setId(2);
            BttFromBuilding.setOnClickListener(new ShowOptionsListener());
            BttToBusStop = (ImageButton) findViewById(R.id.toBusStop);
            BttToBusStop.setId(3);
            BttToBusStop.setOnClickListener(new ShowOptionsListener());
            BttToBuilding = (ImageButton) findViewById(R.id.toBuilding);
            BttToBuilding.setId(4);
            BttToBuilding.setOnClickListener(new ShowOptionsListener());
            
            searchBtt = (Button) findViewById(R.id.bttSearch);
            searchBtt.setOnClickListener(new OnClickListener() {
            	public String nearestBusstopFrom = "Neward Hall";
            	public String nearestBusstopTo = "Lenton and Wortley Hall";
            	Map<String,LatLng> CoorMapBusDrawingList;
            	List<String> busDrawingList;
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//connect 2 point
					LatLng previousLatLng;
					CoorMapBusDrawingList = new HashMap<String,LatLng>();
					busDrawingList = new ArrayList<String>();
					String[] busStopandWaypointsArray = getResources().getStringArray(R.array.busStopandWaypointsArray);
					
					for (int i=0; i<busStopandWaypointsArray.length; i++){
						String[] separate = busStopandWaypointsArray[i].split("#");
						LatLng previousCoor = null;
						if (separate[0].equals(nearestBusstopFrom)){
							for (int j = i; j< (i+busStopandWaypointsArray.length); j++){
								if (j>=busStopandWaypointsArray.length) j= j-busStopandWaypointsArray.length;
								separate = busStopandWaypointsArray[j].split("#");
								LatLng coor = new LatLng(Double.parseDouble(separate[1]), Double.parseDouble(separate[2]));
								if (previousCoor!=null){
									String url = getDirectionsUrl(previousCoor, coor);				
									Log.w("asdf", url);
									DownloadTask downloadTask = new DownloadTask();
									
									// Start downloading json data from Google Directions API
									downloadTask.execute(url);
								}
								previousCoor = new LatLng(Double.parseDouble(separate[1]), Double.parseDouble(separate[2]));
								CoorMapBusDrawingList.put(separate[0], coor);
								
								if (separate[0].equals(nearestBusstopTo)) break;
							}
						}
					}
					/*
					String url = getDirectionsUrl(fromCoor, toCoor);				
					
					DownloadTask downloadTask = new DownloadTask();
					
					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
					*/
				}
				
				public void reduceListBusstopandWaypoints(){
					
				}
				
			});
            
            
            
	}
	
	public class ShowOptionsListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			
			Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
			if (v.getId()==1 || v.getId()==3) intent.putExtra(LIST_BUS, true);
			else intent.putExtra(LIST_BUS, false);
			if (v.getId()==1 || v.getId()==2) intent.putExtra(FROM, true);
			else intent.putExtra(FROM, false);
			intent.putExtra(FROM_NAME, fromName);
			intent.putExtra(TO_NAME, toName);
			intent.putExtra(ChooseActivity.FROM_COOR, fromCoor);
			intent.putExtra(ChooseActivity.TO_COOR, toCoor);
		    startActivity(intent);

		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private String getDirectionsUrl(LatLng origin,LatLng dest){
		
		// Origin of route
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		
		// Destination of route
		String str_dest = "destination="+dest.latitude+","+dest.longitude;		
		
					
		// Sensor enabled
		String sensor = "sensor=false";			
					
		// Building the parameters to the web service
		String parameters = str_origin+"&"+str_dest+"&"+sensor;
					
		// Output format
		String output = "json";
		
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
		
		
		return url;
	}
	
	/** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url 
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url 
                urlConnection.connect();

                // Reading data from url 
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                
                data = sb.toString();

                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }

	
	
	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>{			
				
		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {
				
			// For storing data from web service
			String data = "";
					
			try{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;		
		}
		
		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);			
			
			ParserTask parserTask = new ParserTask();
			//Log.w("result",result);
			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
				
		}		
	}
	
	/** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
    	
    	// Parsing the data in non-ui thread    	
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
			
			JSONObject jObject;	
			List<List<HashMap<String, String>>> routes = null;			           
            
            try{
            	jObject = new JSONObject(jsonData[0]);
            	DirectionsJSONParser parser = new DirectionsJSONParser();
            	
            	// Starts parsing data
            	routes = parser.parse(jObject);    
            }catch(Exception e){
            	e.printStackTrace();
            }
            return routes;
		}
		
		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();
			
			// Traversing through all the routes
			for(int i=0;i<result.size();i++){
				Log.w("","loop");
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				
				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);
				
				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);					
					
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);	
					
					points.add(position);						
				}
				
				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(2);
				lineOptions.color(Color.RED);	
				
			}
			
			// Drawing polyline in the Google Map for the i-th route
			mMap.addPolyline(lineOptions);							
		}			
    }   
	
}
