package com.trackmapoop.web;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by mike on 8/13/14.
 */
public interface BRWebService {

    @GET("/amenimapi.php")
    NearestBathroomsResponse getNearestBathrooms(@Query("amenity")String amenity, @Query("mylat")double mylat, @Query("mylon")double mylon, @Query("mode")String mode, @Query("name")String name, @Query("key")String key);
}
