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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main activity for this application. This application doesn't contain any other activities
 * inside it. Shows weather information for selected location. Launches Google's city search.
 *
 * @author Joonas Salojärvi
 * @version 2019.04.22
 * @since 2019.04.22
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Request code to request location permission.
     */
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    /**
     * Request code for Google's autocomplete location search.
     */
    public final int AUTOCOMPLETE_REQUEST_CODE = 2;

    /**
     * List of all forecasts.
     */
    ArrayList<Forecast> forecasts;

    /**
     * Forecast RecyclerView. Shows all forecasts in a scrollable list.
     */
    RecyclerView rvForecasts;

    /**
     * Handles setting up all the elements when creating the activity.
     *
     * @param savedInstanceState Instances saved in app bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startWeatherService();
        startLocationService();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("weather-update"));

        LocalBroadcastManager.getInstance(this).registerReceiver(forecastReceiver,
                new IntentFilter("forecast-update"));

        Places.initialize(getApplicationContext(), getString(R.string.apikey));

        PlacesClient placesClient = Places.createClient(this);

        forecasts = new ArrayList<>();

        rvForecasts = findViewById(R.id.forecasts);
        ForecastAdapter adapter = new ForecastAdapter(forecasts);
        rvForecasts.setAdapter(adapter);
        rvForecasts.setLayoutManager(new LinearLayoutManager(this));

    }

    /**
     * Method to start the weather service.
     * @see MyWeatherService
     */
    public void startWeatherService(){
        Intent intent = new Intent(this, MyWeatherService.class);
        startService(intent);
    }

    /**
     * Method to start the location service. Also requests location permission.
     */
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

    /**
     * This method handles starting the location service, if the service wasn't started in
     * startLocationService method()
     *
     * @param requestCode Request code for a single permission.
     * @param permissions Permissions granted for this app
     * @param grantResults All granted permissions
     */
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

    /**
     * Receiver for current weather updates from MyWeatherService. Handles the received bundle and
     * passes it's information on to change the texts.
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String city = intent.getStringExtra("city");
            String condition = intent.getStringExtra("condition");
            String temp = intent.getStringExtra("temp");
            Long wind = intent.getLongExtra("wind", 0);
            int image = intent.getIntExtra("icon", 1);

            updateTexts(city,condition,temp, wind, image);
        }
    };

    /**
     * Handles changing the current weather's texts in this activity.
     * @param city Location
     * @param condition Description of weather condition
     * @param temp Current temperature
     * @param wind Wind speed
     * @param icon Icon to be used
     */
    public void updateTexts(String city, String condition, String temp, Long wind, int icon){
        ((TextView) findViewById(R.id.city)).setText(city);
        ((TextView) findViewById(R.id.description)).setText(condition);
        ((TextView) findViewById(R.id.temp)).setText(temp + " °C");
        ((TextView) findViewById(R.id.wind)).setText(wind + " m/s");
        ((ImageView) findViewById(R.id.condition)).setImageResource(icon);
    }

    /**
     * Builds and starts Google's autocomplete search for cities.
     * @param v View (button)
     */
    public void startAutocompleteSearch(View v){


        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setTypeFilter(TypeFilter.CITIES)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    /**
     * Handles result from Google's autocomplete search. Sends city's latlong values as a local
     * broadcast.
     *
     * @param requestCode Request code
     * @param resultCode Result code
     * @param data Intent data from result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                Intent intent = new Intent("location-update");
                intent.putExtra("lat", place.getLatLng().latitude);
                intent.putExtra("lon", place.getLatLng().longitude);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /**
     * Local broadcast receiver for forecasts.
     */
    private BroadcastReceiver forecastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Forecast> forecasts = intent.getParcelableArrayListExtra("forecasts");

            setForecasts(forecasts);

        }
    };

    /**
     * Sets forecasts and notifies RecyclerViews adapter that the data has changed.
     * @param forecasts All forecasts to be displayed
     */
    public void setForecasts(List<Forecast> forecasts){
        this.forecasts.clear();
        this.forecasts.addAll(forecasts);
        rvForecasts.getAdapter().notifyDataSetChanged();
    }
}
