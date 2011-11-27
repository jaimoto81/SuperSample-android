package com.quickblox.supersamples.main.activities;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
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
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.Consts;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.definitions.QBQueries.QBQueryType;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.helpers.LocationsXMLHandler;
import com.quickblox.supersamples.sdk.helpers.Query;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.LocationsList;
import com.quickblox.supersamples.sdk.objects.RestResponse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MapViewActivity extends MapActivity implements ActionResultDelegate {

	private MapView mapView;
	List<Address> addressList;
	MapController mapController;
	private Drawable marker;
	
	private TimerTask task;
	private Timer timer;
	
	// thread callback handler
	private Handler mHandler = new Handler();
	
	private static boolean TIMER_STARTED = false;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);

		initMapView();
		initMyLocation();

		marker = getResources().getDrawable(R.drawable.map_marker_my);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		//startTimer();

		
		// get a latitude and a longitude of the current user
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Log.i("onStatusChanged", provider + String.valueOf(status));
			}

			@Override
			public void onProviderEnabled(String provider) {
				Log.i("onProviderEnabled", provider);
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			// if a location of the device will be changed,
			// send the data on the server
			@Override
			public void onLocationChanged(Location location) {
				Log.i("onLocationChanged", "onLocationChanged");
				if (location != null) {
					
					Store.getInstance().setCurrentLocation(location);
					
					Toast.makeText(
							getBaseContext(),
							"New location latitude [" + location.getLatitude()
									+ "] longitude [" + location.getLongitude()
									+ "]", Toast.LENGTH_LONG).show();
					
					String lat = Double.toString(location.getLatitude());
					String lng = Double.toString(location.getLongitude());
					
					// create entity for current user
					List<NameValuePair> formparamsGeoUser = new ArrayList<NameValuePair>();
					String currentGeoUserId = Store.getInstance().getCurrentUser().findChild("external-user-id").getText();
					formparamsGeoUser.add(new BasicNameValuePair(
							"geo_data[user_id]", currentGeoUserId));
					formparamsGeoUser.add(new BasicNameValuePair(
							"geo_data[latitude]", lat));
					formparamsGeoUser.add(new BasicNameValuePair(
							"geo_data[longitude]", lng));

					UrlEncodedFormEntity postEntityGeoDataUser = null;
					try {
						postEntityGeoDataUser = new UrlEncodedFormEntity(
								formparamsGeoUser, "UTF-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					//
					// make query
					Query.makeQueryAsync(QueryMethod.Post,
							QBQueries.CREATE_GEODATA_QUERY,
							postEntityGeoDataUser, null, MapViewActivity.this,
							QBQueries.QBQueryType.QBQueryTypeCreateGeodata);			
				}
			}
		};

		// registration of the LocationListener.
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 
				0, locListener);
		
		Store.getInstance().setCurrentLocation(locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
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
				mapController.setZoom(3);
				mapController.animateTo(whereAmI.getMyLocation());
			}
		});
		mapView.getOverlays().add(whereAmI);
	}

	@Override
	protected boolean isLocationDisplayed() {
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void completedWithResult(QBQueryType queryType, RestResponse response) {
		if (queryType == QBQueries.QBQueryType.QBQueryTypeCreateGeodata) {
			if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus201) {
				Log.i("completedWithResult", "The current location has been added to the database");
			} else{
				Log.e("completedWithResult", "The current location HAS NOT BEEN ADDED to the database!");
			}
		}
	}

	public void startTimer() {
		if (!TIMER_STARTED){
			TIMER_STARTED = true;

			timer = new Timer();
			task = new TimerTask() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							ShowAllUsers whereAreUsers = new ShowAllUsers(marker);
							mapView.getOverlays().add(whereAreUsers);
						}
					});
				}
			};
			// each 30 seconds to do
			timer.schedule(task, 0, Consts.MAP_UPDATE_PERIOD);
		}
	}
		
	@Override
	protected void onStop() {
		timer.cancel();
		super.onStop();
		
		Log.i("MapViewActivity:", "onStop");
	}
	
	@Override
	protected void onPause() {
		timer.cancel();
		TIMER_STARTED = false;
		super.onPause();
		
		Log.i("MapViewActivity:", "onPause");
	}
	
	@Override
	protected void onResume() {
		startTimer();	
		super.onResume();
		
		Log.i("MapViewActivity:", "onResume");
	}
		
	class PopupPanel {
	    View popup;
	    boolean isVisible=false;
	    
	    PopupPanel(int layout) {
	      ViewGroup parent=(ViewGroup)mapView.getParent();

	      popup=getLayoutInflater().inflate(layout, parent, false);
	                  
	      popup.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	          hide();
	        }
	      });
	    }
	    
	    View getView() {
	      return(popup);
	    }
	    
	    void show(boolean alignTop) {
	    	
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
	      
			if (alignTop) {
				lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				lp.setMargins(0, 20, 0, 0);
			} else {
				lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				lp.setMargins(0, 0, 0, 60);
			}

			hide();
	      
	      ((ViewGroup)mapView.getParent()).addView(popup, lp);
	      isVisible=true;
	    }
	    
	    void hide() {
	      if (isVisible) {
	        isVisible=false;
	        ((ViewGroup)popup.getParent()).removeView(popup);
	      }
	    }
	  }
	
	
	
	
	
	class ShowAllUsers extends ItemizedOverlay<OverlayItem> {

		private List<OverlayItem> locations = new ArrayList<OverlayItem>();
		private Drawable marker;
		LocationsList locList = null;
		private PopupPanel panel=new PopupPanel(R.layout.pop_up);

		public ShowAllUsers(Drawable marker) {
			super(marker);

			this.marker = marker;

			try {

				// Handling XML 
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();

				// Send URL to parse XML Tags 
				URL sourceUrl = new URL(QBQueries.GET_ALL_LOCATIONS_QUERY);

				// Create handler to handle XML Tags ( extends DefaultHandler ) 
				LocationsXMLHandler locXMLHandler = new LocationsXMLHandler();
				xr.setContentHandler(locXMLHandler);
				xr.parse(new InputSource(sourceUrl.openStream()));

			} catch (Exception e) {
				Log.e("XML Parsing Exception = ", e.getMessage());
			}

			// Get result from LocationsXMLHandler locationsList Object 
			locList = LocationsXMLHandler.locList;

			for (int i = 0; i < locList.getUserID().size(); i++) {
				if (locList.getUserID().get(i).equals(Store.getInstance().getCurrentUser().findChild("external-user-id").getText()) == false) {
					try {
						int lat = (int) (Double.parseDouble(locList.getLat()
								.get(i)) * 1000000);
						int lng = (int) (Double.parseDouble(locList.getLng()
								.get(i)) * 1000000);

						// the geodata adding in to list of the locations
						GeoPoint p = new GeoPoint(lat, lng);
						locations.add(new OverlayItem(p, "Hello", ""));

					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				} else
					continue;

				populate();
			}
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
		
		@Override
		protected boolean onTap(int i) {		
			OverlayItem item = getItem(i);
			GeoPoint geo = item.getPoint();
			Point pt = mapView.getProjection().toPixels(geo, null);

			View view = panel.getView();

			((TextView) view.findViewById(R.id.latitude)).setText(String
					.valueOf(geo.getLatitudeE6() / 1000000.0));
			((TextView) view.findViewById(R.id.longitude)).setText(String
					.valueOf(geo.getLongitudeE6() / 1000000.0));

			panel.show(pt.y*2>mapView.getHeight());
		
			return true;
		}
	}

}

