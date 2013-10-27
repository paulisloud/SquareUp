package com.paulisloud.squareup;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;

public class AppData {

	static AppData theData;
	
	public static AppData getTheData() {
		if(theData == null) {
			theData = new AppData();
		}
		return theData;
	}
	
	private JSONArray jsonArray;
	private Set<String> venueSet;
	
	public Set<String> getVenueSet() {
		return venueSet;
	}

	public void setVenueSet(Set<String> venueSet) {
		this.venueSet = venueSet;
	}

	public JSONArray getJsonArray() {
		return jsonArray;
	}

	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	private AppData() {
		jsonArray = new JSONArray();
		this.venueSet = new HashSet<String>();
	}
	
}
