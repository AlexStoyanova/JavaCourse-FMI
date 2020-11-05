package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;


public sealed abstract class Film implements Streamable permits Movie, Series {
    private String name;
    private Genre genre;
    private PgRating rating;

    public Film(String name, Genre genre, PgRating rating) {
        this.name = name;
        this.genre = genre;
        this.rating = rating;
    }

    public String getTitle() {
        return name;
    }

    public abstract int getDuration();

    public PgRating getRating() {
        return rating;
    }

}
