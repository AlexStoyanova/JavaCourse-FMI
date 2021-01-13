package bg.sofia.uni.fmi.mjt.weather.dto;

import java.util.Objects;

public class WeatherCondition {
    private String description;

    public WeatherCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WeatherCondition)) {
            return false;
        }

        WeatherCondition condition = (WeatherCondition) obj;
        return this.description.equals(condition.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(description);
    }
}
