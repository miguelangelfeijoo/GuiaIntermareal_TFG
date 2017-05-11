package tfg.uniovi.es.guiaintermareal.ui;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import tfg.uniovi.es.guiaintermareal.R;
import tfg.uniovi.es.guiaintermareal.provider.LocationProvider;

public class MapsActivity extends FragmentActivity implements LocationProvider.LocationCallback, OnMapReadyCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;
    private LocationProvider mLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        mLocationProvider = new LocationProvider(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //Se crean dos marcadores, uno fijo y otro de la posicion actual
        mMap.addMarker(new MarkerOptions().position(new LatLng(43.541464, -5.650547)).title("Playa San Lorenzo"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Posicion actual"));

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Estoy aqui!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        setUpMap();
    }


    public void setUpMap(){
        try {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.setMyLocationEnabled(true);
            mMap.setTrafficEnabled(true);
            mMap.setIndoorEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }catch(SecurityException e){
            Log.d("setUpMat", "Error al configurar el mapa");
        }
    }

}