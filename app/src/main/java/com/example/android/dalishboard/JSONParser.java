package com.example.android.dalishboard;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

//JSONParser class for making connection from url and extracting Person data
public class JSONParser {
    private static final String LOG_TAG = JSONParser.class.getSimpleName();

    //All methods are static hence object is unnecessary.
    private JSONParser(){
    }

    //Create URL object from URL string and make sure it's a valid URL
    private static URL createURL(String stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error creating URL bro", e);
        }
        return url;
    }

    // read String from URL inputStream
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //read entire JSON response from URL and store in a string
    private static String getJsonResponse(URL url) throws IOException{
        String jsonResponse = "";

        //If the URL is null, just return
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            //connect to url and make request
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //if request was successful, read input stream
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Looks like a bad response code fam: " + urlConnection.getResponseCode());
            }
        } catch (IOException e){
            Log.e(LOG_TAG, "IOException bro. Problem getting JSON response", e);
        } finally {
            //disconnect url and close inputStream when done
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if(inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //extract Person data from JSON response and create Person object with extracted data
    private static ArrayList<Person> extractPersons(String jsonResponse, Context context){
        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        //store all extracted persons
        ArrayList<Person> persons = new ArrayList<>();

        try {
            //root array
            JSONArray root = new JSONArray(jsonResponse);

            for(int i = 0; i < root.length(); i++){
                //get a person object
                JSONObject currentPerson = root.getJSONObject(i);

                //extract name from person object
                String name = context.getString(R.string.name) + currentPerson.getString("name");

                //extract iconUrl from person object
                String iconUrl = context.getString(R.string.url_prefix) + currentPerson.getString("iconUrl");

                //extract url from person object
                String url = currentPerson.getString("url");

                //extract message from person object
                String message = context.getString(R.string.message) + currentPerson.getString("message");

                //extract latitude and longitude from array from person object
                double latitude = currentPerson.getJSONArray("lat_long").getDouble(0);
                double longitude = currentPerson.getJSONArray("lat_long").getDouble(1);

                //extract termsOn from array from person object
                String termsOn = context.getString(R.string.terms_on) + currentPerson.getJSONArray("terms_on").getString(0);

                //extract project array from person object
                JSONArray projectArray = currentPerson.getJSONArray("project");

                // set project string depending on existence of project
                String project;
                if(projectArray.length() == 0 || projectArray.getString(0).equals("")){
                    project = context.getString(R.string.projects) + context.getString(R.string.none_for_now);
                }
                else{
                    project = context.getString(R.string.projects) + projectArray.getString(0);

                }

                //create Person object with extracted data
                Person person = new Person(name, iconUrl, url, message, latitude, longitude,
                        termsOn, project);

                //add person object to arraylist of Person
                persons.add(person);


            }


        }catch (JSONException e) {
            Log.e("JSONParser", "Problem parsing the JSON results", e);
        }

        return persons;

    }

    //Call all methods required to extract Person data
    public static ArrayList<Person> fetchPersonsData(String requestUrl, Context context){
        URL url = createURL(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = getJsonResponse(url);
        } catch (IOException e){
            Log.e(LOG_TAG, "Couldn't close input stream bro", e);
        }
        return extractPersons(jsonResponse, context);
    }



}
