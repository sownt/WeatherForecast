package com.vosxvo.weatherforecast.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MoreWeatherFragment extends Fragment implements WeatherFragment, SwipeRefreshLayout.OnRefreshListener {
    private TextView geoLocation;
    private TextView currentWeather;
    private TextView temperature;
    private TextView temperatureMinMax;
    private TextView temperatureFeelsLike;
    private TextView humidity;
    private TextView speed;
    private TextView deg;
    private TextView sunrise;
    private TextView sunset;
    private SwipeRefreshLayout moreWeatherLayout;
    private ShimmerFrameLayout frameLayout;
    private ConstraintLayout mainLayout;
    private Bundle bundle;

    public MoreWeatherFragment() {
        super(R.layout.fragment_more_weather);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        geoLocation = view.findViewById(R.id.geo_location2);
        currentWeather = view.findViewById(R.id.current_weather);
        temperature = view.findViewById(R.id.temperature);
        temperatureMinMax = view.findViewById(R.id.temperature_minmax);
        temperatureFeelsLike = view.findViewById(R.id.temperature_feels_like);
        moreWeatherLayout = view.findViewById(R.id.more_weather_layout);
        humidity = view.findViewById(R.id.humidity);
        speed = view.findViewById(R.id.speed);
        deg = view.findViewById(R.id.direction);
        sunrise = view.findViewById(R.id.sunrise);
        sunset = view.findViewById(R.id.sunset);
        frameLayout = view.findViewById(R.id.more_weather_shimmer);
        mainLayout = view.findViewById(R.id.more_weather_data);

        moreWeatherLayout.setOnRefreshListener(this);
        startLoading();

        getParentFragmentManager().setFragmentResultListener(MainActivity.MORE_WEATHER_UPDATE,
                this, (requestKey, result) -> {
                    bundle = result;
                    updateUI(result);
                });
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if (bundle != null) updateUI(bundle);
    }

    @Override
    public void onRefresh() {
        getParentFragmentManager().setFragmentResult(MainActivity.UPDATE_UI_REQUEST_KEY, new Bundle());
    }

    @Override
    public void updateUI(Bundle bundle) {
        geoLocation.setText(bundle.getString("name"));
        currentWeather.setText(bundle.getString("weather.description"));
        temperature.setText(String.format("%.0f°C", bundle.getDouble("main.temp")));
        temperatureMinMax.setText(String.format("%.0f°C / %.0f°C", bundle.getDouble("main.temp_min"),
                bundle.getDouble("main.temp_max")));
        temperatureFeelsLike.setText(String.format("%.0f°C", bundle.getDouble("main.feels_like")));
        humidity.setText(String.format("%d%%", bundle.getInt("main.humidity")));
        speed.setText(String.format("%.1f m/s", bundle.getDouble("wind.speed")));
        deg.setText(String.format("%s", degToCompass(bundle.getDouble("deg"))));

        SimpleDateFormat dateFormat = new SimpleDateFormat("K:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date rise = new Date((long) bundle.getLong("sys.sunrise") * 1000);
        sunrise.setText(dateFormat.format(rise));
        Date set = new Date((long) bundle.getLong("sys.sunset") * 1000);
        sunset.setText(dateFormat.format(set));

        moreWeatherLayout.setRefreshing(false);
        stopLoading();
    }

    public String degToCompass(double deg) {
        String[] dir = {"North","North - Northeast","Northeast","East - Northeast","East",
                "East - Southeast", "Southeast", "South - Southeast","South","South - Southwest",
                "Southwest","West - Southwest","West","West - Northwest","Northwest","North - Northwest"};
        return dir[(int) ((deg / 22.5) + 0.5) % 16];
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
        animation.setDuration(500);
        animation.setRepeatCount(0);
        animation.setRepeatMode(Animation.REVERSE);
        mainLayout.startAnimation(animation);
    }
}
