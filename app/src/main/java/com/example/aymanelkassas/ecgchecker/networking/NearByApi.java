package com.example.aymanelkassas.ecgchecker.networking;

import com.example.aymanelkassas.ecgchecker.model.NearByApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Parth Dave on 31/3/17.
 * Spaceo Technologies Pvt Ltd.
 * parthd.spaceo@gmail.com
 */

public interface NearByApi {
    
    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyCu0V0psfBPz8aWI27DrBNPnFD88J-REzc")
    Call<NearByApiResponse> getNearbyPlaces(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);
}
