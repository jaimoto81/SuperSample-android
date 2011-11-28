package com.quickblox.supersamples.main.activities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.views.MapPopUp;
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.Consts;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.definitions.QBQueries.QBQueryType;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.helpers.Query;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.RestResponse;
import com.quickblox.supersamples.sdk.objects.XMLNode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("MapViewActivity:", "onCreate");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);

		initMapView();
		initMyLocation();
		
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
							"geo_data[status]", Store.getInstance().getCurrentStatus()));
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
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Consts.MAP_CHECK_OWN_POSITION_PERIOD, 
				0, locListener);
		
		Store.getInstance().setCurrentLocation(locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
		
		marker = getResources().getDrawable(R.drawable.map_marker_other);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
	}

	@Override
	protected void onPause() {
		Log.i("MapViewActivity:", "onPause");
		
		super.onPause();
		timer.cancel();
		task.cancel();
		timer = null;
		task = null;
	}
	
	@Override
	protected void onResume() {
		Log.i("MapViewActivity:", "onResume");
		
		super.onResume();
		startTimer();	
	}
	
	private void initMapView() {
		mapView = (MapView) findViewById(R.id.mapview);
		mapController = mapView.getController();
		mapView.setSatellite(true);
		mapView.setBuiltInZoomControls(true);
	}

	private void initMyLocation() {
		final WhereAmI wai = new WhereAmI(this, mapView);
		// to begin follow for the updates of the location
		wai.enableMyLocation();
		wai.enableCompass(); // it's no works in the emulator
		wai.runOnFirstFix(new Runnable() {

			@Override
			public void run() {
				// Show current location and change a zoom
				mapController.setZoom(3);
				mapController.animateTo(wai.getMyLocation());
			}
		});
		mapView.getOverlays().add(wai);
	}

	@Override
	protected boolean isLocationDisplayed() {
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void startTimer() {
		timer = new Timer();
		task = new TimerTask() {
				@Override
				public void run() {
					updateMap();
				}
		};
		
		// each 30 seconds to do
		timer.schedule(task, 0, Consts.MAP_UPDATE_PERIOD);
	}
	
	// update map query
	private void updateMap(){
		Query.makeQueryAsync(QueryMethod.Get, QBQueries.GET_ALL_LOCATIONS_QUERY,
				null, null, this, QBQueries.QBQueryType.QBQueryTypeGetAllLocations);
	}
		
	@Override
	public void completedWithResult(QBQueryType queryType, RestResponse response) {
		switch(queryType){
			case QBQueryTypeCreateGeodata:
				if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus201) {
					Log.i("completedWithResult", "The current location has been added to the database");
				} else{
					Log.e("completedWithResult", "The current location HAS NOT BEEN ADDED to the database!");
				}
			
			break;
			
			case QBQueryTypeGetAllLocations:
				if(response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus200){
					// remove 'page count' element
					XMLNode data = response.getBody();
					data.getChildren().remove(0);
					ShowAllUsers whereAreUsers = new ShowAllUsers(marker, data);
					mapView.getOverlays().add(whereAreUsers);
				}
				break;
		}
	}
	
	class ShowAllUsers extends ItemizedOverlay<OverlayItem> implements ActionResultDelegate {

		private List<OverlayItem> locations = new ArrayList<OverlayItem>();
		private Drawable marker;
		private MapPopUp panel = new MapPopUp(MapViewActivity.this, (ViewGroup)mapView.getParent());
		private TextView textUserId;
		private TextView textUserStatus;
		private View view;

		public ShowAllUsers(Drawable marker, XMLNode data) {
			super(marker);

			this.marker = marker;

			for (XMLNode child : data.getChildren()) {
				if (child.findChild("user-id").getText().equals(Store.getInstance().getCurrentGeoUser().findChild("id").getText()) == false) {
					try {
						int lat = (int) (Double.parseDouble(child.findChild("latitude").getText()) * 1000000);
						int lng = (int) (Double.parseDouble(child.findChild("longitude").getText()) * 1000000);

						// the geodata adding in to list of the locations
						GeoPoint p = new GeoPoint(lat, lng);
						locations.add(new OverlayItem(p, "", ""));

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
			return locations.size();
		}
		
		@Override
		protected boolean onTap(int i) {		
			OverlayItem item = getItem(i);
			GeoPoint geo = item.getPoint();
			
			Point pt = mapView.getProjection().toPixels(geo, null);

			View view = panel.getView();
			
			ImageButton butClosePopUp = (ImageButton) view.findViewById(R.id.butClosePopUp);
			
			
			
			
			butClosePopUp.setOnClickListener(new View.OnClickListener() {
	    		public void onClick(View v) {
	    			panel.hide();
	    		}
	    	});

			panel.show();
			
			return true;
		}

		@Override
		public void completedWithResult(QBQueryType queryType,
				RestResponse response) {
			switch (queryType) {

			case QBQueryTypeGetGeoUser:
				if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus200) {
					
					XMLNode data = response.getBody();
					data.getChildren().remove(0);
					ShowAllUsers whereAreUsers = new ShowAllUsers(marker, data);
					mapView.getOverlays().add(whereAreUsers);
					
					textUserId = (TextView)view.findViewById(R.id.textUserID);
					textUserStatus = (TextView)view.findViewById(R.id.textStatusID);
					
					textUserId.setText(response.getBody().findChild("user-id").getText());
					
					
				}
				break;
			}

		}
	}
	
	// change custom bitmap of the current user
	public class WhereAmI extends MyLocationOverlay {
	    private Context mContext;
	    private float   mOrientation;

	    public WhereAmI(Context context, MapView mapView) {
	        super(context, mapView);
	        mContext = context;
	    }

	    @Override 
	    protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix, GeoPoint myLocation, long when) {
	        // translate the GeoPoint to screen pixels
	        Point screenPts = mapView.getProjection().toPixels(myLocation, null);

	        // create a rotated copy of the marker
	        Bitmap arrowBitmap = BitmapFactory.decodeResource( mContext.getResources(), R.drawable.map_marker_my);
	        Matrix matrix = new Matrix();
	        matrix.postRotate(mOrientation);
	        Bitmap rotatedBmp = Bitmap.createBitmap(
	            arrowBitmap, 
	            0, 0, 
	            arrowBitmap.getWidth(), 
	            arrowBitmap.getHeight(), 
	            matrix, 
	            true
	        );
	        // add the rotated marker to the canvas
	        canvas.drawBitmap(
	            rotatedBmp, 
	            screenPts.x - (rotatedBmp.getWidth()  / 2), 
	            screenPts.y - (rotatedBmp.getHeight() / 2), 
	            null
	        );
	    }

	    public void setOrientation(float newOrientation) {
	         mOrientation = newOrientation;
	    }
	}
	
}