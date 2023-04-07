package com.example.finalulidecap.ui.Main.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalulidecap.R;
import com.example.finalulidecap.databinding.FragmentGalleryBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class GalleryFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener {

    GoogleMap googleMap;
    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.routes_maps);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {

    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng mountainView = new LatLng(38.746784346527086, -9.196530753989746);
        LatLng lisbon  = new LatLng(24.368249921401237, -23.942334118087796);

        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(38.706917167588024, -9.147624179719928),
                        new LatLng(38.70224140668347, -9.17271152745779),
                        new LatLng(38.695425754834616, -9.222878073671835),
                        new LatLng(38.6998107493238, -9.249783816170048),
                        new LatLng(38.73201688686085, -9.244173545646076),
                        new LatLng(38.751584539277744, -9.259834736791248)));

        // Set listeners for click events.
        googleMap.setOnPolylineClickListener(this);
        googleMap.setOnPolygonClickListener(this);




        // Move the camera instantly to Sydney with a zoom of 15.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lisbon, 15));

        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mountainView )      // Sets the center of the map to Mountain View
                .zoom(12)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}