package com.ludoscity.findmybikes.data.network.citybik_es;

import com.ludoscity.findmybikes.common.presentation.data.datasource.remote.citybik_es.BikeSystemListAnswerRoot;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by F8Full on 2015-09-29.
 * Retrofit interface to access https://api.citybik.es/v2
 */
public interface Citybik_esAPI {

    //Endpoint : //https://api.citybik.es

    //https://api.citybik.es/v2/networks/bixi-montreal?fields=stations
    @GET("{href}")
    Call<BikeSystemStatusAnswerRoot> getBikeNetworkStatus(@Path("href") String href, @QueryMap Map<String, String> options);

    //https://api.citybik.es/v2/networks/
    @GET("/v2/networks")
    Call<BikeSystemListAnswerRoot> getBikeNetworkList();
}


