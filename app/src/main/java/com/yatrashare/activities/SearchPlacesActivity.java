package com.yatrashare.activities;

import android.app.SearchManager;
import android.content.Context;
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

public class SearchPlacesActivity extends AppCompatActivity implements SearchView.OnCloseListener, SearchView.OnQueryTextListener {

    public ProgressBar searchProgressBar;
    public RecyclerView searchResultsList;
    private boolean hasMoreExhibits = true;
    private String nextPageNumber;
    private boolean mIsLoading = false;
    private boolean mIsLastPage = false;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        searchProgressBar = (ProgressBar) findViewById(R.id.searchProgressBar);
        searchResultsList = (RecyclerView) findViewById(R.id.searchResultsList);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLayoutManager = new LinearLayoutManager(SearchPlacesActivity.this);

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


    @Override
    public boolean onClose() {
        return false;
    }

    String queryString = "";

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!TextUtils.isEmpty(query)) {
            searchProgressBar.setVisibility(View.VISIBLE);
            searchResultsList.setVisibility(View.GONE);
            queryString = query;
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
