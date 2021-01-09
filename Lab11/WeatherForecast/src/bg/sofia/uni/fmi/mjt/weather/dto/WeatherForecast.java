package bg.sofia.uni.fmi.mjt.weather.dto;

import java.util.List;

public class WeatherForecast {
    private List<WeatherCondition> weather;
    private WeatherData main;

    public WeatherForecast(List<WeatherCondition> weather, WeatherData main) {
        this.weather = weather;
        this.main = main;
    }

    public List<WeatherCondition> getWeather() {
        return weather;
    }

    public WeatherData getMain() {
        return main;
    }
}
