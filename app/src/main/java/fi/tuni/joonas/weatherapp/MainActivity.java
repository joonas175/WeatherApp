package fi.tuni.joonas.weatherapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startWeatherService();
        startLocationService();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("weather-update"));

    }

    public void debugLoc(Location loc){
        if(loc != null)
            Log.d("konaa",loc.getLatitude() + " " + loc.getLongitude());
    }


    public void startWeatherService(){
        Intent intent = new Intent(this, MyWeatherService.class);
        startService(intent);
    }

    public void startLocationService(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            Intent intent = new Intent(this, MyLocationService.class);
            startService(intent);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, MyLocationService.class);
                    startService(intent);
                } else {
                }
                return;
            }

        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String city = intent.getStringExtra("city");
            String condition = intent.getStringExtra("condition");
            String temp = intent.getStringExtra("temp");
            Log.d("asd" ,city + " " + temp + " " + condition);

            updateTexts(city,condition,temp);
        }
    };

    public void updateTexts(String city, String condition, String temp){
        ((TextView) findViewById(R.id.city)).setText("City: " + city);
        ((TextView) findViewById(R.id.condition)).setText("Condition: " + condition);
        ((TextView) findViewById(R.id.temp)).setText("Temperature: " + temp);
    }
}
