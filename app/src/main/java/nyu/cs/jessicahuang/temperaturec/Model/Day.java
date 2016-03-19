/**
 * Day.java
 *
 * Created by Jessica Huang.
 */

package nyu.cs.jessicahuang.temperaturec.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Day implements Parcelable {

    String dayOfTheWeek;
    float temperature;

    public static final Parcelable.Creator<Day> CREATOR = new Parcelable.Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

    protected Day(Parcel in) {
        dayOfTheWeek = in.readString();
        temperature = in.readFloat();
    }

    public Day (String dayOfTheWeek, float temperature) {
        this.dayOfTheWeek = dayOfTheWeek;
        this.temperature = temperature;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getDayOfTheWeek() {
        return dayOfTheWeek;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dayOfTheWeek);
        dest.writeFloat(temperature);
    }
}
