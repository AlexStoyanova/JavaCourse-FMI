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

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;

import static org.junit.Assert.assertArrayEquals;
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
        WeatherCondition[] weather = new WeatherCondition[1];
        weather[0] = new WeatherCondition("слънчево");
        WeatherForecast weatherForecast = new WeatherForecast(weather, main);

        String json = new Gson().toJson(weatherForecast);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(json);

        WeatherForecast result = weatherForecastClient.getForecast("Sofia");
        assertEquals(result.getMain(), main);
        assertArrayEquals(result.getWeather(), weather);
    }

    @Test(expected = LocationNotFoundException.class)
    public void testGetForestWithNoSuchCity() throws Exception {
        when(httpClientMock.send(Mockito.any(HttpRequest.class),
                ArgumentMatchers.<BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        when(httpResponseMock.statusCode()).thenReturn(404);
        weatherForecastClient.getForecast("Something");
    }
}
