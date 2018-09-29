package com.example.android.dalishboard;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

//PersonLoader class runs in the background and calls JSONParser class to extract Person data from url
public class PersonLoader extends AsyncTaskLoader<List<Person>>{
    private String[] mURLs;

    //PersonLoader Constructor takes the context it was called from and the url to process
    public PersonLoader(Context context, String... urls){
        super(context);
        mURLs = urls;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Person> loadInBackground() {
        if (mURLs.length < 1 || mURLs[0] == null) {
            return null;
        }

        //call fetchPersonsData method from JSONParser class to extract person data
        return JSONParser.fetchPersonsData(mURLs[0], getContext());

    }
}
