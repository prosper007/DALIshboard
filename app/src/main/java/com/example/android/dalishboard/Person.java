package com.example.android.dalishboard;

import android.graphics.Bitmap;
import android.location.Address;

public class Person {
    private String mName;
    private String mIconUrl;
    private Bitmap mIcon;
    private String mUrl;
    private String mMessage;
    private double mLatitude, mLongitude;
    private String mAddress;
    private String mTermsOn;
    private String mProject;

    public Person(String mName, String mIconUrl, String mUrl, String mMessage,
                  double mLatitude, double mLongitude, String mTermsOn, String mProject) {
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

    public Bitmap getmIcon() {
        return mIcon;
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

    public String getmAddress() {
        return mAddress;
    }

    public String getmTermsOn() {
        return mTermsOn;
    }

    public String getmProject() {
        return mProject;
    }

    public void setmIcon(Bitmap mIcon) {
        this.mIcon = mIcon;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    @Override
    public String toString() {
        return "Person{" +
                "mName='" + mName + '\'' +
                ", mIcon='" + mIcon + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", mMessage='" + mMessage + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mTermsOn='" + mTermsOn + '\'' +
                ", mProject='" + mProject + '\'' +
                '}';
    }
}
