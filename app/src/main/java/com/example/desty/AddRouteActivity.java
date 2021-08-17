// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


package com.example.desty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class AddRouteActivity extends AppCompatActivity
        implements
        GoogleMap.OnMapClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

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
    // A default location (Ankara, Turkey) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(39.925533, 32.866287);
    private static final int DEFAULT_ZOOM = 10;
    private double longitude;
    private double latitude;
    private static final String TAG = AddRouteActivity.class.getSimpleName();
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private Geocoder geocoder = null;
    /*
 Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLngs;
*/
    private Button menu_button,finish_route;
    private boolean isSave = false;
    private int point_count = 0;
    private ArrayList<Double[]> point_coors = new ArrayList<>();
    private ArrayList<String[]> point_name_info = new ArrayList<>();

    private Connection db_conn = null;
    private int publisher_id=-1;
    private String route_name,route_desc,route_country,route_city;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);
        menu_button = findViewById(R.id.menu_button);
        finish_route = findViewById(R.id.finish_route);
        Intent i = getIntent();
        publisher_id = i.getIntExtra("User_ID",0);
        menu_button.setOnClickListener(v -> {
            // Initializing the popup menu and giving the reference as current context
            PopupMenu popupMenu = new PopupMenu(AddRouteActivity.this, menu_button);

            // Inflating popup menu from map_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.map_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.save_route){
                    isSave = true;
                    menu_button.setVisibility(View.INVISIBLE);
                    finish_route.setVisibility(View.VISIBLE);
                    Log.i(TAG,"Starting to save route");
                    Toast.makeText(getApplicationContext(),"Click a point on the map!", Toast.LENGTH_LONG)
                            .show();
                }
                // Other items can be added to menu in future.
                return true;
            });
            // Showing the popup menu
            popupMenu.show();
        });

        finish_route.setOnClickListener(v -> {
            isSave = false;
            LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_route,null);
            EditText route_name_view = layout.findViewById(R.id.routeName);
            EditText route_desc_view = layout.findViewById(R.id.routeDesc);
            Button  done = layout.findViewById(R.id.route_done);
            final PopupWindow popupWindow = new PopupWindow(AddRouteActivity.this);
            popupWindow.setContentView(layout);
            popupWindow.setFocusable(true);
            popupWindow.showAtLocation(layout , Gravity.CENTER, 0, 0);
            done.setOnClickListener(v1 -> {
                this.route_name = route_name_view.getText().toString();
                this.route_desc = route_desc_view.getText().toString();
                try {
                    new PointUpload().execute().get();
                    this.point_count = 0;
                    this.point_coors.clear();
                    this.point_name_info.clear();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                popupWindow.dismiss();
            });

            Log.i(TAG,"Finished route");
            finish_route.setVisibility(View.INVISIBLE);
            menu_button.setVisibility(View.VISIBLE);
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        new Connect().execute();

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
    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        this.map = googleMap;
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
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
        this.map.setOnMapClickListener(this);
        getLocationPermission();
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
                            latitude = lastKnownLocation.getLatitude();
                            longitude =  lastKnownLocation.getLongitude();
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

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if(isSave){
            LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_point,null);
            EditText point_name = layout.findViewById(R.id.pointName);
            EditText point_desc = layout.findViewById(R.id.pointDesc);
            Button  done = layout.findViewById(R.id.point_done);
            final PopupWindow popupWindow = new PopupWindow(AddRouteActivity.this);
            popupWindow.setContentView(layout);
            popupWindow.setFocusable(true);
            popupWindow.showAtLocation(layout , Gravity.CENTER, 0, 0);
            done.setOnClickListener(v -> {
                this.map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(point_name.getText().toString())
                        .snippet(point_desc.getText().toString()));
                this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                point_coors.add(new Double[]{latitude,longitude});
                point_name_info.add(new String[]{point_name.getText().toString(),point_desc.getText().toString()});
                point_count++;
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);
                    if (addresses.size() > 0)
                    {
                        this.route_city = addresses.get(0).getAdminArea();
                        this.route_country = addresses.get(0).getCountryName();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"No addresses were found!");
                }
                popupWindow.dismiss();
            });
        }

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

    private class PointUpload extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            Statement statement,statement1;
            int publisher_status=-1,route_status=-1,point_status=-1;
            try {

                //Make User -> Publisher
                PreparedStatement check_publisher = db_conn.prepareStatement("SELECT publisher_id FROM [dbo].[Publisher] where publisher_id = ?");
                check_publisher.setObject(1,publisher_id);
                ResultSet check = check_publisher.executeQuery(); // in case publisher already exists
                if (!check.next()){
                    //ResultSet is empty
                    PreparedStatement publisher_st = db_conn.prepareStatement("INSERT INTO [dbo].[Publisher] values(?,?,?)");
                    publisher_st.setObject(1,publisher_id);
                    publisher_st.setObject(2,"");
                    publisher_st.setObject(3,0.0);
                    publisher_status = publisher_st.executeUpdate();
                }


                //Route Insertion
                statement = db_conn.createStatement();
                String route_query = "SELECT max(route_id) FROM [dbo].[Route]";
                ResultSet resultSet = statement.executeQuery(route_query);
                int route_id = 0;
                while(resultSet.next()){
                    route_id = resultSet.getInt(1);
                }
                route_id++;

                PreparedStatement insertSt=db_conn.prepareStatement("INSERT INTO [dbo].[Route] values(?,?,?,?,?,?,?,?)");
                insertSt.setObject(1,route_id);
                insertSt.setObject(2,publisher_id);
                insertSt.setObject(3,route_name);
                insertSt.setObject(4,route_desc);
                insertSt.setObject(5,0);    // initial rating
                insertSt.setObject(6,0);    // initial views
                insertSt.setObject(7,route_country);
                insertSt.setObject(8,route_city);
                route_status = insertSt.executeUpdate();

                //Point Insertions
                statement1 = db_conn.createStatement();
                String point_query = "SELECT max(point_id) FROM [dbo].[Point]";
                ResultSet resultSet1 = statement1.executeQuery(point_query);
                int point_id = 0;
                while(resultSet1.next()){
                    point_id = resultSet1.getInt(1);
                }
                point_id++;

                PreparedStatement pointInsert=db_conn.prepareStatement("INSERT INTO [dbo].[Point] values(?,?,?,?,?,?)");
                for (int i=0;i<point_count;i++){
                    pointInsert.setObject(1,point_id);
                    pointInsert.setObject(2,route_id);
                    pointInsert.setObject(3,point_name_info.get(i)[0]); // point name
                    pointInsert.setObject(4,point_name_info.get(i)[1]); // point desc
                    pointInsert.setObject(5,point_coors.get(i)[0]); // point latitude
                    pointInsert.setObject(6,point_coors.get(i)[1]); // point longitude
                    point_status = pointInsert.executeUpdate();
                    point_id++;
                }
                if(publisher_status>0 && point_status>0 && route_status>0)
                    Log.i(TAG,"Route added successfully");
                else
                    Log.e(TAG,"Route cannot be added.");

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
        }
    }
}
