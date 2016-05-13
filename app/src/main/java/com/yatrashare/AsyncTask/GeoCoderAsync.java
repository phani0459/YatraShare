package com.yatrashare.AsyncTask;

import com.yatrashare.dtos.GoogleAddressDto;
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.pojos.RideInfoDto;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by pkandagatla on 22/4/16.
 */
public class GeoCoderAsync {

    private void getAddressfromAPI(final int pos, final ArrayList<RideInfoDto.PossibleRoutesDto> possibleRoutesDtos, final boolean isMain) {
        if (possibleRoutesDtos != null && possibleRoutesDtos.size() > 0) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://maps.googleapis.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(Utils.getOkHttpClient())
                        .build();
                YatraShareAPI yatraShareAPI = retrofit.create(YatraShareAPI.class);
                Call<GoogleAddressDto> call = yatraShareAPI.getGoogleAddressAPI(possibleRoutesDtos.get(pos).getDepartureLatitude() + "," + possibleRoutesDtos.get(pos).getDepartureLongitude());
                call.enqueue(new Callback<GoogleAddressDto>() {
                    /**
                     * Successful HTTP response.
                     *
                     * @param response response from server
                     * @param retrofit adapter
                     */
                    @Override
                    public void onResponse(retrofit.Response<GoogleAddressDto> response, Retrofit retrofit) {
                        android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                        if (response.body() != null && response.isSuccess()) {
                            ArrayList<GoogleAddressDto.AddressResults> results = response.body().results;
                            if (results != null && results.size() > 0 && results.get(0).address_components != null && results.get(0).address_components.size() > 0) {
                                /**
                                 * administrative_area_level_1 = State
                                 * locality = City
                                 * country = Country
                                 */

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
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
