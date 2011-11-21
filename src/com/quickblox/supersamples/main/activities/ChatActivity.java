package com.quickblox.supersamples.main.activities;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.quickblox.supersamples.sdk.helpers.LocationsXMLHandler;
import com.quickblox.supersamples.sdk.objects.LocationsList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatActivity extends Activity {

	/** Create Object For LocationsList Class */
	LocationsList locList = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 *  Create a new layout to display the view
		 */
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(1);

		/** Create a new textview array to display the results */
		TextView userID[];
		TextView latitude[];
		TextView longitude[];

		try {

			/** Handling XML */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			/** Send URL to parse XML Tags */
			URL sourceUrl = new URL(
					"http://geopos.aws02.mob1serv.com/geodata/find.xml?app.id=38&page_size=100");

			/** Create handler to handle XML Tags ( extends DefaultHandler ) */
			LocationsXMLHandler locXMLHandler = new LocationsXMLHandler();
			xr.setContentHandler(locXMLHandler);
			xr.parse(new InputSource(sourceUrl.openStream()));

		} catch (Exception e) {
			System.out.println("XML Parsing Exception = " + e);
		}

		/** Get result from LocationsXMLHandler locationsList Object */
		locList = LocationsXMLHandler.locList;

		/** Assign textview array length by arraylist size */
		userID = new TextView[locList.getUserID().size()];
		latitude = new TextView[locList.getUserID().size()];
		longitude = new TextView[locList.getUserID().size()];

		/** Set the result text in textview and add it to layout */
		for (int i = 0; i < locList.getUserID().size(); i++) {
			userID[i] = new TextView(this);
			userID[i].setText("userID = " + locList.getUserID().get(i));
			latitude[i] = new TextView(this);
			latitude[i].setText("Latitude = " + locList.getLat().get(i));
			longitude[i] = new TextView(this);
			longitude[i]
					.setText("Longitude = " + locList.getLng().get(i));

			layout.addView(userID[i]);
			layout.addView(latitude[i]);
			layout.addView(longitude[i]);
		}

		/** Set the layout view to display */
		setContentView(layout);

	}

}
