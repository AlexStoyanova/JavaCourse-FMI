package bg.sofia.uni.fmi.mjt.weather;

import bg.sofia.uni.fmi.mjt.weather.dto.WeatherCondition;
import bg.sofia.uni.fmi.mjt.weather.dto.WeatherForecast;
import bg.sofia.uni.fmi.mjt.weather.exceptions.LocationNotFoundException;
import bg.sofia.uni.fmi.mjt.weather.exceptions.WeatherForecastClientException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;

public class WeatherForecastClient {

    private static final String CITY_NOT_FOUND_MESSAGE = "The city is not found!";
    private static final String INFORMATION_COULD_NOT_BE_RETRIEVED_MESSAGE =
            "Information regarding the weather for this location could not be retrieved!";
    private static final String API_KEY = "3ab932ab85d69d2404b47c883fa411e2";
    private final HttpClient weatherHttpClient;

    public WeatherForecastClient(HttpClient weatherHttpClient) {
        this.weatherHttpClient = weatherHttpClient;
    }

    public WeatherForecast getForecast(String city) throws WeatherForecastClientException {
        String processedCity = city.trim().replaceAll(" ", "%20");
        Gson gson = new Gson();

        try {
            String URL = "http://api.openweathermap.org/data/2.5/weather?q="
                    + processedCity + "&units=metric&lang=bg&appid=" + API_KEY;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .build();

            HttpResponse<String> response = weatherHttpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new LocationNotFoundException(CITY_NOT_FOUND_MESSAGE);
            }

            return gson.fromJson(response.body(), WeatherForecast.class);

        } catch (IOException | InterruptedException e) {
            throw new WeatherForecastClientException(INFORMATION_COULD_NOT_BE_RETRIEVED_MESSAGE);
        }
    }
}