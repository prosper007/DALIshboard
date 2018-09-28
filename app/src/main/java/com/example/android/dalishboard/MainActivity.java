package com.example.android.dalishboard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
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
    private static final String LOG_TAG = MainActivity.class.getName();
    private AddressResultReceiver mResultReceiver;
    private boolean isFirstLoaderFinish = true;
    private boolean isFirstLoaderCreate = true;
    private ProgressBar mProgressBar;
    NetworkReceiver mReceiver;
    Snackbar mNoConnectionSnackBar;
    String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        personArrayList = new ArrayList<>();
        mResultReceiver = new AddressResultReceiver(new Handler());
        //Register BroadCast Receiver
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new NetworkReceiver();
        registerReceiver(mReceiver, intentFilter);
        mNoConnectionSnackBar = Snackbar.make(findViewById(R.id.coordinator_layout),
                R.string.lost_connection, Snackbar.LENGTH_INDEFINITE);
        mQuery = getIntent().getStringExtra(SearchManager.QUERY);


        mProgressBar = findViewById(R.id.progress);


        loadLoader();
        doMySearch(mQuery);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("onNewIntent: ", "I ran now");
        setIntent(intent);
        doMySearch(mQuery);
    }



    public void doMySearch(String query) {
        ArrayList<Person> results = new ArrayList<>();
        for(Person person : personArrayList){
            if(person.toString().toLowerCase().contains(query.toLowerCase().trim())){
                results.add(person);
            }
        }
        Log.i("results: ", results.toString());
        PersonAdapter resultAdapter = new PersonAdapter(this, results);
        recyclerView.setAdapter(resultAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        recyclerView.setAdapter(personAdapter);
        //super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        //Enabling assisted search
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(onQueryTextListener);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                recyclerView.setAdapter(personAdapter);
                return true;
            }
        });

        return true;
    }

    private final SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String query) {
            doMySearch(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };


    private void loadLoader() {
        boolean isConnected;
        TextView mNoConnection;

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        mNoConnection = findViewById(R.id.no_network);


        if (isConnected) {
            mNoConnection.setVisibility(View.GONE);
            mNoConnectionSnackBar.dismiss();
            getLoaderManager().initLoader(MAIN_LOADER_ID, null, this);
        } else {
            if (isFirstLoaderCreate) {
                mProgressBar.setVisibility(View.GONE);
                mNoConnection.setText(R.string.no_internet);
                mNoConnection.setVisibility(View.VISIBLE);
                mNoConnectionSnackBar.dismiss();
            } else {
                mNoConnectionSnackBar = Snackbar.make(findViewById(R.id.coordinator_layout),
                        R.string.lost_connection, Snackbar.LENGTH_INDEFINITE);
                mNoConnectionSnackBar.show();
            }
        }
    }


    protected void startIntentService(List<Person> people) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putParcelableArrayListExtra(Constants.LOCATION_DATA_EXTRA, (ArrayList<Person>) people);
        startService(intent);
    }

    @Override
    public Loader<List<Person>> onCreateLoader(int i, Bundle bundle) {
        isFirstLoaderCreate = false;
        return new PersonLoader(this, DALI_URL);
    }

    @Override
    public void onLoaderReset(Loader<List<Person>> loader) {
        personArrayList.clear();
    }

    @Override
    public void onLoadFinished(Loader<List<Person>> loader, List<Person> people) {
        mProgressBar.setVisibility(View.GONE);
        if(isFirstLoaderFinish) {
            personArrayList = new ArrayList<>(people);
            startIntentService(personArrayList);
            isFirstLoaderFinish = false;
        }
        personAdapter = new PersonAdapter(this, personArrayList);
        recyclerView.setAdapter(personAdapter);
    }

    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            personAdapter.refreshPersons(resultData.<Person>getParcelableArrayList(Constants.RESULT_DATA_KEY));

        }
    }

    public class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadLoader();
        }
    }
}
