package com.example.android.dalishboard;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class PersonLoader extends AsyncTaskLoader<List<Person>>{
    private String[] mURLs;

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

        return JSONParser.fetchPersonsData(mURLs[0], getContext());

    }
}
