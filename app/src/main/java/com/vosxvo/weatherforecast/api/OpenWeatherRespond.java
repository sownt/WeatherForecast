package com.vosxvo.weatherforecast.api;

import com.google.gson.annotations.SerializedName;

public class OpenWeatherRespond {
    @SerializedName("name")
    private String name;
    @SerializedName("sys")
    private Sys sys;
    @SerializedName("weather")
    private Weather[] weather;
    @SerializedName("main")
    private Main main;

    public String getName() {
        return name;
    }

    public Sys getSys() {
        return sys;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    @Override
    public String toString() {
        return name.toString() + ", " + sys.toString() + "\n" +
                weather[0].toString() + main.toString();
    }

    public class Weather {
        @SerializedName("main")
        private String main;
        @SerializedName("description")
        private String description;

        public String getMain() {
            return main;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return main + description + "\n";
        }
    }
    public class Main {
        @SerializedName("temp")
        private double temp;
        @SerializedName("temp_min")
        private double tempMin;
        @SerializedName("temp_max")
        private double tempMax;

        public double getTemp() {
            return temp;
        }

        public double getTempMin() {
            return tempMin;
        }

        public double getTempMax() {
            return tempMax;
        }

        @Override
        public String toString() {
            return temp + " " + tempMin + " / " + tempMax + "\n";
        }
    }
    public class Sys {
        @SerializedName("country")
        private String country;

        public String getCountry() {
            return country;
        }

        @Override
        public String toString() {
            return country;
        }
    }
}
