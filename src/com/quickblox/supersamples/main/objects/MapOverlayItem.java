package com.quickblox.supersamples.main.objects;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MapOverlayItem extends OverlayItem{

	private String geoUserName;
	private String geoUserStatus;
	
	public MapOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

	public String getGeoUserName() {
		return geoUserName;
	}

	public void setGeoUserName(String geoUserName) {
		this.geoUserName = geoUserName;
	}

	public String getGeoUserStatus() {
		return geoUserStatus;
	}

	public void setGeoUserStatus(String geoUserStatus) {
		this.geoUserStatus = geoUserStatus;
	}
}