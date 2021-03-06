package com.yatrashare.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yatrashare.R;
import com.yatrashare.activities.OfferRideActivity;
import com.yatrashare.activities.RegisterVehicleActivity;
import com.yatrashare.dtos.GoogleMapsDto;
import com.yatrashare.dtos.Seats;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.dtos.Vehicle;
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.pojos.RideInfo;
import com.yatrashare.pojos.RideInfoDto;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublishRideFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = PublishRideFragment.class.getSimpleName();
    private static final int REQUEST_CODE_REGISTER_VEHICLE = 28;
    @Bind(R.id.rbtn_vehicle_car)
    public RadioButton radioButtonCar;
    @Bind(R.id.rbtn_vehicle_bike)
    public RadioButton radioButtonBike;
    @Bind(R.id.selectModel)
    public Spinner selectModelSpinner;
    @Bind(R.id.selectSeats)
    public Spinner selectSeatsSpinner;
    @Bind(R.id.et_provideRideDetails)
    public EditText provideRideDetEditText;
    @Bind(R.id.luggageSpinner)
    public Spinner luggageSpinner;
    @Bind(R.id.timeFlexiSpinner)
    public Spinner timeFlexiSpinner;
    @Bind(R.id.detourSpinner)
    public Spinner detourSpinner;
    @Bind(R.id.cb_ladiesOnly)
    public CheckBox ladiesOnlyCheckBox;
    @Bind(R.id.cb_agreeTerms)
    public CheckBox agreeTermsCheckBox;
    @Bind(R.id.publishRideBGView)
    public View mProgressBGView;
    @Bind(R.id.publishRide_progress)
    public ProgressBar mProgressBar;
    public String selectedVehicle;

    private Context mContext;
    private String userGuid;
    private ArrayList<Vehicle.VehicleData> vehicleDatas;
    private String selectedVehicleId, selectedModel;
    private String seatsSelected, selectedLuggageSize, selectedTimeFlexi, selectedDetour;
    private RideInfoDto rideInfoDto;

    public PublishRideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View inflatedLayout = inflater.inflate(R.layout.fragment_offer_nxt_step, null);
        mContext = getActivity();

        ButterKnife.bind(this, inflatedLayout);
        rideInfoDto = (RideInfoDto) getArguments().getSerializable("RIDE INFO");

        SharedPreferences mSharedPreferences = Utils.getSharedPrefs(mContext);
        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        luggageSpinner.setSelection(1);
        timeFlexiSpinner.setSelection(1);
        detourSpinner.setSelection(1);

        radioButtonCar.setChecked(true);
        selectedVehicle = "1";
        getUserVehicleModels();

        return inflatedLayout;
    }

    @OnClick(R.id.registerNewVehicle)
    public void registerVehicle() {
        Intent intent = new Intent(mContext, RegisterVehicleActivity.class);
        intent.putExtra("Selected Vehicle", radioButtonCar.isChecked() ? "1" : "2");
        intent.putExtra("User Guide", userGuid);
        startActivityForResult(intent, REQUEST_CODE_REGISTER_VEHICLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        radioButtonCar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectSeatsSpinner.setEnabled(true);
                    luggageSpinner.setVisibility(View.VISIBLE);
                    selectedVehicle = "1";
                    ArrayList<String> vehicleSeats = new ArrayList<String>();
                    vehicleSeats.add("Select Seats");
                    selectSeatsSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleSeats));
                    getUserVehicleModels();
                }
            }
        });
        radioButtonBike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectSeatsSpinner.setEnabled(true);
                    luggageSpinner.setVisibility(View.GONE);
                    selectedVehicle = "2";
                    ArrayList<String> vehicleSeats = new ArrayList<String>();
                    vehicleSeats.add("Select Seats");
                    selectSeatsSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleSeats));
                    getUserVehicleModels();
                }
            }
        });

        luggageSpinner.setOnItemSelectedListener(this);
        selectModelSpinner.setOnItemSelectedListener(this);
        selectSeatsSpinner.setOnItemSelectedListener(this);
        timeFlexiSpinner.setOnItemSelectedListener(this);
        detourSpinner.setOnItemSelectedListener(this);
    }

    @OnClick(R.id.btn_PublishRide)
    public void publishRide() {
        if (vehicleDatas != null && vehicleDatas.size() > 0) {
            if (TextUtils.isEmpty(selectedModel) || selectedModel.equalsIgnoreCase("Select Model")) {
                Utils.showToast(mContext, "Select Vehicle Model");
                return;
            }
            if (TextUtils.isEmpty(seatsSelected) || seatsSelected.equalsIgnoreCase("Select Seats")) {
                Utils.showToast(mContext, "Select Seats");
                return;
            }
            if (TextUtils.isEmpty(selectedLuggageSize) || selectedLuggageSize.equalsIgnoreCase("Select Luggage size")) {
                luggageSpinner.setSelection(1);
                selectedLuggageSize = luggageSpinner.getSelectedItem().toString();
            }
            if (TextUtils.isEmpty(selectedTimeFlexi) || selectedTimeFlexi.equalsIgnoreCase("Time Flexibility") || selectedTimeFlexi.equalsIgnoreCase("15Mins")) {
                timeFlexiSpinner.setSelection(1);
                selectedTimeFlexi = "15Mins";
            }
            if (TextUtils.isEmpty(selectedDetour) || selectedDetour.equalsIgnoreCase("Select detour") || selectedDetour.equalsIgnoreCase("NoDetour")) {
                detourSpinner.setSelection(1);
                selectedDetour = "NoDetour";
            }

            if (!agreeTermsCheckBox.isChecked()) {
                Utils.showToast(mContext, "Agree Terms and Conditions");
                return;
            }

            mainPossibleRoutes = new ArrayList<>();
            allPossibleRoutes = new ArrayList<>();
            stopOverPoints = new ArrayList<>();

            if (Utils.isInternetAvailable(mContext)) {
                Utils.showProgress(true, mProgressBar, mProgressBGView);
                addRoutes(true);
            }

        } else {
            Utils.showToast(mContext, "Register New Vehicle");
        }
    }

    ArrayList<RideInfo.PossibleRoutes> mainPossibleRoutes = new ArrayList<>();
    ArrayList<RideInfo.PossibleRoutes> allPossibleRoutes = new ArrayList<>();

    private void addRoutes(boolean isMain) {
        if (rideInfoDto != null) {
            if (isMain) {
                getMainRoutes(0, rideInfoDto.getmMainPossibleRoutes(), true);
            } else {
                getMainRoutes(0, rideInfoDto.getmAllPossibleRoutes(), false);
            }
        }
    }


    public long getPrice(float km) {
        long price;
        if (km > 0 && km < 10) {
            price = 20;
        } else {
            price = Math.round((km * 1.8) / 10) * 10;
        }
        return price;
    }

    public void addMainRoute(RideInfoDto.PossibleRoutesDto route, String duration, String distance, int order, boolean isLast, boolean isMain) {
        /*Address arrivalAddress = getAddress(route.getArrivalLatitude(), route.getArrivalLongitude());
        Address departureAddress = getAddress(route.getDepartureLatitude(), route.getDepartureLongitude());*/

        String mDeparture;
        String mArrival;
        String mDepartureCity;
        String mArrivalCity;
        String mDepartureState;
        String mArrivalState;
        String mRoutePrice;
        String mreadOnly;
        String mkilometers;
        String morder;
        String mMainRoute;
        String mTimeframe;

        mDeparture = route.getDeparture();
        mArrival = route.getArrival();

        mDepartureCity = route.getDepartureCity();
        mArrivalCity = route.getArrivalCity();

        mDepartureState = route.getDepartureState();
        mArrivalState = route.getArrivalState();

        mRoutePrice = "";

        if (distance.contains("km")) {
            try {
                mRoutePrice = getPrice(Float.parseFloat(distance.replace("km", ""))) + "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (distance.contains(" ")) {
            mRoutePrice = getPrice(Float.parseFloat(distance.split(" ")[0])) + "";

        }
        mreadOnly = "";

        mkilometers = distance;
        morder = "" + order;
        mMainRoute = route.isMainRoute() + "";
        mTimeframe = duration;
        RideInfo.PossibleRoutes possibleRoute = new RideInfo().new PossibleRoutes(mDeparture, mArrival, mDepartureCity, mMainRoute,
                mTimeframe, mArrivalCity, mDepartureState, mArrivalState, mRoutePrice, mRoutePrice, mreadOnly, mkilometers, morder);

        if (isMain) {
            mainPossibleRoutes.add(possibleRoute);
        } else {
            allPossibleRoutes.add(possibleRoute);
        }

        if (isLast) {
            if (isMain) {
                addRoutes(false);
            } else {
                Log.e("mainPossibleRoutes: " + mainPossibleRoutes.size(), "allPossibleRoutes: " + allPossibleRoutes.size());
                getStopOvers();
            }
        }
    }

    ArrayList<RideInfo.StopOverPoints> stopOverPoints = new ArrayList<>();

    private void getStopOvers() {
        if (rideInfoDto.getmStopOvers() != null && rideInfoDto.getmStopOvers().size() > 0) {
            for (int i = 0; i < rideInfoDto.getmStopOvers().size(); i++) {
                String mStopOverLocation;
                String mStopOverState;
                String mOrder;
                String mLatitude;
                String mLongitude;
                String mStopOverAddressDetails;
                String mStopOverCity;
//                Address address = getAddress(rideInfoDto.getmStopOvers().get(i).getStopOverLatitude(), rideInfoDto.getmStopOvers().get(i).getStopOverLongitude());

                mLatitude = rideInfoDto.getmStopOvers().get(i).getStopOverLatitude() + "";
                mLongitude = rideInfoDto.getmStopOvers().get(i).getStopOverLongitude() + "";
                mStopOverAddressDetails = rideInfoDto.getmStopOvers().get(i).getStopOverLocation();
                mOrder = (i + 1) + "";
                mStopOverCity = rideInfoDto.getmStopOvers().get(i).getStopOverCity();
                mStopOverState = rideInfoDto.getmStopOvers().get(i).getStopOverState();
                mStopOverLocation = rideInfoDto.getmStopOvers().get(i).getStopOverLocation();

                RideInfo.StopOverPoints stopOverPoint = new RideInfo().new StopOverPoints(mStopOverLocation, mStopOverState, mOrder, mLatitude, mLongitude, mStopOverAddressDetails, mStopOverCity);

                stopOverPoints.add(stopOverPoint);
            }
        }
        Log.e("stopOvers", "size " + stopOverPoints.size());

        if (Utils.isInternetAvailable(mContext)) {
            prepareRide();
        } else {
            Utils.showProgress(false, mProgressBar, mProgressBGView);
        }

    }

    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.e(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.e(tag, content);
        }
    }

    private void prepareRide() {
        String mRideDeparture = rideInfoDto.getmRideDeparture();
        String mRideArrival = rideInfoDto.getmRideArrival();
        String mRideType = rideInfoDto.getmRideType();
        String mLadiesOnly = ladiesOnlyCheckBox.isChecked() ? "true" : "false";
        String mTotalkilometers = rideInfoDto.getmTotalkilometers();
        ArrayList<String> mSelectedWeekdays = rideInfoDto.getmSelectedWeekdays();
        String mTotalprice = rideInfoDto.getmTotalprice();
        String mVehicleType = radioButtonCar.isChecked() ? "1" : "2";
        String mOtherDetails = provideRideDetEditText.getText().toString();
        String mCompanyDetails = "";
        String mDepartureTime = rideInfoDto.getmDepartureTime();
        String mReturnTime = rideInfoDto.getmReturnTime();
        String mDepartureDate = rideInfoDto.getmDepartureDate();
        String mReturnDate = rideInfoDto.getmReturnDate();

        RideInfo rideInfo = new RideInfo(mDepartureDate, mReturnDate, mRideDeparture, mRideArrival, mTotalkilometers, mTotalprice, selectedTimeFlexi, selectedDetour, seatsSelected, mOtherDetails,
                mCompanyDetails, selectedVehicleId, mDepartureTime, mReturnTime, mLadiesOnly, mSelectedWeekdays, mRideType, mVehicleType, selectedLuggageSize, mainPossibleRoutes, allPossibleRoutes, stopOverPoints);

        Gson gson = new Gson();
        String ride = gson.toJson(rideInfo);
        largeLog(TAG, "prepareRide: " + ride);

        if (Utils.isInternetAvailable(mContext)) {
            Call<UserDataDTO> call = Utils.getYatraShareAPI(mContext).offerRide(userGuid, rideInfo);
            call.enqueue(new Callback<UserDataDTO>() {
                /*
                 * Successful HTTP response.
                 *
                 * @param response response from server
                 * @param retrofit adapter
                 */
                @Override
                public void onResponse(retrofit.Response<UserDataDTO> response, Retrofit retrofit) {
                    android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                    if (response.body() != null && response.body().Data != null) {
                        if (response.body().Data.contains("-")) {
                            Utils.showToast(mContext, "Successfully ride created");
                            ((OfferRideActivity) mContext).loadSuccessFragment();
                        } else {
                            Utils.showToast(mContext, response.body().Data);
                        }
                    } else {
                        Utils.showToast(mContext, getString(R.string.tryagain));
                    }
                    Utils.showProgress(false, mProgressBar, mProgressBGView);
                }

                /*
                 * Invoked when a network or unexpected exception occurred during the HTTP request.
                 *
                 * @param t throwable error
                 */
                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    Utils.showToast(mContext, getString(R.string.tryagain));
                    Utils.showProgress(false, mProgressBar, mProgressBGView);
                }
            });
        }
    }

    /*public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            return addresses.get(0);
        } else {
            return null;
        }
    }*/


    private void getMainRoutes(final int pos, final ArrayList<RideInfoDto.PossibleRoutesDto> possibleRoutesDtos, final boolean isMain) {
        if (possibleRoutesDtos != null && possibleRoutesDtos.size() > 0) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://maps.googleapis.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(Utils.getOkHttpClient(mContext))
                        .build();
                YatraShareAPI yatraShareAPI = retrofit.create(YatraShareAPI.class);
                Call<GoogleMapsDto> call = yatraShareAPI.getGoogleMapsAPI(possibleRoutesDtos.get(pos).getDepartureLatitude() + "," + possibleRoutesDtos.get(pos).getDepartureLongitude(),
                        possibleRoutesDtos.get(pos).getArrivalLatitude() + "," + possibleRoutesDtos.get(pos).getArrivalLongitude());
                call.enqueue(new Callback<GoogleMapsDto>() {
                    /**
                     * Successful HTTP response.
                     *
                     * @param response response from server
                     * @param retrofit adapter
                     */
                    @Override
                    public void onResponse(retrofit.Response<GoogleMapsDto> response, Retrofit retrofit) {
                        android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                        if (response.body() != null && response.isSuccess()) {
                            ArrayList<GoogleMapsDto.Routes> routes = response.body().routes;
                            if (routes != null && routes.size() > 0 && routes.get(0).legs != null && routes.get(0).legs.size() > 0) {
                                addMainRoute(possibleRoutesDtos.get(pos), routes.get(0).legs.get(0).duration.text, routes.get(0).legs.get(0).distance.text, (pos + 1),
                                        (pos == possibleRoutesDtos.size() - 1), isMain);
                                if (pos < possibleRoutesDtos.size()) {
                                    getMainRoutes(pos + 1, possibleRoutesDtos, isMain);
                                }
                            }
                        }
                    }

                    /**
                     * Invoked when a network or unexpected exception occurred during the HTTP request.
                     *
                     * @param t throwable error
                     */
                    @Override
                    public void onFailure(Throwable t) {
                        android.util.Log.e("", "FAILURE RESPONSE");
                        Utils.showProgress(false, mProgressBar, mProgressBGView);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getVehicleId(String selectedModel) {
        if (vehicleDatas != null && vehicleDatas.size() > 0) {
            for (int i = 0; i < vehicleDatas.size(); i++) {
                if (vehicleDatas.get(i).ModelName.equalsIgnoreCase(selectedModel)) {
                    return vehicleDatas.get(i).UserVehicleId;
                }
            }
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getUserVehicleModels();
    }

    private void getVehicleSeats(String vehicleId) {
        if (Utils.isInternetAvailable(mContext)) {
            selectSeatsSpinner.setEnabled(true);
            Utils.showProgress(true, mProgressBar, mProgressBGView);
            Call<Seats> call = Utils.getYatraShareAPI(mContext).getUserVehicleSeats(userGuid, vehicleId);
            call.enqueue(new Callback<Seats>() {
                /**
                 * Successful HTTP response.
                 *
                 * @param response response from server
                 * @param retrofit adapter
                 */
                @Override
                public void onResponse(retrofit.Response<Seats> response, Retrofit retrofit) {
                    android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                    ArrayList<String> vehicleSeats = new ArrayList<String>();
                    if (response.body() != null && response.isSuccess()) {
                        android.util.Log.e("SUCCEESS RESPONSE BODY", response.body() + "");
                        ArrayList<String> vehicleDatas = response.body().Data;
                        if (vehicleDatas != null && vehicleDatas.size() > 0) {
                            if (vehicleDatas.size() == 1) {
                                vehicleSeats.add("1");
                                selectSeatsSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleSeats));
                                selectSeatsSpinner.setEnabled(false);
                            } else {
                                vehicleSeats.add("Select Seats");
                                for (int i = 0; i < vehicleDatas.size(); i++) {
                                    if (!TextUtils.isEmpty(vehicleDatas.get(i))) {
                                        vehicleSeats.add(vehicleDatas.get(i));
                                    }
                                }
                            }
                        } else {
                            vehicleSeats.add("Select Seats");
                        }
                    } else {
                        vehicleSeats.add("Select Seats");
                    }
                    selectSeatsSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleSeats));
                    Utils.showProgress(false, mProgressBar, mProgressBGView);
                }

                /**
                 * Invoked when a network or unexpected exception occurred during the HTTP request.
                 *
                 * @param t throwable Error
                 */
                @Override
                public void onFailure(Throwable t) {
                    android.util.Log.e(TAG, "FAILURE RESPONSE");
                    Utils.showProgress(false, mProgressBar, mProgressBGView);
                    showSnackBar(getString(R.string.tryagain));
                }
            });
        }
    }

    private void getUserVehicleModels() {
        ArrayList<String> vehicleModels = new ArrayList<String>();
        vehicleModels.add("Select Model");
        selectModelSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleModels));
        if (Utils.isInternetAvailable(mContext)) {
            Utils.showProgress(true, mProgressBar, mProgressBGView);
            Call<Vehicle> call = Utils.getYatraShareAPI(mContext).getUserVehicleModels(userGuid, selectedVehicle);
            call.enqueue(new Callback<Vehicle>() {
                /**
                 * Successful HTTP response.
                 *
                 * @param response response from server
                 * @param retrofit adapter
                 */
                @Override
                public void onResponse(retrofit.Response<Vehicle> response, Retrofit retrofit) {
                    android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                    ArrayList<String> vehicleModels = new ArrayList<String>();
                    vehicleModels.add("Select Model");
                    if (response.body() != null && response.isSuccess()) {
                        android.util.Log.e("SUCCEESS RESPONSE BODY", response.body() + "");
                        vehicleDatas = response.body().Data;
                        if (vehicleDatas != null && vehicleDatas.size() > 0) {
                            for (int i = 0; i < vehicleDatas.size(); i++) {
                                if (!vehicleDatas.get(i).ModelName.isEmpty()) {
                                    vehicleModels.add(vehicleDatas.get(i).ModelName);
                                }
                            }
                        }
                    }
                    Utils.showProgress(false, mProgressBar, mProgressBGView);
                    selectModelSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleModels));
                }

                /**
                 * Invoked when a network or unexpected exception occurred during the HTTP request.
                 *
                 * @param t throwable Error
                 */
                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    Utils.showProgress(false, mProgressBar, mProgressBGView);
                    showSnackBar(getString(R.string.tryagain));
                }
            });
        }
    }

    public void showSnackBar(String msg) {
        try {
            Snackbar snack = Snackbar.make(selectModelSpinner, msg, Snackbar.LENGTH_LONG).setAction("Action", null);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(selectModelSpinner, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.selectModel:
                selectedModel = (String) parent.getAdapter().getItem(position);
                selectedVehicleId = getVehicleId(selectedModel);
                if (selectedVehicleId != null) {
                    getVehicleSeats(selectedVehicleId);
                }
                break;
            case R.id.selectSeats:
                seatsSelected = (String) parent.getAdapter().getItem(position);
                break;
            case R.id.timeFlexiSpinner:
                switch (position) {
                    case 0:
                    case 1:
                        selectedTimeFlexi = "15Mins";
                        break;
                    case 2:
                        selectedTimeFlexi = "30Mins";
                        break;
                    case 3:
                        selectedTimeFlexi = "1Hour";
                        break;
                    case 4:
                        selectedTimeFlexi = "2Hours";
                        break;
                }
                break;
            case R.id.detourSpinner:
                switch (position) {
                    case 0:
                    case 1:
                        selectedDetour = "NoDetour";
                        break;
                    case 2:
                        selectedDetour = "15MinsDetour";
                        break;
                    case 3:
                        selectedDetour = "30MinsDetou";
                        break;
                    case 4:
                        selectedDetour = "AnyDetour";
                        break;
                }
                break;
            case R.id.luggageSpinner:
                selectedLuggageSize = (String) parent.getAdapter().getItem(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
