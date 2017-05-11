package tfg.uniovi.es.guiaintermareal.ui;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import tfg.uniovi.es.guiaintermareal.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Gijon, España
        // and move the map's camera to the same location.
        LatLng m = new LatLng(43.541464, -5.650547);
        googleMap.addMarker(new MarkerOptions().position(m)
                .title("Playa de San Lorenzo"));
        LatLng m2 = new LatLng(43.543036, -5.670914);
        googleMap.addMarker(new MarkerOptions().position(m2)
                .title("Playa de Poniente"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(m));
    }
}