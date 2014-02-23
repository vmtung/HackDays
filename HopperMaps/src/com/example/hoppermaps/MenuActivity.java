package com.example.hoppermaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuActivity extends Activity {

	Button searchBtt;
	ImageButton BttFromBusStop;
	ImageButton BttFromBuilding;
	ImageButton BttToBusStop;
	ImageButton BttToBuilding;
	String fromName;
	String toName;
	LatLng fromCoor;
	LatLng toCoor;
	public static ArrayList<String> listStop = new ArrayList<String>();
	public static HashMap<String,LatLng> CoorMapBusDrawingList;
	
	public static String FROM_NAME="from_name";
	public static String TO_NAME="to_name";
	public static String LIST_BUS="list bus";
	public static String FROM="from";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		 
		 
		 LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		  Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
         
         Bundle extras = getIntent().getExtras();
 		if(extras !=null) {
 			fromName = extras.getString(FROM_NAME);
 			toName = extras.getString(TO_NAME);
 		} else {
 			if (location != null) {
                 fromCoor = new LatLng(location.getLatitude(),
                         location.getLongitude());
                 toCoor = new LatLng(location.getLatitude(),
                         location.getLongitude());
             }
             fromName=toName="My location";
 		}
		
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
        	
        	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//connect 2 point
				CoorMapBusDrawingList = new HashMap<String,LatLng>();
				String[] busStopandWaypointsArray = getResources().getStringArray(R.array.busStopandWaypointsArray);
				
				for (int i=0; i<busStopandWaypointsArray.length; i++){
					String[] separate = busStopandWaypointsArray[i].split("#");
					if (separate[0].equals(nearestBusstopFrom)){
						for (int j = i; j< (i+busStopandWaypointsArray.length); j++){
							if (j>=busStopandWaypointsArray.length) j= j-busStopandWaypointsArray.length;
							separate = busStopandWaypointsArray[j].split("#");
							LatLng coor = new LatLng(Double.parseDouble(separate[1]), Double.parseDouble(separate[2]));
							
							listStop.add(separate[0]);
							CoorMapBusDrawingList.put(separate[0], coor);
							
							if (separate[0].equals(nearestBusstopTo)) break;
						}
					}
				}
				
				LinearLayout directionOpts = (LinearLayout) findViewById(R.id.chooseDirection);
				Button btt = new Button(getApplicationContext());
				btt.setText("by bus");
				btt.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						Log.d("coor", "size "+CoorMapBusDrawingList.size());
						intent.putExtra(MainActivity.COOR_LIST, CoorMapBusDrawingList);
						intent.putExtra(MainActivity.NAME_LIST, listStop);
						// TODO Auto-generated method stub
						startActivity(intent);
					}
				});
				directionOpts.addView(btt);
				
				//go to main and draw
			}
			
			
		});
        
        TextView from = (TextView) findViewById(R.id.from);
        TextView to = (TextView) findViewById(R.id.to);
        
        from.setText(fromName);
        to.setText(toName);
        
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
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}
