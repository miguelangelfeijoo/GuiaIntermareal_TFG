package tfg.uniovi.es.guiaintermareal.ui;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import tfg.uniovi.es.guiaintermareal.R;
import tfg.uniovi.es.guiaintermareal.provider.LocationProvider;

public class MapsActivity extends FragmentActivity implements LocationProvider.LocationCallback, OnMapReadyCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();
    private static final String GEO_REF_ROOT = "https://fir-cards-c5267.firebaseio.com";

    private GoogleMap mMap;
    private LocationProvider mLocationProvider;
    GeoFire geoFire;


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

        String ref = getIntent().getStringExtra("ref");
        String title = getIntent().getStringExtra("title");
        String GEO_FIRE_REF = GEO_REF_ROOT + "/" + ref + "/" + title;

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        //double currentLatitude = 67.123421;
        //double currentLongitude = -8.123451;

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        geoFire = new GeoFire(database.getReferenceFromUrl(GEO_FIRE_REF));
        geoFire.setLocation("locations", new GeoLocation(currentLatitude, currentLongitude), new GeoFire.CompletionListener(){

                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if(error!=null){
                            System.err.println("Error guardando la localizacion en GeoFire: " + error);
                        }else{
                            System.out.println("Localizacion guardada!!!");
                        }
                    }
        });



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