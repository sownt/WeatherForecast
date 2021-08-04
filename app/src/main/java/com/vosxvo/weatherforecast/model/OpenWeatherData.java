package com.vosxvo.weatherforecast.model;

import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class OpenWeatherData {
    @SerializedName("coord")
    private OpenWeatherData.Coordinate coord;
    @SerializedName("weather")
    private OpenWeatherData.Weather[] weather;
    @SerializedName("main")
    private OpenWeatherData.Main main;
    @SerializedName("wind")
    private OpenWeatherData.Wind wind;
    @SerializedName("sys")
    private OpenWeatherData.Sys sys;
    @SerializedName("timezone")
    private int timezone;
    @SerializedName("name")
    private String name;

    /**
     * Contruct an {@link OpenWeatherData} object from a bundle structured by this
     *
     * @param bundle
     */
    public OpenWeatherData(Bundle bundle) {
        if (bundle != null) {

            coord = new Coordinate();
            weather = new Weather[1];
            main = new Main();
            wind = new Wind();
            sys = new Sys();

            coord.setLatitude(bundle.getDouble("coord.lat"));
            coord.setLongitude(bundle.getDouble("coord.lon"));
            weather[0].setMain(bundle.getString("weather.main"));
            weather[0].setDescription(bundle.getString("weather.description"));
            main.setTemp(bundle.getDouble("main.temp"));
            main.setFeelsLike(bundle.getDouble("main.feels_like"));
            main.setHumidity(bundle.getInt("main.humidity"));
            main.setTempMin(bundle.getDouble("main.temp_min"));
            main.setTempMax(bundle.getDouble("main.temp_max"));
            wind.setSpeed(bundle.getDouble("wind.speed"));
            wind.setDeg(bundle.getDouble("wind.deg"));
            sys.setCountry(bundle.getString("sys.country"));
            sys.setSunrise(bundle.getLong("sys.sunrise"));
            sys.setSunset(bundle.getLong("sys.sunset"));
            timezone = bundle.getInt("timezone");
            name = bundle.getString("name");
        }
    }

    /**
     * Convert weather data from {@link OpenWeatherData} to {@link Bundle}
     *
     * @return weather data {@link Bundle}
     */
    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putDouble("coord.lat", getCoord().getLatitude());
        bundle.putDouble("coord.lon", getCoord().getLongitude());
        bundle.putString("weather.main", getWeather()[0].getMain());
        bundle.putString("weather.description", getWeather()[0].getDescription());
        bundle.putDouble("main.temp", getMain().getTemp());
        bundle.putDouble("main.feels_like", getMain().getFeelsLike());
        bundle.putInt("main.humidity", getMain().getHumidity());
        bundle.putDouble("main.temp_min", getMain().getTempMin());
        bundle.putDouble("main.temp_max", getMain().getTempMax());
        bundle.putDouble("wind.speed", getWind().getSpeed());
        bundle.putDouble("wind.deg", getWind().getDeg());
        bundle.putString("sys.country", getSys().getCountry());
        bundle.putLong("sys.sunrise", getSys().getSunrise() + getTimezone());
        bundle.putLong("sys.sunset", getSys().getSunset() + getTimezone());
        bundle.putInt("timezone", getTimezone());
        bundle.putString("name", getName());

        return bundle;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Coordinate {
        @SerializedName("lat")
        private double latitude;
        @SerializedName("lon")
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

    public static class Weather {
        @SerializedName("main")
        private String main;
        @SerializedName("description")
        private String description;

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
    public static class Main {
        @SerializedName("temp")
        private double temp;
        @SerializedName("feels_like")
        private double feelsLike;
        @SerializedName("humidity")
        private int humidity;
        @SerializedName("temp_min")
        private double tempMin;
        @SerializedName("temp_max")
        private double tempMax;

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public double getFeelsLike() {
            return feelsLike;
        }

        public void setFeelsLike(double feelsLike) {
            this.feelsLike = feelsLike;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public double getTempMin() {
            return tempMin;
        }

        public void setTempMin(double tempMin) {
            this.tempMin = tempMin;
        }

        public double getTempMax() {
            return tempMax;
        }

        public void setTempMax(double tempMax) {
            this.tempMax = tempMax;
        }
    }
    public static class Wind {
        @SerializedName("speed")
        private double speed;
        @SerializedName("deg")
        private double deg;

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public double getDeg() {
            return deg;
        }

        public void setDeg(double deg) {
            this.deg = deg;
        }
    }
    public static class Sys {
        @SerializedName("country")
        private String country;
        @SerializedName("sunrise")
        private long sunrise;
        @SerializedName("sunset")
        private long sunset;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public long getSunrise() {
            return sunrise;
        }

        public void setSunrise(long sunrise) {
            this.sunrise = sunrise;
        }

        public long getSunset() {
            return sunset;
        }

        public void setSunset(long sunset) {
            this.sunset = sunset;
        }
    }
}
