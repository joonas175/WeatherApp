package fi.tuni.joonas.weatherapp;

import android.os.Parcel;
import android.os.Parcelable;



import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class that represents one forecast at a given time. Used mainly to populate RecyclerView.
 * Extends parcelable, so it can be passed inside bundles.
 *
 * @author Joonas Salojärvi
 * @version 2019.04.22
 * @since 2019.04.22
 */
public class Forecast implements Parcelable {

    /**
     * Time representation in string format
     */
    String time;

    /**
     * Description of weather
     */
    String desc;

    /**
     * Icon id for weather
     */
    int icon;

    /**
     * Temperature
     */
    int temp;

    /**
     * Wind speed
     */
    float wind;


    /**
     * Default constructor to be used
     * @param time Time of forecast
     * @param desc Description of weather condition
     * @param icon Icon id
     * @param temp Temperature
     * @param wind Wind speed
     */
    public Forecast(String time, String desc, int icon, int temp, float wind) {
        this.time = parseDate(time);
        this.desc = desc;
        this.icon = icon;
        this.temp = temp;
        this.wind = wind;
    }

    /**
     * Parses date and returns it in a different formatting
     * @param time time in the format that OpenWeatherMap returns it
     * @return String representation of time
     */
    private String parseDate(String time) {
        try{
            Date date=new SimpleDateFormat("yy-MM-dd HH:mm:ss").parse(time);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("E, dd MMM HH:mm");

            return dateFormatter.format(date);
        } catch (Exception e){
            return time;
        }

    }

    /**
     * Reads given parcel. Used to recover this object from bundle extras.
     * @param in Parcel
     */
    protected Forecast(Parcel in) {
        time = in.readString();
        desc = in.readString();
        icon = in.readInt();
        temp = in.readInt();
        wind = in.readFloat();
    }

    /**
     * Used to write this object into a parcel, that can be send inside an Intent.
     * @param dest Parcel
     * @param flags int
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(desc);
        dest.writeInt(icon);
        dest.writeInt(temp);
        dest.writeFloat(wind);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Forecast> CREATOR = new Creator<Forecast>() {
        @Override
        public Forecast createFromParcel(Parcel in) {
            return new Forecast(in);
        }

        @Override
        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public float getWind() {
        return wind;
    }

    public void setWind(float wind) {
        this.wind = wind;
    }
}
