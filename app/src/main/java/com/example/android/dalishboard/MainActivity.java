package com.example.android.dalishboard;


import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.design.widget.Snackbar;
import android.content.BroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Person>> {

    private RecyclerView recyclerView;
    private PersonAdapter personAdapter;
    private ArrayList<Person> personArrayList;
    private static final int MAIN_LOADER_ID = 0;
    private static final String DALI_URL = "http://mappy.dali.dartmouth.edu/members.json";
    private ProgressBar mProgressBar;
    private NetworkReceiver mReceiver;
    private Snackbar mNoConnectionSnackBar;
    private TextView emptyView;
    //flag to indicate first time app is opened/ data is fetched
    private boolean isFirstLoaderCreate = true;
    //flag to indicate if data has been loaded before to prevent unnecessary loading
    private boolean isFirstLoaderFinish = true;
    private static final String LOG_TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //recyclerView to hold and display data
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Instantiate arrayList holding extracted people
        personArrayList = new ArrayList<>();

        //Register BroadCast Receiver
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new NetworkReceiver();
        registerReceiver(mReceiver, intentFilter);

        //empty view for when there is no result for search query. Visiblity set to gone in
        //layout file
        emptyView = findViewById(R.id.empty);


        //create loader and fetch data appropriately
        loadLoader();
    }

    //<-------------------------- LOADER METHODS -------------------------------------------------->

    //creates loader and displays appropriate views depending on device connection status and app state
    private void loadLoader() {
        // get device's current network status
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // determine if device has internet connection
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();

        //TextView that'll be displayed if there's no internet connection
        TextView mNoConnection = findViewById(R.id.no_network);

        if (isConnected) {
            //make no-connection view disappear
            mNoConnection.setVisibility(View.GONE);

            //initialize loader
            getLoaderManager().initLoader(MAIN_LOADER_ID, null, this);

            //dismiss no-connection-snackbar if it's been shown
            if (mNoConnectionSnackBar != null) {
                mNoConnectionSnackBar.dismiss();
            }
        } else {
            //If there's no connection the first time the app is run, show blank "no connection screen"
            if (isFirstLoaderCreate) {
                mProgressBar.setVisibility(View.GONE);
                mNoConnection.setText(R.string.no_internet);
                mNoConnection.setVisibility(View.VISIBLE);
            }
            //If connection is lost while using app, notify user with snackbar
            else {
                mNoConnectionSnackBar = Snackbar.make(findViewById(R.id.coordinator_layout),
                        R.string.lost_connection, Snackbar.LENGTH_INDEFINITE);
                mNoConnectionSnackBar.show();
            }
        }
    }

    //sets isFirstLoaderCreate to false, show progress bar and initialize loader appropriately
    @Override
    public Loader<List<Person>> onCreateLoader(int i, Bundle bundle) {
        //set flag to false to indicate loader has been created before
        isFirstLoaderCreate = false;

        //Show progress bar while loader fetches data.
        mProgressBar = findViewById(R.id.progress);
        mProgressBar.setVisibility(View.VISIBLE);

        //create new loader with MainActivity and desired url as input
        return new PersonLoader(this, DALI_URL);
    }

    //clears personsArrayList when loader is reset
    @Override
    public void onLoaderReset(Loader<List<Person>> loader) {
        personArrayList.clear();
    }

    //populate personArrayList, start fetchAddressIntentService, set recyclerView adapter,
    //set isFirstLoaderFinish to false
    @Override
    public void onLoadFinished(Loader<List<Person>> loader, List<Person> people) {
        //hide progress bar when data is ready
        mProgressBar.setVisibility(View.GONE);

        //only create new array if data hasn't been loaded before
        if (isFirstLoaderFinish) {
            //pass people result into personArrayList
            personArrayList = new ArrayList<>(people);

            //start fetchAddressIntentService to resolve persons addresses after
            // PersonArrayList data becomes available
            startIntentService(personArrayList);

            //set flag to false to indicate data has been fetched before
            isFirstLoaderFinish = false;
        }

        //initialize adapter with populated arraylist and set recylclerView's to created adapter
        personAdapter = new PersonAdapter(this, personArrayList);
        recyclerView.setAdapter(personAdapter);
    }

    //<-------------------------- INTENT SERVICE METHODS ------------------------------------------>

    //create intent and pass in AddressResultReceiver and personArrayList for coordinates to be
    //resolved to addresses
    protected void startIntentService(List<Person> people) {
        //Register AddressResultReceiver object to pass address to FetchAddressIntentService class
        AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());

        //Create explicit intent and specify FetchAddressIntentService to handle intent
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        //pass in receiver so we can receive the results
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        //pass in personArrayList for address processing
        intent.putParcelableArrayListExtra(Constants.LOCATION_DATA_EXTRA, (ArrayList<Person>) people);

        //launch service
        startService(intent);
    }

    //Inner class that enables MainActivity receive intent service results
    class AddressResultReceiver extends ResultReceiver {

        //Constructor
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        //updates list of persons with resolved addresses
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            //return if there was no result
            if (resultData == null) {
                return;
            }

            // update adapter with new list of persons that contain addresses
            personAdapter.refreshPersons(resultData.<Person>getParcelableArrayList(Constants.RESULT_DATA_KEY));
        }
    }

    //<-------------------------- NETWORK RECEIVER METHODS ---------------------------------------->

    //inner class that notifies app of network changes and  calls loadLoader to react appropriately
    public class NetworkReceiver extends BroadcastReceiver {

        //call loadLoader to handle network changes appropriately
        @Override
        public void onReceive(Context context, Intent intent) {
            loadLoader();
        }
    }

    //unregister mReceiver when app is exited to save device resources
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    //<-------------------------- SEARCH METHODS -------------------------------------------------->

    //inflates menu with search widget, sets listener that handles searches, and returns
    // recyclerView after search
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();


        //Enabling assisted search
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // Do not iconify the widget; expand it by default
        searchView.setIconifiedByDefault(false);

        //set onQueryTextListener to handle searches
        searchView.setOnQueryTextListener(onQueryTextListener);


        //show full list when search widget is collapsed
        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                //set recyclerView to adapter with full list of person
                recyclerView.setAdapter(personAdapter);

                //hide emptyView if it was shown and display recyclerView again
                if (recyclerView.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                if (emptyView.getVisibility() == View.VISIBLE) {
                    emptyView.setVisibility(View.GONE);
                }
                return true;
            }
        });

        return true;
    }

    //OnQueryTextListener object handles searches by passing search query to doMySearch method and
    //suppresses call to android default OnSearchRequested() method.
    private final SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {

        //gets String query and passes it to doMySearch method
        @Override
        public boolean onQueryTextSubmit(String query) {
            doMySearch(query);
            //indicates that the search was handled here and the default OnSearchRequested() method
            //does not need to be called
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    //performs search
    public void doMySearch(String query) {
        //initialize arraylist of results
        ArrayList<Person> results = new ArrayList<>();

        //search personArrayList for query
        for (Person person : personArrayList) {
            if (person.toString().toLowerCase().contains(query.toLowerCase().trim())) {
                results.add(person);
            }
        }

        //Create new adapter with just results
        PersonAdapter resultAdapter = new PersonAdapter(this, results);

        //if there were no results, make the emptyView visible
        if (results.size() == 0) {
            emptyView.setText(R.string.no_result);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        //else display the results
        else {
            recyclerView.setAdapter(resultAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    //<-------------------------------------------------------------------------------------------->

}
