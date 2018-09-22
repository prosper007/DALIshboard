package com.example.android.dalishboard;

import android.graphics.Bitmap;
import android.location.Address;

public class Person {
    private String mName;
    private Bitmap mIcon;
    private String mUrl;
    private String mMessage;
    private String mAddress;
    private String mTermsOn;
    private String mProject;

    public Person(String mName, Bitmap mIcon, String mUrl, String mMessage, String mAddress,
                  String mTermsOn, String mProject) {
        this.mName = mName;
        this.mIcon = mIcon;
        this.mUrl = mUrl;
        this.mMessage = mMessage;
        this.mAddress = mAddress;
        this.mTermsOn = mTermsOn;
        this.mProject = mProject;
    }

    public String getmName() {
        return mName;
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

    public String getmAddress() {
        return mAddress;
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
                ", mIcon='" + mIcon + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", mMessage='" + mMessage + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mTermsOn='" + mTermsOn + '\'' +
                ", mProject='" + mProject + '\'' +
                '}';
    }
}
