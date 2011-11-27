package com.quickblox.supersamples.sdk.helpers;

import android.location.Location;

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
	private XMLNode currentUser;
	private XMLNode currentGeoUser;
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
	 
	public XMLNode getCurrentGeoUser() {
		return currentGeoUser;
	}
	public void setCurrentGeoUser(XMLNode currentGeoUser) {
		this.currentGeoUser = currentGeoUser;
	}
	
	public Location getCurrentLocation() {
		return currentLocation;
	}
	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}
	
	public String getCurrentStatus() {
		if(currentStatus == null){
			currentStatus = "I am QuickBlox user!";
		}
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
}