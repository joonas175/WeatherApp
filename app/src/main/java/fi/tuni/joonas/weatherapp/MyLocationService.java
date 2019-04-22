package fi.tuni.joonas.weatherapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Location service to handle location updates.
 *
 * @author Joonas Salojärvi
 * @version 2019.04.22
 * @since 2019.04.22
 */
public class MyLocationService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

    }

    /**
     * Method that's called when launching the service. Calls method to start a listener for
     * location updates.
     * @param intent intent
     * @param flags flags
     * @param startId startId
     * @return Service param (STICKY)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("MyLocationService", "started");

        startListener();

        return START_STICKY;
    }


    /**
     * Starts location listener. Sends new location update broadcast every 1000 meters.
     */
    public void startListener(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000, locationListener);
        } catch(SecurityException e){

        }


    }

    /**
     * Location listener that handles location updates.
     *
     * @author Joonas Salojärvi
     * @version 2019.04.22
     * @since 2019.04.22
     */
    class MyLocationListener implements LocationListener{

        /**
         * Called when location has changed. Sends local broadcast to inform other services of
         * location update.
         * @param location Current location
         */
        public void onLocationChanged(Location location) {

            //debugLoc(location);

            Intent intent = new Intent("location-update");
            intent.putExtra("lat", location.getLatitude());
            intent.putExtra("lon", location.getLongitude());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}

        /**
         * For debugging purposes. Prints location to log.
         * @param loc Location to print
         */
        public void debugLoc(Location loc){
            if(loc != null)
                Log.d("LocationService",loc.getLatitude() + " " + loc.getLongitude());
        }

    }

}
