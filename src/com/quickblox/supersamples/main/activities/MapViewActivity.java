package com.quickblox.supersamples.main.activities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.flurry.android.FlurryAgent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.helpers.AlertManager;
import com.quickblox.supersamples.main.objects.MapOverlayItem;
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
import android.widget.ProgressBar;

public class MapViewActivity extends MapActivity implements ActionResultDelegate {

	private MapView mapView;
	List<Address> addressList;
	MapController mapController;
	private Drawable marker;
	private WhereAmI ownOverlay;
	
	private MapPopUp mapPopUp;
	
	private TimerTask task;
	private Timer timer;
	
	private ProgressBar mapUpdateProgress;
	
	private Thread processLocationsDataThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);

		// Init Map
		initMapView();
		initMyLocation();
		
		
		// init progress wheel
		mapUpdateProgress = (ProgressBar)findViewById(R.id.mapUpdate_progressBar);
		
		// init map popup
		mapPopUp = new MapPopUp(this, (ViewGroup)mapView.getParent());
		
		
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
					
					// save current location
					Store.getInstance().setCurrentLocation(location);
					
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
		super.onPause();
		timer.cancel();
		task.cancel();
		timer = null;
		task = null;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		startTimer();	
	}
	
	public void onStart()
	{
	    super.onStart();
	    FlurryAgent.onStartSession(this, "B6G7VFD3ZY767YUJA1J2");
	    
	}
	
	public void onStop()
	{
	    super.onStop();
	    FlurryAgent.onEndSession(this);
	}
	
	private void initMapView() {
		mapView = (MapView) findViewById(R.id.mapview);
		mapController = mapView.getController();
		mapView.setSatellite(true);
		mapView.setBuiltInZoomControls(true);
	}

	private void initMyLocation() {
		ownOverlay = new WhereAmI(this, mapView);
		// to begin follow for the updates of the location
		ownOverlay.enableMyLocation();
		ownOverlay.enableCompass(); // it's no works in the emulator
		ownOverlay.runOnFirstFix(new Runnable() {
			@Override
			public void run() {
				// Show current location and change a zoom
				mapController.setZoom(1);
				mapController.animateTo(ownOverlay.getMyLocation());
			}
		});
		mapView.getOverlays().add(ownOverlay);
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

		// show progress wheel
		MapViewActivity.this.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				mapUpdateProgress.setVisibility(View.VISIBLE);
			}
		});

		// make query
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
					final XMLNode data = response.getBody();
					// empty response
					if(data.getChildren() == null){
						mapUpdateProgress.setVisibility(View.GONE);
						
						return;
					}
					data.getChildren().remove(0);
					
					final List<MapOverlayItem> locationsList = new ArrayList<MapOverlayItem>();
					
					processLocationsDataThread = new Thread(new Runnable() {
						public void run() {

							// populate chats
							for(XMLNode child : data.getChildren()){
								
								Log.i("GEO=", Store.getInstance().getCurrentGeoUser().toString());
								
								// skip own location
								if(child.findChild("user-id").getText().equals(Store.getInstance().getCurrentGeoUser().findChild("id").getText())){
									continue;
								}
	
								int lat = (int) (Double.parseDouble(child.findChild("latitude").getText()) * 1000000);
								int lng = (int) (Double.parseDouble(child.findChild("longitude").getText()) * 1000000);

								final MapOverlayItem overlayItem = new MapOverlayItem(new GeoPoint(lat, lng), "", "");
								overlayItem.setGeoUserStatus(child.findChild("status").getText());
									
								// get geouser name
								RestResponse response = Query.makeQuery(QueryMethod.Get, 
										String.format(QBQueries.GET_GEOUSER_QUERY_FORMAT, child.findChild("user-id").getText()),
											null, null);
								overlayItem.setGeoUserName(response.getBody().findChild("name").getText());

								locationsList.add(overlayItem);
							}
							
							// there are no points
							if(locationsList.size() == 0){
								MapViewActivity.this.runOnUiThread(new Runnable(){
									@Override
									public void run() {
										mapUpdateProgress.setVisibility(View.GONE);
									}
								});
								return;
							}
							
							
							final ShowAllUsers whereAreUsers = new ShowAllUsers(marker, locationsList);
							
							// update map
							MapViewActivity.this.runOnUiThread(new Runnable(){
								@Override
								public void run() {
									// add overlays
									mapView.getOverlays().clear();
									mapView.getOverlays().add(whereAreUsers);
									mapView.getOverlays().add(ownOverlay);
									mapView.invalidate();
									
									mapUpdateProgress.setVisibility(View.GONE);
								}
							});
			            }
					});
					
					processLocationsDataThread.start();

				}else{
					mapUpdateProgress.setVisibility(View.GONE);
					
					AlertManager.showServerError(this, "Error while updating map");
				}
				break;
		}
	}
	
	
	// Other Users overlays
	class ShowAllUsers extends ItemizedOverlay<MapOverlayItem> {

		private List<MapOverlayItem> locations = new ArrayList<MapOverlayItem>();
		private Drawable marker;


		public ShowAllUsers(Drawable marker, List<MapOverlayItem> overlayItems) {
			super(marker);

			this.marker = marker;

			// populate items
			for (MapOverlayItem overlayItem : overlayItems) {
				locations.add(overlayItem);
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
		protected MapOverlayItem createItem(int i) {
			return locations.get(i);
		}

		@Override
		public int size() {
			return locations.size();
		}
		
		// tab on marker
		@Override
		protected boolean onTap(int i) {
			MapOverlayItem item = (MapOverlayItem) getItem(i);

			// set data
			mapPopUp.setData(item.getGeoUserName(), item.getGeoUserStatus());
			
			// show popup
			mapPopUp.show();
			
			return true;
		}
	}
	
	
	// Current User overlay
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
	    
	    @Override
	    public boolean onTap(GeoPoint p, MapView map) {
	    	// show popup data
	    	mapPopUp.setData(Store.getInstance().getCurrentGeoUser().findChild("name").getText(), "It's me!");
			
			// show popup
			mapPopUp.show();
			
	    	return true;
	    }
	}
}