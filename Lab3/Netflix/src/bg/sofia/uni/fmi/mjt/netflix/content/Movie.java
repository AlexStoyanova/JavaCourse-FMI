package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;

public final class Movie extends Film
{
    private int duration;

    public Movie(String name, Genre genre, PgRating rating, int duration)
    {
        super(name, genre, rating);
        this.duration = duration;
    }

    public int getDuration()
    {
        return duration;
    }
}
