package com.vosxvo.weatherforecast.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.vosxvo.weatherforecast.model.OpenWeatherData;

public class PreferencesHelper {
    public static final String PREFERENCES = "com.vosxvo.weatherforecast.PREFERENCES";
    private SharedPreferences preferences;
    private Context context;
    private int mode;

    public PreferencesHelper(Context context, int mode) {
        this.context = context;
        this.mode = mode;
        preferences = context.getSharedPreferences(PREFERENCES, mode);
    }

    /**
     * Check if application uses current location
     *
     * @return true if using current location, false if use fixed location stored before.
     */
    public boolean usesLocation() {
        if (preferences == null) return true;
        return preferences.getBoolean("uses_location", true);
    }

    public OpenWeatherData getWeather() {
        return new OpenWeatherData(getPreferences());
    }

    /**
     * Get weather data from {@link SharedPreferences} com.vosxvo.weatherforecast.PREFERENCES and
     * put it into a bundle
     *
     * @return  bundle stored weather data
     */
    public Bundle getPreferences() {
        if (preferences == null) return null;

        Bundle bundle = new Bundle();
        bundle.putBoolean("uses_location", preferences.getBoolean("uses_location", false));
        if (bundle.getBoolean("uses_location")) return bundle;

        bundle.putDouble("coord.lat", Double.parseDouble(preferences.getString("coord.lat", "0.0")));
        bundle.putDouble("coord.lon", Double.parseDouble(preferences.getString("coord.lon", "0.0")));
        bundle.putString("weather.main", preferences.getString("weather.main", "N/A"));
        bundle.putString("weather.description", preferences.getString("weather.description", "N/A"));
        bundle.putDouble("main.temp", Double.parseDouble(preferences.getString("main.temp", "N/A")));
        bundle.putDouble("main.feels_like", Double.parseDouble(preferences.getString("main.feels_like", "0.0")));
        bundle.putInt("main.humidity", preferences.getInt("main.humidity", 0));
        bundle.putDouble("main.temp_min", Double.parseDouble(preferences.getString("main.temp_min", "0.0")));
        bundle.putDouble("main.temp_max", Double.parseDouble(preferences.getString("main.temp_max", "0.0")));
        bundle.putDouble("wind.speed", Double.parseDouble(preferences.getString("wind.speed", "0.0")));
        bundle.putDouble("wind.deg", Double.parseDouble(preferences.getString("wind.deg", "0.0")));
        bundle.putString("sys.country", preferences.getString("sys.country", "N/A"));
        bundle.putLong("sys.sunrise", preferences.getLong("sys.sunrise", 0));
        bundle.putLong("sys.sunset", preferences.getLong("sys.sunset", 0));
        bundle.putInt("timezone", preferences.getInt("timezone", 0));
        bundle.putString("name", preferences.getString("name", "N/A"));

        return bundle;
    }

    /**
     * Save weather data to {@link SharedPreferences} called com.vosxvo.weatherforecast.PREFERENCES
     * from {@link Bundle} data.
     *
     * @param data {@link Bundle} data structured by {@link }
     */
    public void savePreferences(Bundle data) {
        if (preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("uses_location", data.getBoolean("uses_location"));
        if (data.getBoolean("uses_location")) {
            editor.apply();
            return;
        }

        editor.putString("coord.lat", String.valueOf(data.getDouble("coord.lat")));
        editor.putString("coord.lon", String.valueOf(data.getDouble("coord.lon")));
        editor.putString("weather.main", data.getString("weather.main"));
        editor.putString("weather.description", data.getString("weather.description"));
        editor.putString("main.temp", String.valueOf(data.getDouble("main.temp")));
        editor.putString("main.feels_like", String.valueOf(data.getDouble("main.feels_like")));
        editor.putInt("main.humidity", data.getInt("main.humidity"));
        editor.putString("main.temp_min", String.valueOf(data.getDouble("main.temp_min")));
        editor.putString("main.temp_max", String.valueOf(data.getDouble("main.temp_max")));
        editor.putString("wind.speed", String.valueOf(data.getDouble("wind.speed")));
        editor.putString("wind.deg", String.valueOf(data.getDouble("wind.deg")));
        editor.putString("sys.country", data.getString("sys.country"));
        editor.putLong("sys.sunrise", data.getLong("sys.sunrise"));
        editor.putLong("sys.sunset", data.getLong("sys.sunset"));
        editor.putInt("timezone", data.getInt("timezone"));
        editor.putString("name", data.getString("name"));

        editor.apply();
    }

    public void close() {
        context = null;
        preferences = null;
    }
}
