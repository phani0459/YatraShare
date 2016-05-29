package com.yatrashare.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.yatrashare.R;
import com.yatrashare.adapter.RecyclerViewAdapter;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.GooglePlacesDto;
import com.yatrashare.dtos.PlaceDetailDto;
import com.yatrashare.dtos.SerializedPlace;
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class SearchPlacesActivity extends AppCompatActivity implements SearchView.OnCloseListener, SearchView.OnQueryTextListener, RecyclerViewAdapter.SetOnItemClickListener {

    public ProgressBar searchProgressBar;
    public RecyclerView searchResultsList;
    private String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        searchProgressBar = (ProgressBar) findViewById(R.id.searchProgressBar);
        searchResultsList = (RecyclerView) findViewById(R.id.searchResultsList);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = Utils.getSharedPrefs(this);

        country = "in";

        CountryData countryData = Utils.getCountryInfo(this, sharedPreferences.getString(Constants.PREF_USER_COUNTRY, ""));

        if (countryData != null) {
            country = countryData.CountryCode;
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(SearchPlacesActivity.this);

        searchResultsList.setHasFixedSize(true);
        searchResultsList.setLayoutManager(mLayoutManager);

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
            // to Remove Search Icon
            ImageView magImage = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
            magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void getPlacesDetails(String placeid) {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(Utils.getOkHttpClient(this))
                    .build();
            YatraShareAPI yatraShareAPI = retrofit.create(YatraShareAPI.class);
            Call<PlaceDetailDto> call = yatraShareAPI.getPlaceDetailsAPI(placeid, getString(R.string.server_api_key));
            call.enqueue(new Callback<PlaceDetailDto>() {
                /**
                 * administrative_area_level_1 = State
                 * locality = City
                 * country = Country
                 */
                /**
                 * Successful HTTP response.
                 *
                 * @param response response from server
                 * @param retrofit adapter
                 */
                @Override
                public void onResponse(retrofit.Response<PlaceDetailDto> response, Retrofit retrofit) {
                    android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                    if (response.body() != null && response.isSuccess()) {
                        String city = "";
                        String state = "";
                        if (response.body().result.address_components != null && response.body().result.address_components.size() > 0) {
                            for (PlaceDetailDto.AddressComponent addressComponent : response.body().result.address_components) {
                                if (addressComponent.types.contains("locality")) {
                                    city = addressComponent.long_name;
                                }
                                if (addressComponent.types.contains("administrative_area_level_1")) {
                                    state = addressComponent.long_name;
                                }
                            }
                        }
                        SerializedPlace serializedPlace = new SerializedPlace();
                        serializedPlace.address = response.body().result.formatted_address;
                        serializedPlace.latitude = response.body().result.geometry.location.lat;
                        serializedPlace.longitude = response.body().result.geometry.location.lng;
                        serializedPlace.city = city;
                        serializedPlace.state = state;

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("Searched Place", serializedPlace);
                        setResult(RESULT_OK, returnIntent);
                        finish();
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
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    .client(Utils.getOkHttpClient(this))
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
                            searchResultsList.setAdapter(new RecyclerViewAdapter(results, SearchPlacesActivity.this));
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
                    t.printStackTrace();
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

    @Override
    public void onItemClick(GooglePlacesDto.PlaceResults data) {
        searchProgressBar.setVisibility(View.VISIBLE);
        searchResultsList.setVisibility(View.GONE);
        getPlacesDetails(data.place_id);
    }
}
