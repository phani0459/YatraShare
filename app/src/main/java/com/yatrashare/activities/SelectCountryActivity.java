package com.yatrashare.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.yatrashare.R;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.CountryInfo;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SelectCountryActivity extends AppCompatActivity {

    @Bind(R.id.selectCountry_spin)
    public Spinner selectCountrySpinner;
    private ArrayList<String> countries;
    @Bind(R.id.country_progress)
    ProgressBar splashProgress;
    @Bind(R.id.countryProgressBGView)
    View progressBGView;
    private ArrayList<CountryData> countryDatas;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);
        ButterKnife.bind(this);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        countryDatas = (ArrayList<CountryData>) getIntent().getExtras().getSerializable("Countries");

        countries = new ArrayList<>();
        countries.add("Select your country");
        for (CountryData data : countryDatas) {
            countries.add(data.CountryName);
        }

        selectCountrySpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, countries));
    }

    public String getCountryCode(String countryName) {
        for (CountryData data : countryDatas) {
            if (data.CountryName.equalsIgnoreCase(countryName)) {
                return data.CountryCode;
            }
        }
        return "";
    }

    @OnClick(R.id.btn_submitcountry)
    public void submitCountry() {
        String selectedCountry = selectCountrySpinner.getSelectedItem().toString();
        if (!TextUtils.isEmpty(selectedCountry) && !selectedCountry.equalsIgnoreCase("Select your country")) {
            getCountryInfo(getCountryCode(selectedCountry), selectedCountry);
        } else {
            Utils.showToast(this, "Select Country");
        }
    }

    public void startHomePage() {
        Intent mainIntent = new Intent(SelectCountryActivity.this, HomeActivity.class);
        overridePendingTransition(R.anim.jump_to_down, R.anim.jump_from_down);
        startActivity(mainIntent);
        finish();
    }

    private void getCountryInfo(final String countryCode, final String countryName) {
        if (Utils.isInternetAvailable(this)) {
            Utils.showProgress(true, splashProgress, progressBGView);
            mEditor.putString(Constants.PREF_USER_COUNTRY, countryName);
            mEditor.apply();
            Call<CountryInfo> call = Utils.getYatraShareAPI(this).GetCountryInfo(countryCode);
            call.enqueue(new Callback<CountryInfo>() {
                @Override
                public void onResponse(Response<CountryInfo> response, Retrofit retrofit) {
                    Log.e("Response raw", "" + response.raw());
                    if (response.body() != null && response.body().Data != null) {
                        mEditor.putString(Constants.PREF_USER_TOKEN, response.body().Data.Token);
                        mEditor.apply();
                        Utils.saveCountryInfo(SelectCountryActivity.this, response.body().Data, countryName);
                        Utils.showProgress(false, splashProgress, progressBGView);
                        startHomePage();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Utils.showProgress(false, splashProgress, progressBGView);
                    startHomePage();
                }
            });
        }
    }
}
