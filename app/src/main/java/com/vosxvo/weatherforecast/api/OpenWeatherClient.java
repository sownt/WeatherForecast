package com.vosxvo.weatherforecast.api;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(ActivityComponent.class)
public class OpenWeatherClient {

    @Provides
    public OpenWeatherService getService(Retrofit retrofit) {
        return retrofit.create(OpenWeatherService.class);
    }

    @Provides
    public Retrofit getRetrofit(GsonConverterFactory factory, String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(factory)
                .build();
    }

    @Provides
    public GsonConverterFactory getConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Provides
    public String getBaseUrl() {
        return "http://api.openweathermap.org/";
    }

}
