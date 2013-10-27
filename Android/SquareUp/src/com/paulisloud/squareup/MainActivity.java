package com.paulisloud.squareup;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private VenueArrayAdapter adapter;
	private static final String CLIENT_ID = "Z34RZK1SCL3I0BH4HGQTEWEYIKCFEYAX4MZZFDWSVIJHIKYT";
	private static final String CLIENT_SECRET = "VZLBRPB4FMSFIZ4PFY1UT0DZ1JSHPSPWVMT5YN2CVNFMICBT";
	private static final String URL_STUMP = "https://api.foursquare.com/v2/venues/trending?ll=";
	private static final String jsonMockup = 	"{ \"response\" : { \"venues\": [ { \"name\": \"Krasse Butze\", "+
			"\"location\": { \"distance\" : 123}, \"hereNow\": { \"count\": 321}}]}}";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.adapter = null;
		setContentView(R.layout.activity_main);
		Intent trendListen = new Intent(this, TrendListenService.class);
    	hasNewTrending();

		JSONArray venues = AppData.getTheData().getJsonArray();
		Log.i("SquareUp", "json get "+venues.toString());
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < venues.length(); ++i) {
			try {
				list.add(venues.getJSONObject(i).getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
				Log.i("SquareUp", "bad json: "+venues.toString());
			}
		}
		this.adapter = new VenueArrayAdapter(this, list);
		final ListView listview = (ListView) findViewById(R.id.listview);
		listview.setAdapter(adapter);
	}
	
	public boolean hasNewTrending() {
		String readTrending = readTrending();
		Log.i("SquareUp", readTrending);
		Set<String> nextSet = new HashSet<String>();
		JSONArray venues = null;
		try {
			JSONObject jsonObject = new JSONObject(readTrending);
			venues = jsonObject.getJSONObject("response").getJSONArray("venues");
			for (int i = 0; i < venues.length(); ++i) {
				nextSet.add(venues.getJSONObject(i).getString("id"));
			}
			if(!AppData.getTheData().getVenueSet().containsAll(nextSet)) {
				AppData.getTheData().setJsonArray(venues);
				Log.i("SquareUp", "json put "+venues.toString());
				return true;
			}
			AppData.getTheData().setVenueSet(nextSet);
		}
		catch (Exception e) {
			Log.i("SquareUp", readTrending);
			e.printStackTrace();
		}
		return false;
	}
	
	private String readTrending()
	{
		HttpClient client = new DefaultHttpClient();
		String locationString = getLocationString();
		String url =URL_STUMP
					+locationString
					+"&radius=5000&limit=10&client_id="
					+CLIENT_ID+"&client_secret="
					+CLIENT_SECRET+"&v=20131026";
		Log.i("SquareUp","http-request: "+url);
		HttpGet httpGet = new HttpGet(url);
		StringBuilder builder = new StringBuilder();
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			}
			else {
				return(jsonMockup);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return jsonMockup;
		}
		return builder.toString();
	}

	private String getLocationString() {
		double latitude = 53.550556;
		double longitude = 9.993333;
		try {
			LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			Location location = null;
			for(String provider : lm.getProviders(true)) {
				Log.i("SquareUp",provider);
				location = lm.getLastKnownLocation(provider);
				if(location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		Log.i("SquareUp","latitute = "+latitude+" longitude = "+longitude);
		String ll = latitude+","+longitude;
		return ll;
	}

	
	
	private class VenueArrayAdapter extends ArrayAdapter<String> {
		  private final Context context;
		  private final JSONArray items;

		  public VenueArrayAdapter(Context context, List<String> values) {
		    super(context, R.layout.rowlayout, values);
		    this.items = AppData.getTheData().getJsonArray();
		    this.context = context;
		  }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		    TextView nameView = (TextView) rowView.findViewById(R.id.name);
		    TextView checkinView = (TextView) rowView.findViewById(R.id.checkins);
		    TextView distanceView = (TextView) rowView.findViewById(R.id.distance);
		    try {
		    	nameView.setText(items.getJSONObject(position).getString("name"));
		    	checkinView.setText(items.getJSONObject(position)
		    						  .getJSONObject("hereNow").getInt("count")+" checked in");
		    	distanceView.setText(items.getJSONObject(position)
		    						  .getJSONObject("location")
		    						  .getInt("distance")+" meters");
		    } catch (Exception e){
		    	e.printStackTrace();
		    } finally {
		    	
		    }
		    return rowView;
		  }
		} 
}
