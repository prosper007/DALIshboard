package com.example.android.dalishboard;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        if (intent == null) {
            return;
        }
        String errorMessage = "";

        // Get the location passed to this service through an extra.
        ArrayList<Person> people = intent.getParcelableArrayListExtra(
                Constants.LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        Address address;
        List<Address> addresses = null;
        StringBuilder addressAsString = new StringBuilder();
        Location location = new Location("");
        for(Person person : people) {
            location.setLatitude(person.getmLatitude());
            location.setLongitude(person.getmLongitude());
            try {
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(), 1);
            } catch (IOException ioException) {
                errorMessage = "Service not available";
                //Log.e(TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                errorMessage = "invalid latitude and longitude used";
                Log.e(TAG, errorMessage + ". " + "Name: " + person.getmName() +
                        "Latitude = " + location.getLatitude() +
                        ", Longitude = " +
                        location.getLongitude(), illegalArgumentException);
            }

            if (addresses == null || addresses.size() == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = person.getmName() + "No address found";
                    Log.e(TAG, errorMessage);
                }
                addressAsString.append(getString(R.string.address)).append(getString(R.string.unavailable));
                person.setmAddress(addressAsString.toString());


            }else{
                address = addresses.get(0);
                addressAsString.append(getString(R.string.address));
                String locality, adminArea, countryName;
                if(address.getLocality() != null){
                    locality = address.getLocality() + ", ";
                }
                else{
                    locality = "";
                }
                if(address.getAdminArea() != null){
                    adminArea = address.getAdminArea() + ", ";
                }
                else{
                    adminArea = "";
                }
                if(address.getCountryName() != null){
                    countryName = address.getCountryName();
                }
                else{
                    countryName = "";
                }

                addressAsString.append(locality).append(adminArea).append(countryName);
                person.setmAddress(addressAsString.toString());
            }
            addressAsString.delete(0, addressAsString.length());
        }
        deliverResultToReceiver(Constants.SUCCESS_RESULT, people);

    }

    private void deliverResultToReceiver(int resultCode, ArrayList<Person> people) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.RESULT_DATA_KEY, people);
        mReceiver.send(resultCode, bundle);

    }


}
