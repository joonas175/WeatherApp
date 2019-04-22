package fi.tuni.joonas.weatherapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.JsonArray;
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
import java.util.ArrayList;
import java.util.List;

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
        new ConnectionTask().execute("http://api.openweathermap.org/data/2.5/weather?units=metric&lat=" + lat +"&lon=" + lon + "&appId=" + appId, "current");
        new ConnectionTask().execute("http://api.openweathermap.org/data/2.5/forecast?units=metric&lat=" + lat +"&lon=" + lon + "&appId=" + appId + "&cnt=24", "forecast");

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
                parseValuesFromResponse(response, strings[1]);
            } catch (Exception e){
                e.printStackTrace();
            }
            Log.d("MyWeatherService", response);
            return null;
        }

        protected void parseValuesFromResponse(String response, String param){
            if(param.equalsIgnoreCase("current")){
                JsonElement jelement = new JsonParser().parse(response);

                JsonObject jobject = jelement.getAsJsonObject();

                JsonObject main = jobject.getAsJsonObject("main");

                JsonObject weather = jobject.getAsJsonArray("weather").get(0).getAsJsonObject();


                Intent intent = new Intent("weather-update");
                intent.putExtra("condition", weather.get("description").getAsString());
                intent.putExtra("temp", main.get("temp").getAsString());
                intent.putExtra("city", jobject.get("name").getAsString());
                intent.putExtra("wind", jobject.get("wind").getAsJsonObject().get("speed").getAsLong());
                intent.putExtra("icon", getIconInt(weather.get("icon").getAsString()));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            } else if (param.equalsIgnoreCase("forecast")){
                JsonElement jelement = new JsonParser().parse(response);

                JsonObject jobject = jelement.getAsJsonObject();

                JsonArray jarray = jobject.getAsJsonArray("list");
                List<Forecast> forecasts = new ArrayList<Forecast>();
                for(JsonElement forecastJson : jarray){
                    JsonObject forecastObject = forecastJson.getAsJsonObject();
                    String time = forecastObject.get("dt_txt").getAsString();

                    JsonObject weather = forecastObject.get("weather").getAsJsonArray().get(0).getAsJsonObject();
                    String desc = weather.get("description").getAsString();
                    int icon = getIconInt(weather.get("icon").getAsString());

                    int temp = Math.round(forecastObject.get("main").getAsJsonObject().get("temp").getAsFloat());
                    float wind = forecastObject.get("wind").getAsJsonObject().get("speed").getAsFloat();

                    forecasts.add(new Forecast(time, desc, icon, temp, wind));

                }
                Intent intent = new Intent("forecast-update");
                intent.putParcelableArrayListExtra("forecasts", (ArrayList<? extends Parcelable>) forecasts);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            }


        }

    }

    private int getIconInt(String icon) {
        switch (icon){
            case "01d": return R.drawable.w01d;
            case "01n": return R.drawable.w01n;
            case "02d": return R.drawable.w02d;
            case "02n": return R.drawable.w02n;
            case "03d": return R.drawable.w03d;
            case "03n": return R.drawable.w03n;
            case "04d": return R.drawable.w04d;
            case "04n": return R.drawable.w04n;
            case "09d": return R.drawable.w09d;
            case "09n": return R.drawable.w09n;
            case "10d": return R.drawable.w10d;
            case "10n": return R.drawable.w10n;
            case "11d": return R.drawable.w11d;
            case "11n": return R.drawable.w11n;
            case "13d": return R.drawable.w13d;
            case "13n": return R.drawable.w13n;
            case "50d": return R.drawable.w50d;
            case "50n": return R.drawable.w50n;

        }

        return R.drawable.w01d;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MyWeatherService" ,intent.getDoubleExtra("lat", 0.0) + " " + intent.getDoubleExtra("lon", 0.0));
            updateWeather(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("lon", 0.0));
        }
    };


}
