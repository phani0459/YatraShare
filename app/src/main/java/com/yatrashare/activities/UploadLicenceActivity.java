package com.yatrashare.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yatrashare.R;
import com.yatrashare.utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UploadLicenceActivity extends AppCompatActivity {

    @Bind(R.id.im_licence_One)
    SimpleDraweeView licenceOneDrawee;
    @Bind(R.id.im_licence_Two)
    SimpleDraweeView licenceTwoDrawee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_licence);
        ButterKnife.bind(this);

        licenceOneDrawee.setImageURI(Constants.getDefaultNoImageURI());
        licenceTwoDrawee.setImageURI(Constants.getDefaultNoImageURI());

        String userGuide = getIntent().getExtras().getString("UserGuide", "");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Driving Licence");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
