package fi.tuni.joonas.weatherapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MyLocationService extends Service {

    int MY_PERMISSIONS_REQUEST_LOCATION;




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("MyLocationService", "started");

        startListener();

        return START_STICKY;
    }



    public void startListener(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000, locationListener);
        } catch(SecurityException e){

        }


    }

    class MyLocationListener implements LocationListener{
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            debugLoc(location);

            Intent intent = new Intent("location-update");
            intent.putExtra("lat", location.getLatitude());
            intent.putExtra("lon", location.getLongitude());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}

        public void debugLoc(Location loc){
            if(loc != null)
                Log.d("konaa",loc.getLatitude() + " " + loc.getLongitude());
        }

    }

}
