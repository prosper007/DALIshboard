package com.example.android.dalishboard;


import android.os.Parcel;
import android.os.Parcelable;

// Person class to hold data for each person. It implements Parcelable so it could be passed from
// MainActivity UI thread to fetchAddressIntentService background thread
public class Person implements Parcelable{

    private String mName;
    private String mIconUrl;
    private String mUrl;
    private String mMessage;
    private double mLatitude, mLongitude;
    private String mAddress;
    private String mTermsOn;
    private String mProject;

    //Constructor takes in data extracted from JSON response and sets appropriate fields
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

    //getter methods
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

    public String getmAddress() {
        return mAddress;
    }

    public String getmTermsOn() {
        return mTermsOn;
    }

    public String getmProject() {
        return mProject;
    }

    // Only address needs to be set as it is calculated from coordinates by
    // fetchAddressIntentService class
    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    @Override
    public String toString() {
        return "Person{" +
                "mName='" + mName + '\'' +
                ", mMessage='" + mMessage + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mTermsOn='" + mTermsOn + '\'' +
                ", mProject='" + mProject + '\'' +
                '}';
    }

    //Methods controlling Parcelable behaviour
    public static final Parcelable.Creator<Person> CREATOR
            = new Parcelable.Creator() {
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    //Constructor for creating a Person from Parcelable object
    public Person(Parcel in){
        this.mName = in.readString();
        this.mIconUrl = in.readString();
        this.mUrl = in.readString();
        this.mMessage = in.readString();
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
        this.mTermsOn = in.readString();
        this.mProject = in.readString();
        this.mAddress = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Method for creating a Parcelable Person
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mName);
        parcel.writeString(this.mIconUrl);
        parcel.writeString(this.mUrl);
        parcel.writeString(this.mMessage);
        parcel.writeDouble(this.mLatitude);
        parcel.writeDouble(this.mLongitude);
        parcel.writeString(this.mTermsOn);
        parcel.writeString(this.mProject);
        parcel.writeString(this.mAddress);
    }
}
