package bg.sofia.uni.fmi.mjt.weather.dto;

public class WeatherData {
    private double temp;
    private double feels_like;

    public WeatherData(double temp, double feels_like) {
        this.temp = temp;
        this.feels_like = feels_like;
    }

    public double getTemp() {
        return temp;
    }

    public double getFeels_like() {
        return feels_like;
    }
}
