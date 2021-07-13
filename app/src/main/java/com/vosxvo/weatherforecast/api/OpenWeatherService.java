package com.vosxvo.weatherforecast.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherService {
    @GET("data/2.5/weather")
    Call<OpenWeatherRespond> getOpenWeatherRespond(@Query("lat") double latitude,
                                                   @Query("lon") double longitude,
                                                   @Query("units") String units,
                                                   @Query("appid") String appid);
}
