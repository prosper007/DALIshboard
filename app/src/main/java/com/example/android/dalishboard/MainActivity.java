package com.example.android.dalishboard;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Person>> {

private RecyclerView recyclerView;
private PersonAdapter personAdapter;
private ArrayList<Person> personArrayList;
private static final int MAIN_LOADER_ID = 0;
private static final String DALI_URL = "http://mappy.dali.dartmouth.edu/members.json";
private static final String LOG_TAG = MainActivity.class.getName();
private AddressResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        personArrayList = new ArrayList<>();
        mResultReceiver = new AddressResultReceiver(new Handler());

       /*
       ArrayList<Person> test = new ArrayList<>();
        Person testy = new Person("Obi", "http://mappy.dali.dartmouth.edu/images/ricky.jpg",
                "taboada.me", "Let's build something.", 23.113592, -82.366596,
                "17S", "Dali");
        test.add(testy);
        */
        //personAdapter = new PersonAdapter(this, personArrayList);
        //recyclerView.setAdapter(personAdapter);

        getLoaderManager().initLoader(MAIN_LOADER_ID, null, this);
    }

    protected void startIntentService(List<Person> people) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putParcelableArrayListExtra(Constants.LOCATION_DATA_EXTRA, (ArrayList<Person>)people);
        startService(intent);
    }

    @Override
    public Loader<List<Person>> onCreateLoader(int i, Bundle bundle) {
        return new PersonLoader(this, DALI_URL);
    }

    @Override
    public void onLoaderReset(Loader<List<Person>> loader) {
        personArrayList.clear();
    }

    @Override
    public void onLoadFinished(Loader<List<Person>> loader, List<Person> people) {
        Log.e(LOG_TAG, people.toString());
        personArrayList = new ArrayList<>(people);
        startIntentService(personArrayList);
        personAdapter = new PersonAdapter(this, personArrayList);
        recyclerView.setAdapter(personAdapter);
        }

        class AddressResultReceiver extends ResultReceiver{

                public AddressResultReceiver(Handler handler) {
                    super(handler);
                }

                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {

                    if (resultData == null) {
                        return;
                    }

                    personArrayList = resultData.getParcelableArrayList(Constants.RESULT_DATA_KEY);
                    personAdapter.notifyDataSetChanged();
                }
        }
}
