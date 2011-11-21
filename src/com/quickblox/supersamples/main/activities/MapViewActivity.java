package com.quickblox.supersamples.main.activities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.quickblox.supersamples.R;
import com.quickblox.supersamples.sdk.helpers.LocationsXMLHandler;
import com.quickblox.supersamples.sdk.objects.LocationsList;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MapViewActivity extends MapActivity {

	private MapView mapView;
	private Button back;
	private Button findLocBtn;
	Geocoder geocoder;
	ProgressDialog progDialog;
	List<Address> addressList;
	MapController mapController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);

		initMapView();
		initMyLocation();

		back = (Button) findViewById(R.id.back);
		findLocBtn = (Button) findViewById(R.id.findLocBtn);

		geocoder = new Geocoder(this);

		Drawable marker = getResources().getDrawable(R.drawable.marker);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		ShowAllUsers whereAreUsers = new ShowAllUsers(marker);
		mapView.getOverlays().add(whereAreUsers);

		GeoPoint pt = whereAreUsers.getCenter(); // get of a point with the
													// highest rating
		mapController.setCenter(pt);
		mapController.setZoom(8);

	}

	private void initMapView() {
		mapView = (MapView) findViewById(R.id.mapview);
		mapController = mapView.getController();
		mapView.setSatellite(true);
		mapView.setBuiltInZoomControls(true);
	}

	private void initMyLocation() {
		final MyLocationOverlay whereAmI = new MyLocationOverlay(this, mapView);
		// to begin follow for the updates of the location
		whereAmI.enableMyLocation();
		whereAmI.enableCompass(); // it's no works in the emulator
		whereAmI.runOnFirstFix(new Runnable() {

			@Override
			public void run() {
				// Show current location and change a zoom
				mapController.setZoom(15);
				mapController.animateTo(whereAmI.getMyLocation());
			}
		});
		mapView.getOverlays().add(whereAmI);
	}

	public void doClick(View v) {
		EditText loc = (EditText) findViewById(R.id.editLocation);
		String locationName = loc.getText().toString();

		progDialog = ProgressDialog.show(MapViewActivity.this, "Processing",
				"Finding location", true, false);

		findLocation(locationName);

	}

	public void findLocation(final String locationName) {

		Thread thrd = new Thread() {
			public void run() {
				try {
					// do background work
					addressList = geocoder.getFromLocationName(locationName, 5);
					// send message to handler to process results
					uiCallback.sendEmptyMessage(0);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		thrd.start();

	}

	// ui thread callback handler
	private Handler uiCallback = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// tear down dialog
			progDialog.dismiss();

			if (addressList != null && addressList.size() > 0) {
				int lat = (int) (addressList.get(0).getLatitude() * 1000000);
				int lng = (int) (addressList.get(0).getLongitude() * 1000000);
				GeoPoint pt = new GeoPoint(lat, lng);
				mapView.setSatellite(false);
				mapView.getController().setZoom(19);
				mapView.getController().animateTo(pt);

			} else {
				Dialog foundNothingDlg = new AlertDialog.Builder(
						MapViewActivity.this).setIcon(0)
						.setTitle("Failed to Find Location")
						.setPositiveButton("Ok", null)
						.setMessage("Location Not Found...").create();
				foundNothingDlg.show();
			}
		}
	};

	@Override
	protected boolean isLocationDisplayed() {
		return true;

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	// back to the Main Activity
	public void exit(View v) {
		Intent intent = new Intent();
		intent.setClass(this, StartActivity.class);

		startActivity(intent);
		finish();

	}

	class ShowAllUsers extends ItemizedOverlay<OverlayItem> {

		private List<OverlayItem> locations = new ArrayList<OverlayItem>();
		private Drawable marker;
		/** Create Object For LocationsList Class */
		LocationsList locList = null;

		public ShowAllUsers(Drawable marker) {
			super(marker);
			this.marker = marker;

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

			for (int i = 0; i < locList.getUserID().size(); i++) {

				try {
			
					int lat = (int)(Double.parseDouble(locList.getLat().get(i))*1000000);
					int lng = (int)(Double.parseDouble(locList.getLng().get(i))*1000000);
					
					GeoPoint p = new GeoPoint(lat, lng);			
					locations.add(new OverlayItem(p, "", ""));

				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			
			populate();
		}

		// a shadow of the marker
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(marker);

		}

		@Override
		protected OverlayItem createItem(int i) {
			return locations.get(i);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return locations.size();
		}

	}
}
