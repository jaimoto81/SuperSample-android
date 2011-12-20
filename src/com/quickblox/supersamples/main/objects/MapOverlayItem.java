package com.quickblox.supersamples.main.objects;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MapOverlayItem extends OverlayItem{

	private String userName;
	private String userFullName;
	private String userStatus;

	
	public MapOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}
	
	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	

}