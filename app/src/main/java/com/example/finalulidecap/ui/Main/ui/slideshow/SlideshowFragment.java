package com.example.finalulidecap.ui.Main.ui.slideshow;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalulidecap.R;
import com.example.finalulidecap.databinding.FragmentHomeBinding;
import com.example.finalulidecap.databinding.FragmentSlideshowBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class SlideshowFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener {

    private FragmentSlideshowBinding binding;

    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;

    Button start, pause;


    protected GoogleMap mMap;

    FusedLocationProviderClient fusedLocationProviderClient;

    Handler handler = new Handler();
    int delay = 3000; //milliseconds

    List<LatLng> route = new ArrayList<>();

    LocationManager locationManager;
    LocationListener locationListener;

    private boolean getLocation = true;

    TextView speedTextView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        chronometer = root.findViewById(R.id.chronometer);
        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        start = root.findViewById(R.id.start);
        pause = root.findViewById(R.id.pause);
        speedTextView = root.findViewById(R.id.speedTextView);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView2);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChronometer(v);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseChronometer(v);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(Objects.requireNonNull(getActivity()), location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                Log.e("Current Location", currentLocation.toString());
                Toast.makeText(getActivity(), currentLocation.toString(), Toast.LENGTH_SHORT).show();
                route.add(currentLocation);
                LatLng[] routeArray = route.toArray(new LatLng[route.size()]);
                Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .add(routeArray));



                // Set listeners for click events.
                mMap.setOnPolylineClickListener(this);
                mMap.setOnPolygonClickListener(this);
                // move camera to the last location in the route
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.get(route.size() - 1), 20));

            }
        });
    }

    public void startChronometer(View view) {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }
    public void pauseChronometer(View view) {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }


    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {

    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        mMap.setOnPolylineClickListener(this);
        mMap.setOnPolygonClickListener(this);

        handler.postDelayed(new Runnable(){
            public void run(){

                locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (getLocation){
                            mMap.clear();
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.e("Current Location", userLocation.toString());
                            route.add(userLocation);
                            LatLng[] routeArray = route.toArray(new LatLng[route.size()]);
                            Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                                    .clickable(true)
                                    .add(routeArray));


                            // move camera to the last location in the route
                            if (getLocation){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.get(route.size() - 1), 20));
                                getLocation = false;
                            }

                        }


                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };

                if (Build.VERSION.SDK_INT < 23) {
                    if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                } else {
                    if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    } else {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        mMap.clear();
                        LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        Log.e("Current Location", currentLocation.toString());
                        route.add(currentLocation);
                        LatLng[] routeArray = route.toArray(new LatLng[route.size()]);
                        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                                .clickable(true)
                                .add(routeArray));

                        LocationListener locationListener = new LocationListener() {
                            public void onLocationChanged(Location location) {
                                float speed = location.getSpeed();
                                float speedKmH = speed * 3.6f;
                                // make the speedKmH with two decimal places
                                speedKmH = Math.round(speedKmH * 100.0f) / 100.0f;
                                speedTextView.setText("Speed: " + speedKmH + "Km/h");
//                                Toast.makeText(getActivity(), "Speed: "+ speedKmH + "Km/h", Toast.LENGTH_SHORT).show();
                            }
                            public void onStatusChanged(String provider, int status, Bundle extras) {}
                            public void onProviderEnabled(String provider) {}
                            public void onProviderDisabled(String provider) {}
                        };

                        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 0, locationListener);


                        // move camera to the last location in the route
                        if (getLocation){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.get(route.size() - 1), 20));
                            getLocation = false;
                        }
                    }
                }

                handler.postDelayed(this, delay);
            }
        }, delay);
    }
}