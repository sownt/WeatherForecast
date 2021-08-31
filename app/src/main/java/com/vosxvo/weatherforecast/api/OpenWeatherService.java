package com.vosxvo.weatherforecast.api;

import com.vosxvo.weatherforecast.model.OpenWeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherService {
    /**
     *
     * @param latitude
     * @param longitude
     * @param units
     * @param appid
     * @return
     */
    @GET("data/2.5/weather")
    Call<OpenWeatherData> getOpenWeatherResponse(@Query("lat") double latitude,
                                                 @Query("lon") double longitude,
                                                 @Query("units") String units,
                                                 @Query("appid") String appid);
}
