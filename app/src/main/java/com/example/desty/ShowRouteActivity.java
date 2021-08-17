package com.example.desty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ShowRouteActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback{

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean locationPermissionGranted = false;
    // The entry point to the Places API.
    //private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap map;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final String TAG = ShowRouteActivity.class.getSimpleName();
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    private Button info_button;
    private Connection db_conn=null;
    private int route_id = 1;
    private String route_name,route_desc,route_rating,route_views,route_country,route_city;
    ArrayList<Object[]> points = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_route);
        info_button = findViewById(R.id.show_route_info);
//        Intent i = getIntent();
//        route_id = i.getIntExtra("route_id",0);
//        System.out.println("Route Id  " + route_id);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        new Connect().execute();
        new FetchRoutePoints().execute();

        info_button.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_route_info,null);
            TextView route_name_view = layout.findViewById(R.id.routeName);
            TextView route_desc_view = layout.findViewById(R.id.routeDesc);
            TextView route_views_view = layout.findViewById(R.id.routeViews);
            TextView route_rate_view = layout.findViewById(R.id.routeRate);
            TextView route_city_view = layout.findViewById(R.id.routeCity);
            TextView route_country_view = layout.findViewById(R.id.routeCountry);
            route_name_view.setText(route_name);
            route_desc_view.setText(route_desc);
            route_views_view.setText(route_views);
            route_rate_view.setText(route_rating);
            route_city_view.setText(route_city);
            route_country_view.setText(route_country);

            Button  done = layout.findViewById(R.id.info_done);
            final PopupWindow popupWindow = new PopupWindow(ShowRouteActivity.this);
            popupWindow.setContentView(layout);
            popupWindow.setFocusable(true);
            popupWindow.showAtLocation(layout , Gravity.CENTER, 0, 0);
            done.setOnClickListener(v1 -> {
                popupWindow.dismiss();
            });
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        this.map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });
        getLocationPermission();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * The device never recorded its location,
     * which could be the case of a new device or a device that has been restored to factory settings.
     */
    @SuppressLint("MissingPermission")
    private void requestLocation(){
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.i(TAG,"Initial location for the setup");
                    }
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        updateLocationUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.setOnMyLocationButtonClickListener(this);
                requestLocation();
                putMarkers();
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    @SuppressLint("MissingPermission")
    private boolean getDeviceLocation() {
        Log.i(TAG,"Getting device location");
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            Log.i(TAG,"Fused location client task successful");
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        map.animateCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }

        return locationPermissionGranted && lastKnownLocation != null;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return getDeviceLocation();
    }

    public void putMarkers(){
        Log.i(TAG,"Putting markers to the map");
        LatLng pointLatLng=defaultLocation; // in case no location fetched.
        double lat,lng;
        for(Object[] p: points){
            lat = Double.parseDouble(p[4].toString());  // latitude of point
            lng = Double.parseDouble(p[5].toString());   // longitude of point
            pointLatLng = new LatLng(lat,lng);
            this.map.addMarker(new MarkerOptions()
                    .position(pointLatLng)
                    .title(p[2].toString()) // name of point
                    .snippet(p[3].toString())); // description of point
        }
        this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(pointLatLng,DEFAULT_ZOOM));
    }

    private class Connect extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {

            Connection connection = null;
            String conn_url;

            try {
                conn_url = BuildConfig.db_url;
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(conn_url);
                if (connection != null) {
                    Log.i("Connection Status", "Connected");
                } else
                    Log.i("Connection Status", "Not Connected");

            } catch (SQLException throwable) {
                throwable.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            db_conn=connection;
            return null;
        }
    }

    private class FetchRoutePoints extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            PreparedStatement route_statement,point_statement;
            String route_query = "SELECT * FROM [dbo].[Route] where route_id = ?";
            String point_query = "SELECT * FROM [dbo].[Point] where route_id = ?";
            Object[] columns = new Object[8]; // Route table columns
            Object[] point_columns = new Object[6];
            try {
                // Fetch route from DB
                route_statement = db_conn.prepareStatement(route_query);
                route_statement.setObject(1,route_id);
                ResultSet resultSet = route_statement.executeQuery();
                while (resultSet.next()) {
                    columns[0] = resultSet.getInt(1);   // route_id
                    columns[1] = resultSet.getInt(2);   // publisher_id
                    columns[2] = resultSet.getString(3);    //route_name
                    columns[3] = resultSet.getString(4);    // route_desc
                    columns[4] = resultSet.getFloat(5);    // rating
                    columns[5] = resultSet.getInt(6);   // views
                    columns[6] = resultSet.getString(7);   // country
                    columns[7] = resultSet.getString(8);    // city
                }
                route_name = columns[2].toString();
                route_desc = columns[3].toString();
                route_rating = columns[4].toString();
                route_views = columns[5].toString();
                route_country = columns[6].toString();
                route_city = columns[7].toString();

                //Fetch route points from DB
                point_statement = db_conn.prepareStatement(point_query);
                point_statement.setObject(1,route_id);
                ResultSet resultSet1 = point_statement.executeQuery();
                while (resultSet1.next()) {
                    point_columns[0] = resultSet1.getInt(1);   // point_id
                    point_columns[1] = resultSet1.getInt(2);   // route_id
                    point_columns[2] = resultSet1.getString(3);    // point_name
                    point_columns[3] = resultSet1.getString(4);    // point_desc
                    point_columns[4] = resultSet1.getFloat(5);    // latitude
                    point_columns[5] = resultSet1.getFloat(6);   // longitude
                    points.add(point_columns);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }


    }

}
