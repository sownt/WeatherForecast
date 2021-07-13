package com.vosxvo.weatherforecast.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.vosxvo.weatherforecast.MainActivity;
import com.vosxvo.weatherforecast.R;
import com.vosxvo.weatherforecast.api.OpenWeatherService;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainWeatherFragment extends Fragment implements WeatherFragment, SwipeRefreshLayout.OnRefreshListener {
    private TextView geoLocation;
    private TextView temp;
    private TextView description;
    private TextView tempMin;
    private TextView tempMax;
    private SwipeRefreshLayout mainWeatherLayout;
    private Bundle bundle;
    private ShimmerFrameLayout frameLayout;
    private ConstraintLayout mainLayout;

    @Inject
    public OpenWeatherService service;

    public MainWeatherFragment() {
        super(R.layout.fragment_main_weather);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view,
                              @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        geoLocation = view.findViewById(R.id.geo_location);
        temp = view.findViewById(R.id.temp);
        description = view.findViewById(R.id.description);
        tempMin = view.findViewById(R.id.min);
        tempMax = view.findViewById(R.id.max);
        mainWeatherLayout = view.findViewById(R.id.main_weather_layout);
        frameLayout = view.findViewById(R.id.main_weather_shimmer);
        mainLayout = view.findViewById(R.id.main_weather_data);

        mainWeatherLayout.setOnRefreshListener(this);
        startLoading();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if (bundle != null) updateUI(bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        getParentFragmentManager().setFragmentResultListener(MainActivity.MAIN_WEATHER_REQUEST_KEY,
                this, (requestKey, result) -> {
            bundle = result;
            updateUI(result);
        });
    }

    @Override
    public void onRefresh() {
        ((MainActivity) getActivity()).updateLocation();
    }

    @Override
    public void updateUI(Bundle bundle) {
        geoLocation.setText(bundle.getString("geoLocation"));
        temp.setText(String.format("%.0f°C", bundle.getDouble("temp")));
        description.setText(bundle.getString("main"));
        tempMin.setText(String.format("%.0f°C", bundle.getDouble("tempMin")));
        tempMax.setText(String.format(" / %.0f°C", bundle.getDouble("tempMax")));
        mainWeatherLayout.setRefreshing(false);

        stopLoading();
    }

    public void startLoading() {
        mainLayout.setVisibility(View.INVISIBLE);
        frameLayout.setVisibility(View.VISIBLE);
        frameLayout.startShimmer();
    }

    public void stopLoading() {
        frameLayout.stopShimmer();
        frameLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);

        startFade();
    }

    public void startFade() {
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(1000);
        animation.setRepeatCount(0);
        animation.setRepeatMode(Animation.REVERSE);
        mainLayout.startAnimation(animation);
    }
}