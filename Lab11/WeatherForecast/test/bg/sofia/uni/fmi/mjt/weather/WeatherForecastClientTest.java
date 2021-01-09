package bg.sofia.uni.fmi.mjt.weather;

import bg.sofia.uni.fmi.mjt.weather.dto.WeatherCondition;
import bg.sofia.uni.fmi.mjt.weather.dto.WeatherData;
import bg.sofia.uni.fmi.mjt.weather.dto.WeatherForecast;
import bg.sofia.uni.fmi.mjt.weather.exceptions.LocationNotFoundException;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeatherForecastClientTest {

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private WeatherForecastClient weatherForecastClient;

    @Before
    public void setClient() {
        weatherForecastClient = new WeatherForecastClient(httpClientMock);
    }

    @Test
    public void testGetForecastGetCorrectData() throws Exception {
        when(httpClientMock.send(Mockito.any(HttpRequest.class),
                ArgumentMatchers.<BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        WeatherData main = new WeatherData(25.5, 28.0);
        List<WeatherCondition> weather = new LinkedList<>();
        weather.add(new WeatherCondition("слънчево"));
        WeatherForecast weatherForecast = new WeatherForecast(weather, main);

        String json = new Gson().toJson(weatherForecast);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(json);

        WeatherForecast result = weatherForecastClient.getForecast("Sofia");
        assertEquals( 25.5, result.getMain().getTemp(), 0.01);
        assertEquals(28.0, result.getMain().getFeels_like(), 0.01);
        assertEquals("слънчево", result.getWeather().get(0).getDescription());
    }

    @Test(expected = LocationNotFoundException.class)
    public void testGetForestWithNoSuchCity() throws Exception {
        when(httpClientMock.send(Mockito.any(HttpRequest.class),
                ArgumentMatchers.<BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        when(httpResponseMock.statusCode()).thenReturn(404);
        WeatherForecast result = weatherForecastClient.getForecast("");
    }



}
