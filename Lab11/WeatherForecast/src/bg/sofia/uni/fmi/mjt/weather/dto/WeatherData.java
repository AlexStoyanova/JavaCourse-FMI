package bg.sofia.uni.fmi.mjt.weather.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class WeatherData {
    private double temp;

    @SerializedName("feels_like")
    private double feelsLike;

    public WeatherData(double temp, double feelsLike) {
        this.temp = temp;
        this.feelsLike = feelsLike;
    }

    public double getTemp() {
        return temp;
    }

    public double getFeels_like() {
        return feelsLike;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WeatherData)) {
            return false;
        }

        WeatherData data = (WeatherData) obj;
        return this.temp == data.temp && this.feelsLike == data.feelsLike;
    }

    @Override
    public int hashCode() {
        return Objects.hash(temp, feelsLike);
    }
}
