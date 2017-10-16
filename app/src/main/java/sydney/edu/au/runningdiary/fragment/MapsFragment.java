package sydney.edu.au.runningdiary.fragment;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Iterator;

import sydney.edu.au.runningdiary.R;
import sydney.edu.au.runningdiary.activity.MainActivity;

/**
 * Created by yang on 10/14/17.
 */

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private static GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
    }

    public static void setupMap() {
        if (MainActivity.current_position != null) {
            mMap.addMarker(new MarkerOptions().position(MainActivity.current_position).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker_copy)).title("My Current Position"));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(MainActivity.current_position, 13);
            mMap.animateCamera(cameraUpdate);
        }
    }

    public static void updateMap() {
        if (MainActivity.trackPoints != null) {
            int numOfPoints = MainActivity.trackPoints.size();

            if (numOfPoints > 1) {
                Iterator<LatLng> iterator = MainActivity.trackPoints.iterator();
                LatLng start_point = iterator.next();
                LatLng end_point = null;

                while (iterator.hasNext()) {
                    end_point = iterator.next();
                    mMap.addPolyline((new PolylineOptions()).add(start_point, end_point)
                            .width(15).color(Color.CYAN).visible(true));
                    start_point = end_point;
                }
            }
        }
    }

    public static void resetMap() {
        mMap.clear();
    }
}
