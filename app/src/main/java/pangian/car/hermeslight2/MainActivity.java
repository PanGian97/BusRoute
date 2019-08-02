package pangian.car.hermeslight2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final float STROKE_WIDTH = 12f;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    GoogleMap map;
    Bus bus;
    ArrayList<Point> points = new ArrayList<>();
    LatLng userMarkerLocation;
    Location currentLocation;
    BitmapFactory bitmapFactory;
    private GeoApiContext geoApiContext;
    private boolean locationPermission = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Button hideShowBtn;
    MapViewModel mapViewModel;
    boolean widgetState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        bitmapFactory = new BitmapFactory();
        hideShowBtn = (Button)findViewById(R.id.hide_show_btn);
    }

    @Override
    protected void onStart() {
        //??  A()->B() = inside
        //??  ->A()
        //    ->B() after
        //?? A()( B()->C() ) inside A and C after B


        //->initBus()
        //->initPoints()
        //->userPermissions()
        //->if userPermissions==true( ->initMap() ->onMapReady() ->getDeviceLocation() ->OnComplete()->( showBus() ->showPoints() -> addPolylinesOnMap() ) )

        super.onStart();
        initBus();
        initPoints();
        userPermissions();
        if (isLocationPermission()) {
            initMap(this);
        }



    }

    private void initButton() {
        hideShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapViewModel.showHideWidgets();
            }
        });
    }

    private void initBus() {
        bus = new Bus(38.018141, 23.809288);
    }

    private void initPoints() {
        points.add(new Point(1, 38.018987, 23.807273, true, "Doukisis Plakentias"));
        points.add(new Point(2, 38.018141, 23.809288, false, "empty"));
        points.add(new Point(3, 38.016502, 23.81315, true, "Metro Agia Paraskevh"));
        points.add(new Point(4, 38.015497, 23.815733, false, "empty"));
        points.add(new Point(5, 38.0076716, 23.8185177, true, "Agia Paraskevh Terma"));
    }

    public void userPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                setLocationPermission(true);
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    public void initMap(OnMapReadyCallback onMapReadyCallback) {

        Log.d(TAG, "initMap: Initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) ((AppCompatActivity) this).getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(onMapReadyCallback);//preparing map

        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()// use it to calculate directions
                    .apiKey(this.getString(R.string.api_key_string))
                    .build();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();


        getDeviceLocation();

        //DEFAULT if statement that need for setMyLocationEnabled
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);//location spot on map
        map.getUiSettings().setMyLocationButtonEnabled(false);//disable the by default location button
        map.getUiSettings().setCompassEnabled(true);


    }

    public void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting current location ");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (locationPermission) {
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Found current location!");
                            currentLocation = (Location) task.getResult();

                            userMarkerLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                            requestVisibilityState();
                            initButton();

                        } else {
                            Log.d(TAG, "onComplete: Current location is null");
                            Toast.makeText(MainActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
        }

    }

    private void requestVisibilityState() {
    mapViewModel.widgetVisibility().observe(this, new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
            map.clear();
            widgetState = aBoolean;
            showBus(aBoolean);
            showPoints(aBoolean);
            addPolylinesToMap(aBoolean);
        }
    });
    }

    private void showPoints(boolean state) {
        for (Point point : points) {
//

            LatLng markerLocation = new LatLng(point.getLat(), point.getLon());
            Bitmap busStopMarkerIcon = bitmapFactory.getBitmap(this, R.drawable.ic_bus_stop);
            if (point.isStation) {
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(markerLocation)
                        .icon(BitmapDescriptorFactory.fromBitmap(busStopMarkerIcon))
                        .title(point.getStationAddress())
                        .snippet(String.valueOf(point.getId()))
                         .visible(state)

                );

            }
        }

    }

    private void showBus(boolean state) {

        LatLng markerLocation = new LatLng(bus.getLat(), bus.getLon());


        Marker marker = map.addMarker(new MarkerOptions()
                .position(markerLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
                .visible(state)
        );
    }


    private void addPolylinesToMap(boolean state) {
        PolylineOptions route = new PolylineOptions();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Point point : points) {
            LatLng coordinates = new LatLng(point.getLat(), point.getLon());
            route.width(STROKE_WIDTH);
            // route.color(R.color.colorAccent);
            route.geodesic(true);
            route.add(coordinates);
            builder.include(coordinates);
            route.visible(state);

        }
        map.addPolyline(route);
        LatLngBounds bounds = builder.build();
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
    }

    public boolean isLocationPermission() {
        return locationPermission;
    }

    public void setLocationPermission(boolean locationPermission) {
        this.locationPermission = locationPermission;
    }
}
