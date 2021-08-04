package com.vosxvo.weatherforecast;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vosxvo.weatherforecast.api.OpenWeatherService;
import com.vosxvo.weatherforecast.model.OpenWeatherData;
import com.vosxvo.weatherforecast.preferences.PreferencesHelper;
import com.vosxvo.weatherforecast.ui.MainWeatherFragment;
import com.vosxvo.weatherforecast.ui.MoreWeatherFragment;
import com.vosxvo.weatherforecast.ui.SavedLocationFragment;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * <h1>Launcher Activity</h1>
 *
 * @author vosxvo (Thai Son)
 * @version 1.0
 */

@AndroidEntryPoint
public class MainActivity extends FragmentActivity {
    private static final String OpenWeather_API_KEY = BuildConfig.OpenWeatherApiKey;
    private static final int NUM_PAGES = 3;
    private static final long MIN_TIME_INTERVAL = 10000;
    private static final int REQUEST_CHECK_SETTINGS = 10000;
    private static final int REQUEST_PERMISSION_SETTINGS = 10001;

    public static final String MAIN_WEATHER_UPDATE = "MAIN_WEATHER";
    public static final String MORE_WEATHER_UPDATE = "MORE_WEATHER";
    public static final String SAVED_LOCATION_UPDATE = "SAVED_LOCATION";
    public static final String UPDATE_UI_REQUEST_KEY = "UPDATE_UI";
    public static final String GET_WEATHER_REQUEST_KEY = "GET_WEATHER";

    private ViewPager2 pager;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient locationProviderClient;
    private PreferencesHelper preferencesHelper;

    @Inject
    public OpenWeatherService service;

    private Callback<OpenWeatherData> updateCallback = new Callback<OpenWeatherData>() {
        @Override
        public void onResponse(@NotNull Call<OpenWeatherData> call,
                               @NotNull Response<OpenWeatherData> response) {
            OpenWeatherData result = response.body();
            if (result == null) return;
            updateData(MAIN_WEATHER_UPDATE, result.toBundle());
            updateData(MORE_WEATHER_UPDATE, result.toBundle());
        }

        @Override
        public void onFailure(@NotNull Call<OpenWeatherData> call, @NotNull Throwable t) {
            Log.e("Retrofit", t.getMessage());
            // Get weather data from Shared Preferences
            updateData(MAIN_WEATHER_UPDATE, preferencesHelper.getPreferences());
            updateData(MORE_WEATHER_UPDATE, preferencesHelper.getPreferences());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = findViewById(R.id.pager);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        FragmentStateAdapter adapter = new ScreenSlidePagerAdapter(this);
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);
        pager.setOffscreenPageLimit(2);

        TabLayout tabLayout = findViewById(R.id.tabDots);
        new TabLayoutMediator(tabLayout, pager, (tab, position) -> {
        }).attach();

        preferencesHelper = new PreferencesHelper(this, MODE_PRIVATE);
        updateWeather();

        getSupportFragmentManager().setFragmentResultListener(UPDATE_UI_REQUEST_KEY, this,
                (requestKey, result) -> updateWeather());

        getSupportFragmentManager().setFragmentResultListener(GET_WEATHER_REQUEST_KEY, this,
                (requestKey, result) -> getWeather(
                        result.getDouble("lat", 0.0),
                        result.getDouble("lon", 0.0),
                        new Callback<OpenWeatherData>() {
                            @Override
                            public void onResponse(Call<OpenWeatherData> call, Response<OpenWeatherData> response) {
                                OpenWeatherData result = response.body();
                                if (result == null) return;
                                updateData(MAIN_WEATHER_UPDATE, result.toBundle());
                                updateData(MORE_WEATHER_UPDATE, result.toBundle());
                                updateData(SAVED_LOCATION_UPDATE, result.toBundle());
                            }

                            @Override
                            public void onFailure(Call<OpenWeatherData> call, Throwable t) {

                            }
                        }));
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            pager.setCurrentItem(1);
        } else if (pager.getCurrentItem() == 1) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    public void updateWeather() {
        if (preferencesHelper.usesLocation()) {
            // if "uses_location" is true or it doesn't init, get coordinate from Fused Location Provider
            // before, we need request location permission
            requestLocationPermissions();
        } else {
            Bundle bundle = preferencesHelper.getPreferences();
            getWeather(
                    bundle.getDouble("coord.lat"),
                    bundle.getDouble("coord.lon"),
                    updateCallback
            );
        }
    }

    public void getWeather(Location location, Callback<OpenWeatherData> callback) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        getWeather(lat, lon, callback);
    }

    public void getWeather(double lat, double lon, Callback<OpenWeatherData> callback) {
        Call<OpenWeatherData> call = service
                .getOpenWeatherResponse(lat, lon, "metric", OpenWeather_API_KEY);

        call.enqueue(callback);
    }

    public void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {   // Not have location permission, request it

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_SETTINGS);
            }

        } else {    // Permission already, check location settings
            requestLocationSettings();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions,
                                           @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /**
         * Handle request permission response. If permission granted, check location settings and
         * request change (if needed). The first time user denied permision, request try again in
         * {@link Snackbar}. If user denied it forever, request user go to settings to change this.
         */
        if (requestCode == REQUEST_PERMISSION_SETTINGS) {   // handle requestLocationPermissions()
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationSettings();  // Permission granted, call requestLocationSettings()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Snackbar.make(pager, "Permission Denied.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Try again", v -> {
                            requestLocationPermissions();
                        }).show();  // Alert "Permission denied.", click "Try again" to request
            } else {
                Snackbar.make(pager, "Permission Denied.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Fix", v -> {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts(
                                    "package",
                                    this.getPackageName(),
                                    null
                            ));
                            startActivity(intent);
                        }).show();  // Alert "Permission denied.", click "Fix" to go Settings
            }
        }
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(MIN_TIME_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void requestLocationSettings() {
        // Check and request Location Setting
        createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            // Change settings successful, now get location from GPS_PROVIDER
            updateLocation();
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                try {
                    resolvableApiException.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        });
    }

    public void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Location> task = locationProviderClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                new CancellationToken() {
                    @Override
                    public boolean isCancellationRequested() {
                        return false;
                    }

                    @NonNull
                    @NotNull
                    @Override
                    public CancellationToken onCanceledRequested(@NonNull @NotNull OnTokenCanceledListener onTokenCanceledListener) {
                        return null;
                    }
                });

        task.addOnSuccessListener(this, location -> getWeather(location, updateCallback));
        task.addOnFailureListener(this, e -> {
            Log.e("Location", e.getMessage());
            Bundle bundle = preferencesHelper.getPreferences();
            getWeather(
                    bundle.getDouble("coord.lat"),
                    bundle.getDouble("coord.lon"),
                    updateCallback
            );
        });
    }

    public void updateData(String requestKey, Bundle result) {
        getSupportFragmentManager().setFragmentResult(requestKey, result);
    }

    // ViewPager2 adapter
    private static class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(@NonNull
                                       @org.jetbrains.annotations.NotNull
                                               FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @NotNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 1) {
                return new MainWeatherFragment();
            } else if (position == 2){
                return new MoreWeatherFragment();
            } else if (position == 0) {
                return new SavedLocationFragment();
            }
            return new MainWeatherFragment();
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}