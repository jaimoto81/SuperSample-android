package com.quickblox.supersamples.sdk.helpers;

import android.location.Location;
import android.util.Log;

import com.quickblox.supersamples.sdk.objects.XMLNode;

public class Store {
	
	/*
	 * Singleton
	 */
	private static Store instance;
	    
	public static synchronized Store getInstance() {
		if (instance == null) {
			instance = new Store();
	    } 
	    return instance;
	}
	 
	/*
	 * Fields
	 */
	private String authToken;
	
	private XMLNode currentUser;
	private Location currentLocation;
	private String currentStatus;
	 
	/*
     * Properties
     */
	public XMLNode getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(XMLNode currentUser) {
		this.currentUser = currentUser;
	}
	
	public Location getCurrentLocation() {
		return currentLocation;
	}
	public void setCurrentLocation(Location currentLocation) {
		if(currentLocation != null){
			Log.i("setCurrentLocation", "Set!");
		}
		this.currentLocation = currentLocation;
	}
	
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
		
		Log.i("setAuthToken", authToken);
	}
}