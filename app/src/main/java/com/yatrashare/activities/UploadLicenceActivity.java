package com.yatrashare.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.yatrashare.R;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class UploadLicenceActivity extends AppCompatActivity {

    private static final String TAG = UploadLicenceActivity.class.getSimpleName();
    @Bind(R.id.im_licence_One)
    SimpleDraweeView licenceOneDrawee;
    @Bind(R.id.im_licence_Two)
    SimpleDraweeView licenceTwoDrawee;
    private Context mContext;
    private Uri licenceOneUri, licenceTwoUri;
    @Bind(R.id.btn_remove_licence_One)
    public Button removeLicenceOneButton;
    @Bind(R.id.btn_remove_licence_Two)
    public Button removeLicenceTwoButton;
    @Bind(R.id.licenceProgress)
    public ProgressBar mProgressBar;
    @Bind(R.id.licenceProgressBGView)
    public View mProgressBGView;
    private String userGuid;
    private SharedPreferences.Editor mSharedPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_licence);
        ButterKnife.bind(this);
        mContext = this;

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSharedPrefEditor = mSharedPreferences.edit();

        String licenceOne = mSharedPreferences.getString(Constants.PREF_USER_LICENCE_1, "");
        String licenceTwo = mSharedPreferences.getString(Constants.PREF_USER_LICENCE_2, "");

        GenericDraweeHierarchy hierarchy = licenceOneDrawee.getHierarchy();
        hierarchy.setProgressBarImage(new ProgressBarDrawable());
        licenceOneDrawee.setHierarchy(hierarchy);

        if (!TextUtils.isEmpty(licenceOne) && !licenceOne.startsWith("/")) {
            Uri uri = Uri.parse(licenceOne);
            licenceOneDrawee.setImageURI(uri);
            removeLicenceOneButton.setVisibility(View.VISIBLE);
        } else {
            licenceOneDrawee.setImageURI(Constants.getDefaultNoImageURI());
            removeLicenceOneButton.setVisibility(View.GONE);
        }

        GenericDraweeHierarchy hierarch2y = licenceTwoDrawee.getHierarchy();
        hierarch2y.setProgressBarImage(new ProgressBarDrawable());
        licenceTwoDrawee.setHierarchy(hierarch2y);

        if (!TextUtils.isEmpty(licenceTwo) && !licenceTwo.startsWith("/")) {
            Uri uri = Uri.parse(licenceTwo);
            licenceTwoDrawee.setImageURI(uri);
            removeLicenceTwoButton.setVisibility(View.VISIBLE);
        } else {
            licenceTwoDrawee.setImageURI(Constants.getDefaultNoImageURI());
            removeLicenceTwoButton.setVisibility(View.GONE);
        }

        userGuid = getIntent().getExtras().getString("UserGuide", "");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Driving Licence");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static final int REQUEST_READ_STORAGE_ONE = 14;
    private static final int REQUEST_READ_STORAGE_TWO = 12;

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_READ_STORAGE_ONE) {
                uploadLicenceOne();
            }
            if (requestCode == REQUEST_READ_STORAGE_TWO) {
                uploadLicenceTwo();
            }
        }
    }

    private boolean mayRequestStorage(final int licenceNumber) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (mContext.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(licenceOneDrawee, R.string.storage_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, licenceNumber);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, licenceNumber);
        }
        return false;
    }

    @OnClick(R.id.im_licence_One)
    public void browseLicenceOne() {
        licenceOneUri = null;
        startActivityForResult(getPickImageChooserIntent(), RESULT_LOAD_LICENCEONE);
    }

    @OnClick(R.id.im_licence_Two)
    public void browseLicenceTwo() {
        licenceTwoUri = null;
        startActivityForResult(getPickImageChooserIntent(), RESULT_LOAD_LICENCETWO);
    }

    public void showSnackBar(String msg) {
        Snackbar.make(licenceOneDrawee, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @OnClick(R.id.btn_licence_One)
    public void uploadLicenceOne() {
        if (Utils.isInternetAvailable(mContext)) {
            if (licenceOneUri != null) {
                if (!mayRequestStorage(REQUEST_READ_STORAGE_ONE)) {
                    return;
                }

                File file = null;
                try {
                    URI uri = new URI(licenceOneUri.toString());
                    file = new File(uri);
                } catch (Exception e) {
                    file = null;
                }
                try {
                    if (file == null) {
                        file = new File(Utils.getPath(licenceOneUri, mContext));
                    }

                    Utils.showProgress(true, mProgressBar, mProgressBGView);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    RequestBody body = new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart("Licence", file.getName(), requestFile).build();

                    Call<UserDataDTO> call = Utils.getYatraShareAPI().uploadLicenceOne(userGuid, body);
                    call.enqueue(new Callback<UserDataDTO>() {

                        @Override
                        public void onResponse(Response<UserDataDTO> response, Retrofit retrofit) {
                            android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                            if (response.body() != null && response.isSuccess()) {
                                Log.e(TAG, "Licence One Pic: " + response.body().Data);
                                showSnackBar("One side of licence uploaded successfully.");
                                mSharedPrefEditor.putString(Constants.PREF_USER_LICENCE_1, response.body().Data);
                                mSharedPrefEditor.commit();
                                Utils.deleteFile(mContext, userGuid);
                            }
                            Utils.showProgress(false, mProgressBar, mProgressBGView);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                            android.util.Log.e(TAG, "FAILURE RESPONSE");
                            Utils.showProgress(false, mProgressBar, mProgressBGView);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Utils.showToast(mContext, "Select Image");
            }

        }
    }

    @OnClick(R.id.btn_licence_Two)
    public void uploadLicenceTwo() {
        if (Utils.isInternetAvailable(mContext)) {
            if (licenceTwoUri != null) {
                if (!mayRequestStorage(REQUEST_READ_STORAGE_TWO)) {
                    return;
                }

                File file = null;
                try {
                    URI uri = new URI(licenceTwoUri.toString());
                    file = new File(uri);
                } catch (Exception e) {
                    file = null;
                }
                try {
                    if (file == null) {
                        file = new File(Utils.getPath(licenceTwoUri, mContext));
                    }

                    Utils.showProgress(true, mProgressBar, mProgressBGView);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    RequestBody body = new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart("Licence", file.getName(), requestFile).build();

                    Call<UserDataDTO> call = Utils.getYatraShareAPI().uploadLicenceTwo(userGuid, body);
                    call.enqueue(new Callback<UserDataDTO>() {

                        @Override
                        public void onResponse(Response<UserDataDTO> response, Retrofit retrofit) {
                            android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                            if (response.body() != null && response.isSuccess()) {
                                Log.e(TAG, "Licence Two Pic: " + response.body().Data);
                                showSnackBar("Licence uploaded succesfully");
                                mSharedPrefEditor.putString(Constants.PREF_USER_LICENCE_2, response.body().Data);
                                mSharedPrefEditor.commit();
                                Utils.deleteFile(mContext, userGuid);
                            }
                            Utils.showProgress(false, mProgressBar, mProgressBGView);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                            android.util.Log.e(TAG, "FAILURE RESPONSE");
                            Utils.showProgress(false, mProgressBar, mProgressBGView);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Utils.showToast(mContext, "Select Image");
            }

        }
    }

    @OnClick(R.id.btn_remove_licence_One)
    public void removeLicenceOne() {
        licenceOneUri = null;
        removeLicenceOneButton.setVisibility(View.GONE);
        licenceOneDrawee.setImageURI(Constants.getDefaultNoImageURI());

        Call<UserDataDTO> call = Utils.getYatraShareAPI().removeLicence(userGuid, "1");

        call.enqueue(new Callback<UserDataDTO>() {
            @Override
            public void onResponse(Response<UserDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                if (response.body() != null && response.isSuccess()) {
                    Log.e(TAG, "Empty Licence One: " + response.body().Data);
                    showSnackBar("Licence One removed successfully");
                    mSharedPrefEditor.putString(Constants.PREF_USER_LICENCE_1, response.body().Data);
                    mSharedPrefEditor.commit();
                    Utils.deleteFile(mContext, userGuid);
                }
                Utils.showProgress(false, mProgressBar, mProgressBGView);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Utils.showProgress(false, mProgressBar, mProgressBGView);
            }
        });

    }

    @OnClick(R.id.btn_remove_licence_Two)
    public void removeLicenceTwo() {
        licenceTwoUri = null;
        removeLicenceTwoButton.setVisibility(View.GONE);
        licenceTwoDrawee.setImageURI(Constants.getDefaultNoImageURI());

        Call<UserDataDTO> call = Utils.getYatraShareAPI().removeLicence(userGuid, "2");

        call.enqueue(new Callback<UserDataDTO>() {
            @Override
            public void onResponse(Response<UserDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                if (response.body() != null && response.isSuccess()) {
                    Log.e(TAG, "Empty Licence Two: " + response.body().Data);
                    showSnackBar("Licence Two removed successfully");
                    mSharedPrefEditor.putString(Constants.PREF_USER_LICENCE_2, response.body().Data);
                    mSharedPrefEditor.commit();
                    Utils.deleteFile(mContext, userGuid);
                }
                Utils.showProgress(false, mProgressBar, mProgressBGView);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Utils.showProgress(false, mProgressBar, mProgressBGView);
            }
        });
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = mContext.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_LICENCEONE && resultCode == Activity.RESULT_OK) {
            licenceOneUri = getPickImageResultUri(data);
            licenceOneDrawee.setImageURI(licenceOneUri);
            removeLicenceOneButton.setVisibility(View.VISIBLE);
        } else if (requestCode == RESULT_LOAD_LICENCETWO && resultCode == Activity.RESULT_OK) {
            licenceTwoUri = getPickImageResultUri(data);
            licenceTwoDrawee.setImageURI(licenceTwoUri);
            removeLicenceTwoButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<Intent>();
        PackageManager packageManager = mContext.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private static int RESULT_LOAD_LICENCEONE = 1;
    private static int RESULT_LOAD_LICENCETWO = 2;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
