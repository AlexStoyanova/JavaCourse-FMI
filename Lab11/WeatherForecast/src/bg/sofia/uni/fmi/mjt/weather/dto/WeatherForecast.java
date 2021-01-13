package bg.sofia.uni.fmi.mjt.weather.dto;

import java.util.Arrays;
import java.util.Objects;

public class WeatherForecast {
    private WeatherCondition[] weather;
    private WeatherData main;

    public WeatherForecast(WeatherCondition[] weather, WeatherData main) {
        this.weather = weather;
        this.main = main;
    }

    public WeatherCondition[] getWeather() {
        return weather;
    }

    public WeatherData getMain() {
        return main;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WeatherForecast)) {
            return false;
        }

        WeatherForecast weatherForecast = (WeatherForecast) obj;
        return Arrays.equals(this.weather, weatherForecast.weather)
                && this.main.equals(weatherForecast.main);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weather, main);
    }
}
