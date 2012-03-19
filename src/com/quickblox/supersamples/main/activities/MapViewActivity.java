package com.quickblox.supersamples.main.activities;

import android.content.Context;
import android.graphics.*;
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
import com.flurry.android.FlurryAgent;
import com.google.android.maps.*;
import com.quickblox.supersamples.R;
import com.quickblox.supersamples.main.definitions.Consts;
import com.quickblox.supersamples.main.helpers.AlertManager;
import com.quickblox.supersamples.main.objects.MapOverlayItem;
import com.quickblox.supersamples.main.views.MapPopUp;
import com.quickblox.supersamples.sdk.definitions.ActionResultDelegate;
import com.quickblox.supersamples.sdk.definitions.QBQueries;
import com.quickblox.supersamples.sdk.definitions.QBQueries.QBQueryType;
import com.quickblox.supersamples.sdk.definitions.QueryMethod;
import com.quickblox.supersamples.sdk.definitions.ResponseHttpStatus;
import com.quickblox.supersamples.sdk.helpers.Query;
import com.quickblox.supersamples.sdk.helpers.Store;
import com.quickblox.supersamples.sdk.objects.RestResponse;
import com.quickblox.supersamples.sdk.objects.XMLNode;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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


        // init progress wheel
        mapUpdateProgress = (ProgressBar) findViewById(R.id.mapUpdate_progressBar);

        // init map popup
        mapPopUp = new MapPopUp(this, (ViewGroup) mapView.getParent());


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
                if (location != null && Store.getInstance().getCurrentUser() != null) {

                    Log.i("onLocationChanged", "onLocationChanged");

                    // save current location
                    Store.getInstance().setCurrentLocation(location);

                    String lat = Double.toString(location.getLatitude());
                    String lng = Double.toString(location.getLongitude());

                    // create entity for current user
                    List<NameValuePair> formparamsGeoUser = new ArrayList<NameValuePair>();
                    formparamsGeoUser.add(new BasicNameValuePair(
                            "geo_data[latitude]", lat));
                    formparamsGeoUser.add(new BasicNameValuePair(
                            "geo_data[longitude]", lng));
                    if (Store.getInstance().getCurrentStatus() != null) {
                        formparamsGeoUser.add(new BasicNameValuePair(
                                "geo_data[status]", Store.getInstance().getCurrentStatus()));
                    }
                    formparamsGeoUser.add(new BasicNameValuePair(
                            "token", Store.getInstance().getAuthToken()));

                    UrlEncodedFormEntity postEntityGeoDataUser = null;
                    try {
                        postEntityGeoDataUser = new UrlEncodedFormEntity(
                                formparamsGeoUser, "UTF-8");
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }

                    //
                    // make query
                    Query.performQueryAsync(QueryMethod.Post,
                            QBQueries.CREATE_GEODATA_QUERY,
                            postEntityGeoDataUser, null, MapViewActivity.this,
                            QBQueries.QBQueryType.QBQueryTypeCreateGeodata);

                    Log.i("SEND OWN LOCATION ON THE SERVER", "ON");
                } else {
                    Log.i("SEND OWN LOCATION ON THE SERVER", "OFF");
                }


            }
        };

        List<String> providers = locManager.getProviders(true);
        for (String provider : providers) {

            // registration of the LocationListener.
            locManager.requestLocationUpdates(provider, Consts.MAP_CHECK_OWN_POSITION_PERIOD,
                    10, locListener);

            Store.getInstance().setCurrentLocation(locManager.getLastKnownLocation(provider));
        }

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

        // read a value which is established from CheckBoxPreference
        if (Store.getInstance().getCurrentUser() != null) {
            initMyLocation();
        }else if (ownOverlay != null){
            mapView.getOverlays().remove(ownOverlay);
            ownOverlay = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Consts.FLURRY_API_KEY);
        FlurryAgent.logEvent("run MapViewActivity");

    }

    @Override
    protected void onStop() {
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
        if(ownOverlay != null){
            return;
        }
        
        ownOverlay = new WhereAmI(this, mapView);
        // to begin follow for the updates of the location
        ownOverlay.enableMyLocation();
        ownOverlay.enableCompass(); // it's no works in the emulator
        ownOverlay.runOnFirstFix(new Runnable() {

            @Override
            public void run() {
                // Show current location and change a zoom
                mapController.setZoom(2);

                if (ownOverlay.getMyLocation() != null) {
                    mapController.animateTo(ownOverlay.getMyLocation());
                }
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

        // each 1 min to do
        timer.schedule(task, 0, Consts.MAP_UPDATE_PERIOD);
    }

    // update map query
    private void updateMap() {
        if (mapUpdateProgress.getVisibility() == View.VISIBLE) {
            return;
        }

        // show progress wheel
        MapViewActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mapUpdateProgress.setVisibility(View.VISIBLE);
            }
        });

        String query = QBQueries.GET_ALL_LOCATIONS_QUERY + "&token=" + Store.getInstance().getAuthToken();

        // make query
        Query.performQueryAsync(QueryMethod.Get, query,
                null, null, this, QBQueries.QBQueryType.QBQueryTypeGetAllLocations);
    }

    @Override
    public void completedWithResult(QBQueryType queryType, RestResponse response) {
        // no internet connection
        if (response == null) {
            mapUpdateProgress.setVisibility(View.GONE);
            AlertManager.showServerError(this, "Please check your internet connection");
            return;
        }

        switch (queryType) {
            // CREATED
            case QBQueryTypeCreateGeodata:
                if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus201) {
                    Log.i("completedWithResult", "The current location has been added to the database");
                } else {
                    Log.e("completedWithResult", "The current location HAS NOT BEEN ADDED to the database!");
                }

                break;

            // Ok
            case QBQueryTypeGetAllLocations:
                if (response.getResponseStatus() == ResponseHttpStatus.ResponseHttpStatus200) {

                    // remove 'page count' element
                    final XMLNode data = response.getBody();
                    // empty response
                    if (data.getChildren() == null) {
                        mapUpdateProgress.setVisibility(View.GONE);

                        return;
                    }

                    final List<MapOverlayItem> locationsList = new ArrayList<MapOverlayItem>();

                    processLocationsDataThread = new Thread(new Runnable() {

                        public void run() {

                            // populate chats
                            for (XMLNode child : data.getChildren()) {

                                // if user doesnt exist
                                XMLNode userNode = child.findChild("user");
                                if (userNode == null) {
                                    continue;
                                }

                                // skip own location
                                if (Store.getInstance().getCurrentUser() != null && userNode.findChild("id").getText().equals(Store.getInstance().getCurrentUser().findChild("id").getText())) {
                                    Store.getInstance().setCurrentStatus(child.findChild("status").getText());
                                    continue;
                                }

                                int lat = (int) (Double.parseDouble(child.findChild("latitude").getText()) * 1000000);
                                int lng = (int) (Double.parseDouble(child.findChild("longitude").getText()) * 1000000);

                                final MapOverlayItem overlayItem = new MapOverlayItem(new GeoPoint(lat, lng), "", "");
                                overlayItem.setUserStatus(child.findChild("status").getText());
                                String name = child.findChild("user").findChild("full-name").getText();
                                if(name.length() == 0){
                                    name = child.findChild("user").findChild("login").getText();
                                }
                                overlayItem.setUserName(name);
                                locationsList.add(overlayItem);
                            }

                            // there are no points
                            if (locationsList.isEmpty()) {
                                MapViewActivity.this.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        mapUpdateProgress.setVisibility(View.GONE);
                                    }
                                });
                                return;
                            }


                            final ShowAllUsers whereAreUsers = new ShowAllUsers(marker, locationsList);

                            // update map
                            MapViewActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // add overlays
                                    mapView.getOverlays().clear();
                                    mapView.getOverlays().add(whereAreUsers);
                                    if(ownOverlay != null){
                                        mapView.getOverlays().add(ownOverlay);
                                    }

                                    mapView.invalidate();

                                    mapUpdateProgress.setVisibility(View.GONE);
                                }
                            });
                        }
                    });

                    processLocationsDataThread.start();

                } else {
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
            mapPopUp.setData(item.getUserName(), item.getUserFullName(), item.getUserStatus());

            // show popup
            mapPopUp.show();

            return true;
        }
    }

    // Current User overlay
    public class WhereAmI extends MyLocationOverlay {

        private Context mContext;
        private float mOrientation;
        private Rect markerRect;

        public WhereAmI(Context context, MapView mapView) {
            super(context, mapView);
            mContext = context;
        }

        @Override
        protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix, GeoPoint myLocation, long when) {
            // translate the GeoPoint to screen pixels
            Point screenPts = mapView.getProjection().toPixels(myLocation, null);

            // create a rotated copy of the marker
            Bitmap arrowBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.map_marker_my);
            Matrix matrix = new Matrix();
            matrix.postRotate(mOrientation);
            Bitmap rotatedBmp = Bitmap.createBitmap(
                    arrowBitmap,
                    0, 0,
                    arrowBitmap.getWidth(),
                    arrowBitmap.getHeight(),
                    matrix,
                    true);
            // add the rotated marker to the canvas
            canvas.drawBitmap(
                    rotatedBmp,
                    screenPts.x - (rotatedBmp.getWidth() / 2),
                    screenPts.y - (rotatedBmp.getHeight() / 2),
                    null);

            markerRect = new Rect(screenPts.x - (rotatedBmp.getWidth() / 2), screenPts.y - (rotatedBmp.getHeight() / 2),
                    screenPts.x + (rotatedBmp.getWidth() / 2), screenPts.y + (rotatedBmp.getHeight() / 2));

            rotatedBmp.recycle();
        }

        public void setOrientation(float newOrientation) {
            mOrientation = newOrientation;
        }

        @Override
        public boolean onTap(GeoPoint p, MapView map) {

            Point tapPts = mapView.getProjection().toPixels(p, null);

            if (markerRect == null || tapPts == null) {
                return false;
            }

            if (!markerRect.contains(tapPts.x, tapPts.y)) {
                return false;
            }

            // show popup data
            String status = Store.getInstance().getCurrentStatus();
            if (status == null) {
                status = "<empty>";
            }
            mapPopUp.setData(Store.getInstance().getCurrentUser().findChild("login").getText(),
                    Store.getInstance().getCurrentUser().findChild("full-name").getText(), status);

            // show popup
            mapPopUp.show();

            return true;
        }
    }
}