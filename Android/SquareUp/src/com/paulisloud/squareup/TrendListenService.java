package com.paulisloud.squareup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ListView;

public class TrendListenService extends Service {

	private static final String CLIENT_ID = "Z34RZK1SCL3I0BH4HGQTEWEYIKCFEYAX4MZZFDWSVIJHIKYT";
	private static final String CLIENT_SECRET = "VZLBRPB4FMSFIZ4PFY1UT0DZ1JSHPSPWVMT5YN2CVNFMICBT";
	private static final String URL_STUMP = "https://api.foursquare.com/v2/venues/trending?ll=";
	private static final String jsonMockup = 	"{ \"response\" : { \"venues\": [ { \"name\": \"Krasse Butze\", "+
			"\"location\": { \"distance\" : 123}, \"hereNow\": { \"count\": 321}}]}}";

	public TrendListenService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		hasNewTrending();
		//new TrendListenThread(this).start();
		return Service.START_STICKY;
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
	
	private void pushVenues() {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setContentTitle("New hot venue")
		        .setContentText("Exciting things are going on around you");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MainActivity.class);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder.build();
		mNotificationManager.notify(0, mBuilder.build());
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


@Override
public IBinder onBind(Intent intent) {
	return null;
}

private class TrendListenThread extends Thread {
	TrendListenService service;
	public TrendListenThread(TrendListenService service) {
		this.service = service;
	}
	public void run() {
		while(this.isAlive()) {
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			if(service.hasNewTrending()) {
				service.pushVenues();
			}
		}
	}
}
}
