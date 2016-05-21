package com.yatrashare.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.yatrashare.R;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.GooglePlacesDto;
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class SearchPlacesActivity extends AppCompatActivity implements SearchView.OnCloseListener, SearchView.OnQueryTextListener {

    public ProgressBar searchProgressBar;
    public ListView searchResultsList;
    private String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        searchProgressBar = (ProgressBar) findViewById(R.id.searchProgressBar);
        searchResultsList = (ListView) findViewById(R.id.searchResultsList);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = Utils.getSharedPrefs(this);

        country = "in";

        CountryData countryData = Utils.getCountryInfo(this, sharedPreferences.getString(Constants.PREF_USER_COUNTRY, ""));

        if (countryData != null) {
            country = countryData.CountryCode;
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.powered_google, null);
        searchResultsList.addFooterView(view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setQueryHint(getString(R.string.search_title));
        searchView.setOnCloseListener(this);
        searchView.setOnQueryTextListener(this);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        try {
            // to Remove Search Iocn
            ImageView magImage = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
            magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPlacesFromApi(String query) {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(Utils.getOkHttpClient())
                    .build();
            YatraShareAPI yatraShareAPI = retrofit.create(YatraShareAPI.class);
            Call<GooglePlacesDto> call = yatraShareAPI.getGooglePlacesAPI(query, getString(R.string.server_api_key), "country:" + country.toLowerCase());
            call.enqueue(new Callback<GooglePlacesDto>() {
                /**
                 * Successful HTTP response.
                 *
                 * @param response response from server
                 * @param retrofit adapter
                 */
                @Override
                public void onResponse(retrofit.Response<GooglePlacesDto> response, Retrofit retrofit) {
                    android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                    if (response.body() != null && response.isSuccess()) {
                        ArrayList<GooglePlacesDto.PlaceResults> results = response.body().predictions;
                        if (results != null && results.size() > 0) {
                            ArrayList<String> places = new ArrayList<String>();
                            for (int i = 0; i < results.size(); i++) {
                                GooglePlacesDto.PlaceResults component = results.get(i);
                                if (!TextUtils.isEmpty(component.description)) {
                                    places.add(component.description);
                                }
                            }
                            searchResultsList.setAdapter(new ArrayAdapter<String>(SearchPlacesActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, places));
                        }
                    }
                    searchProgressBar.setVisibility(View.GONE);
                    searchResultsList.setVisibility(View.VISIBLE);
                }

                /**
                 * Invoked when a network or unexpected exception occurred during the HTTP request.
                 *
                 * @param t throwable error
                 */
                @Override
                public void onFailure(Throwable t) {
                    android.util.Log.e("", "FAILURE RESPONSE");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onClose() {
        searchProgressBar.setVisibility(View.GONE);
        searchResultsList.setVisibility(View.GONE);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!TextUtils.isEmpty(query)) {
            searchProgressBar.setVisibility(View.VISIBLE);
            searchResultsList.setVisibility(View.GONE);
            getPlacesFromApi(query);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            searchProgressBar.setVisibility(View.VISIBLE);
            searchResultsList.setVisibility(View.GONE);
            getPlacesFromApi(newText);
        }
        return false;
    }
}
