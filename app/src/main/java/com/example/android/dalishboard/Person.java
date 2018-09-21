package com.example.android.dalishboard;

import android.location.Location;

public class Person {
    private String mName;
    private String mIconUrl;
    private String mUrl;
    private String mMessage;
    private double mLatitude, mLongitude;
    private String mTermsOn;
    private String mProject;

    public Person(String mName, String mIconUrl, String mUrl, String mMessage, double mLatitude,
                  double mLongitude, String mTermsOn, String mProject) {
        this.mName = mName;
        this.mIconUrl = mIconUrl;
        this.mUrl = mUrl;
        this.mMessage = mMessage;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mTermsOn = mTermsOn;
        this.mProject = mProject;
    }

    public String getmName() {
        return mName;
    }

    public String getmIconUrl() {
        return mIconUrl;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getmMessage() {
        return mMessage;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public String getmTermsOn() {
        return mTermsOn;
    }

    public String getmProject() {
        return mProject;
    }

    @Override
    public String toString() {
        return "Person{" +
                "mName='" + mName + '\'' +
                ", mIconUrl='" + mIconUrl + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", mMessage='" + mMessage + '\'' +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                ", mTermsOn='" + mTermsOn + '\'' +
                ", mProject='" + mProject + '\'' +
                '}';
    }
}
