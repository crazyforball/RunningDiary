package sydney.edu.au.runningdiary.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Iterator;

import sydney.edu.au.runningdiary.R;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        int numOfPoints = RunActivity.mapPoints.size();

        if (numOfPoints > 0) {
            LatLng current = RunActivity.mapPoints.get(numOfPoints -1);
//            mMap.addMarker(new MarkerOptions().position(current).title("My Current Position"));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(current, 13);
            mMap.animateCamera(cameraUpdate);
//            mMap.moveCamera(cameraUpdate);
        }

        if (numOfPoints > 1) {
            Iterator<LatLng> iterator = RunActivity.mapPoints.iterator();
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
