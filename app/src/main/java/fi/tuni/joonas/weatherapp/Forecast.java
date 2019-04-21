package fi.tuni.joonas.weatherapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Forecast implements Parcelable {
    String time;
    String desc;
    int icon;
    int temp;
    float wind;


    public Forecast(String time, String desc, int icon, int temp, float wind) {
        this.time = parseDate(time);
        this.desc = desc;
        this.icon = icon;
        this.temp = temp;
        this.wind = wind;
    }

    private String parseDate(String time) {
        try{
            Date date=new SimpleDateFormat("yy-MM-dd HH:mm:ss").parse(time);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("E, dd MMM HH:mm");

            return dateFormatter.format(date);
        } catch (Exception e){
            return time;
        }

    }


    protected Forecast(Parcel in) {
        time = in.readString();
        desc = in.readString();
        icon = in.readInt();
        temp = in.readInt();
        wind = in.readFloat();
    }

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
}
