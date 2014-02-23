package com.example.hoppermaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChooseActivity extends Activity {
	String fromName;
	String toName;
	ListView listOptions;
	boolean listBus=false;
	boolean from = false;
	List<String> list;
	Map<String,LatLng> CoorMap;
	LatLng fromCoor;
	LatLng toCoor;
	
	public static String FROM_COOR="from coor";
	public static String TO_COOR="to coor";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose);
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
		    fromName = extras.getString(MainActivity.FROM_NAME);
		    toName = extras.getString(MainActivity.TO_NAME);
		    listBus=extras.getBoolean(MainActivity.LIST_BUS);
		    from=extras.getBoolean(MainActivity.FROM);
		    fromCoor=extras.getParcelable(FROM_COOR);
		    toCoor=extras.getParcelable(TO_COOR);
		}
		CoorMap = new HashMap<String,LatLng>();
		
		listOptions = (ListView) findViewById(R.id.listOptions);
		
		ArrayAdapter<String> adapter = null;
		String[] optionsArray;
		if (listBus){
			optionsArray =  getResources().getStringArray(R.array.busStopArray);
		} else {
			optionsArray =  getResources().getStringArray(R.array.buildingArray);
		}
		
		list = new ArrayList<String>();
		for (int i=0; i<optionsArray.length; i++) {
			String[] separate = optionsArray[i].split("#");
			list.add(separate[0]);
			LatLng coor = new LatLng(Double.parseDouble(separate[1]), Double.parseDouble(separate[2]));
			CoorMap.put(separate[0], coor);
		}
		
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		listOptions.setAdapter(adapter);
		
		listOptions.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		        String text = list.get(position);
		        Intent i = new Intent(getApplicationContext(), MainActivity.class);
		        if (from) {
		        	i.putExtra(MainActivity.FROM_NAME, text);
		        	i.putExtra(FROM_COOR, CoorMap.get(text));
		        	i.putExtra(MainActivity.TO_NAME, toName);
		        	i.putExtra(TO_COOR, toCoor);
		        }
		        else {
		        	i.putExtra(MainActivity.TO_NAME, text);
		        	i.putExtra(TO_COOR, CoorMap.get(text));
		        	i.putExtra(MainActivity.FROM_NAME, fromName);
		        	i.putExtra(FROM_COOR, fromCoor);
		        }// <-- Assumed you image is Parcelable
		        startActivity(i);
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose, menu);
		return true;
	}

}
