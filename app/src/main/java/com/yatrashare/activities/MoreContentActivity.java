package com.yatrashare.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.MoreDataDTO;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreContentActivity extends AppCompatActivity {


    @Bind(R.id.mainLinearLayout)
    public LinearLayout mainLayout;
    @Bind(R.id.more_progress)
    public ProgressBar progressBar;
    @Bind(R.id.moreProgressBGView)
    public View bgView;


    public static final String howItWorks = "HowYatrashareWorks";
    public static final String ContactUs = "ContactUs";
    public static final String faqS = "FAQ";
    public static final String PrivacyPolicy = "PrivacyPolicy";
    public static final String WhyYatrashare = "WhyYatrashare";
    public static final String terms = "Terms";

    private Context mContext;
    private CountryData country;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_scroll);
        ButterKnife.bind(this);
        mContext = this;

        String contentType = getIntent().getExtras().getString("KEY", "");
        String title = getIntent().getExtras().getString("TITLE", "");
        country = Utils.getCountryInfo(mContext, Utils.getSharedPrefs(mContext).getString(Constants.PREF_USER_COUNTRY, ""));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        if (Utils.isInternetAvailable(mContext)) {
            getContent(contentType);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    public void getContent(String contentType) {
        Utils.showProgress(true, progressBar, bgView);
        Call<MoreDataDTO> call = Utils.getYatraShareAPI(mContext).getMoreContent(contentType, country.CountryCode);

        call.enqueue(new Callback<MoreDataDTO>() {
            @Override
            public void onResponse(Response<MoreDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response.body() != null && response.body().Data != null) {
                    if (response.body().Data.size() > 0) {
                        loadContent(response.body().Data);
                    }
                }
                Utils.showProgress(false, progressBar, bgView);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Utils.showProgress(false, progressBar, bgView);
            }
        });

    }

    private void loadContent(ArrayList<MoreDataDTO.MoreSection> data) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < data.size(); i++) {
            View view = inflater.inflate(R.layout.more_texts, null);

            TextView sectionHeading = (TextView) view.findViewById(R.id.heading);
            WebView sectionDescription = (WebView) view.findViewById(R.id.description);

            if (TextUtils.isEmpty(data.get(i).SectionName)) {
                sectionHeading.setVisibility(View.GONE);
            } else {
                sectionHeading.setText(data.get(i).SectionName);
            }

            if (TextUtils.isEmpty(data.get(i).Content)) {
                sectionDescription.setVisibility(View.GONE);
            } else {
                sectionDescription.loadData(data.get(i).Content, "text/html", "utf-8");
            }

            mainLayout.addView(view);

        }
    }

}
