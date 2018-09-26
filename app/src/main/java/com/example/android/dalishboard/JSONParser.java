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

public class JSONParser {
    private static final String LOG_TAG = JSONParser.class.getSimpleName();
    Context context;

    private JSONParser(){
    }

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

    private static URL createURL(String stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error creating URL bro", e);
        }
        return url;
    }

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

    private static String getJsonResponse(URL url) throws IOException{
        String jsonResponse = "";

        //If the URL is null, just return
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
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
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if(inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static ArrayList<Person> extractPersons(String jsonResponse, Context context){
        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        ArrayList<Person> persons = new ArrayList<>();

        try {
            JSONArray root = new JSONArray(jsonResponse);
            for(int i = 0; i < root.length(); i++){
                JSONObject currentPerson = root.getJSONObject(i);
                String name = context.getString(R.string.name) + currentPerson.getString("name");
                String iconUrl = context.getString(R.string.url_prefix) + currentPerson.getString("iconUrl");
                String url = currentPerson.getString("url");
                String message = context.getString(R.string.message) + currentPerson.getString("message");
                double latitude = currentPerson.getJSONArray("lat_long").getDouble(0);
                double longitude = currentPerson.getJSONArray("lat_long").getDouble(1);
                String termsOn = context.getString(R.string.terms_on) + currentPerson.getJSONArray("terms_on").getString(0);
                JSONArray projectArray = currentPerson.getJSONArray("project");
                String project;
                if(projectArray.length() == 0 || projectArray.getString(0).equals("")){
                    project = context.getString(R.string.projects) + context.getString(R.string.none_for_now);
                }
                else{
                    project = context.getString(R.string.projects) + projectArray.getString(0);

                }

                Person person = new Person(name, iconUrl, url, message, latitude, longitude,
                        termsOn, project);
                persons.add(person);


            }


        }catch (JSONException e) {
            Log.e("JSONParser", "Problem parsing the JSON results", e);
        }

        return persons;

    }



}
