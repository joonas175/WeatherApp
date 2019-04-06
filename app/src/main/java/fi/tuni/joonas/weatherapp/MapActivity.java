package fi.tuni.joonas.weatherapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyDFo5X0ashZRIy8jVK0kmQ6u8UsvMB8zPo");

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng current = new LatLng(61.4982, 23.761);
        try {
            Location location = ((LocationManager) this.getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);
            current = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (SecurityException e){
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
        }


        googleMap.addMarker(new MarkerOptions().position(current)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(current));
    }
}
