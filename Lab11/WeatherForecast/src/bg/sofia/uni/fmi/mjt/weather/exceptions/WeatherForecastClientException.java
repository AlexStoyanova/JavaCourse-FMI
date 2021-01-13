package bg.sofia.uni.fmi.mjt.weather.exceptions;

public class WeatherForecastClientException extends Exception {
    public WeatherForecastClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeatherForecastClientException(String message) {
        super(message);
    }
}
