package fi.tuni.joonas.weatherapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyWeatherService extends Service {

    private final String appId = "81be560b0713295bd67e9c8df0dd5e67";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("location-update"));

        Log.d("MyWeatherService", "started");
        return START_STICKY;
    }

    public void updateWeather(double lat, double lon){
        new ConnectionTask().execute("http://api.openweathermap.org/data/2.5/weather?units=metric&lat=" + lat +"&lon=" + lon + "&appId=" + appId);

    }

    private class ConnectionTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String response = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.connect();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));

                //poista kaikki tän jälkeen
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                response = content.toString();
                parseValuesFromResponse(response);
            } catch (Exception e){
                e.printStackTrace();
            }
            Log.d("MyWeatherService", response);
            return null;
        }

        protected void parseValuesFromResponse(String response){
            JsonElement jelement = new JsonParser().parse(response);

            JsonObject jobject = jelement.getAsJsonObject();

            JsonObject main = jobject.getAsJsonObject("main");

            JsonObject weather = jobject.getAsJsonArray("weather").get(0).getAsJsonObject();


            Intent intent = new Intent("weather-update");
            intent.putExtra("condition", weather.get("description").getAsString());
            intent.putExtra("temp", main.get("temp").getAsString());
            intent.putExtra("city", jobject.get("name").getAsString());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        }

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MyWeatherService" ,intent.getDoubleExtra("lat", 0.0) + " " + intent.getDoubleExtra("lon", 0.0));
            updateWeather(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("lon", 0.0));
        }
    };


}
