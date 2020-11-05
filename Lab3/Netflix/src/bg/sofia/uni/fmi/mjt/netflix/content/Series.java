package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;

public final class Series extends Film {
    private Episode[] episodes;

    public Series(String name, Genre genre, PgRating rating, Episode[] episodes) {
        super(name, genre, rating);
        this.episodes = episodes;
    }

    public int getDuration() {
        int totalDuration = 0;

        for (int i = 0; i < episodes.length; ++i) {
            totalDuration += episodes[i].duration();
        }

        return totalDuration;
    }
}
