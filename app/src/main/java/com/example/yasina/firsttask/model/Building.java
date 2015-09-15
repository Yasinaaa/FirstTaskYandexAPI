package com.example.yasina.firsttask.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by yasina on 15.09.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Building implements Parcelable{

    @JsonProperty("Point")
    private Point p;

    public Building(){}

    public String getP() {
        return p.getPos();
    }

    public void setP(Point p) {
        this.p = p;
    }

    protected Building(Parcel in) {
        p = in.readParcelable(Point.class.getClassLoader());
    }

    public static final Creator<Building> CREATOR = new Creator<Building>() {
        @Override
        public Building createFromParcel(Parcel in) {
            return new Building(in);
        }

        @Override
        public Building[] newArray(int size) {
            return new Building[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(p, flags);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Point implements Parcelable{

        @JsonProperty("pos")
        String pos;

        public Point(Parcel in) {
            pos = in.readString();
        }

        public String getPos() {
            return pos;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }

        public static final Creator<Point> CREATOR = new Creator<Point>() {
            @Override
            public Point createFromParcel(Parcel in) {
                return new Point(in);
            }

            @Override
            public Point[] newArray(int size) {
                return new Point[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(pos);
        }
    }


}
