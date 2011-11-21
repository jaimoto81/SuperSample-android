package com.quickblox.supersamples.sdk.objects;

import java.util.ArrayList;
import java.util.Collection;

/** Contains getter and setter method for varialbles */
public class LocationsList {

	/** Variables */
	private ArrayList<String> userID = new ArrayList<String>();
	private ArrayList<String> latitude = new ArrayList<String>();
	private ArrayList<String> longitude = new ArrayList<String>();

	/**
	 * In Setter method default it will return arraylist change that to add
	 */

	public ArrayList<String> getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID.add(userID);
	}

	public ArrayList<String> getLat() {
		return latitude;
	}

	public void setLat(String latitude) {
		this.latitude.add(latitude);
	}

	public ArrayList<String> getLng() {
		return longitude;
	}

	public void setLng(String longitude) {
		this.longitude.add(longitude);
	}

}
