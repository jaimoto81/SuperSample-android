package com.quickblox.supersamples.sdk.helpers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.quickblox.supersamples.sdk.objects.LocationsList;

public class LocationsXMLHandler extends DefaultHandler {

	Boolean currentElement = false;
	String currentValue = null;
	public static LocationsList locList = null;

	public static LocationsList getLocationsList() {
		return locList;
	}

	public static void setLocationsList(LocationsList locList) {
		LocationsXMLHandler.locList = locList;
	}
	
	@Override
	public void startDocument() throws SAXException {
		locList = new LocationsList();
	}

	/** Called when tag starts */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		currentElement = true;
	}

	/** Called when tag closing */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		currentElement = false;

		Log.i("localName", localName);

		/** set value */
		if (localName.equalsIgnoreCase("user-id")) {

			Log.i("currentValue", currentValue);

			locList.setUserID(currentValue);
		} else if (localName.equalsIgnoreCase("latitude"))
			locList.setLat(currentValue);
		else if (localName.equalsIgnoreCase("longitude"))
			locList.setLng(currentValue);

	}

	/** Called to get tag characters */
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (currentElement) {
			currentValue = new String(ch, start, length);
			currentElement = false;
		}

	}

}
