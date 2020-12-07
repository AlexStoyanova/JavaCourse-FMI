package bg.sofia.uni.fmi.mjt.spotify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SpotifyExplorer {

    private final List<SpotifyTrack> tracks;
    private static final int MINUTE_TO_MILLISECONDS = 60_000;
    private static final String MESSAGE_FOR_ILLEGAL_ARGUMENT = "Argument is a negative number!";

    public SpotifyExplorer(Reader dataInput) {
        try (var bufferedReader = new BufferedReader(dataInput)) {
            bufferedReader.readLine();
            tracks = bufferedReader.lines().map(SpotifyTrack::of).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from the file", e);
        }
    }

    public Collection<SpotifyTrack> getAllSpotifyTracks() {
        return Collections.unmodifiableList(tracks);
    }


    public Collection<SpotifyTrack> getExplicitSpotifyTracks() {
        return tracks.stream()
                .filter(SpotifyTrack::explicit)
                .collect(Collectors.toUnmodifiableList());
    }


    public Map<Integer, Set<SpotifyTrack>> groupSpotifyTracksByYear() {
        return tracks.stream()
                .collect(Collectors.groupingBy(SpotifyTrack::year, Collectors.toSet()));
    }

    public int getArtistActiveYears(String artist) {
        if (tracks.isEmpty()
                || tracks.stream().noneMatch((t -> t.artists().contains(artist)))) {
            return 0;
        }
        int maxYear = tracks.stream()
                .filter(t -> t.artists().contains(artist))
                .max(Comparator.comparing(SpotifyTrack::year))
                .get()
                .year();
        int minYear = tracks.stream()
                .filter(t -> t.artists().contains(artist))
                .min(Comparator.comparing(SpotifyTrack::year))
                .get()
                .year();
        return (maxYear - minYear) + 1;
    }

    public List<SpotifyTrack> getTopNHighestValenceTracksFromThe80s(int n) {
        if (n < 0) {
            throw new IllegalArgumentException(MESSAGE_FOR_ILLEGAL_ARGUMENT);
        }
        return tracks.stream()
                .filter(t -> t.year() >= 1980 && t.year() <= 1989)
                .sorted(Comparator.comparing(SpotifyTrack::valence).reversed())
                .limit(n)
                .collect(Collectors.toUnmodifiableList());
    }

    public SpotifyTrack getMostPopularTrackFromThe90s() {
        return tracks.stream()
                .filter(t -> t.year() >= 1990 && t.year() <= 1999)
                .max(Comparator.comparing(SpotifyTrack::popularity))
                .orElseThrow(NoSuchElementException::new);
    }

    public long getNumberOfLongerTracksBeforeYear(int minutes, int year) {
        if (minutes < 0 || year < 0) {
            throw new IllegalArgumentException(MESSAGE_FOR_ILLEGAL_ARGUMENT);
        }
        return tracks.stream()
                .filter(t -> t.year() < year)
                .filter(t -> t.duration() > minutes * MINUTE_TO_MILLISECONDS)
                .count();
    }

    public Optional<SpotifyTrack> getTheLoudestTrackInYear(int year) {
        if (year < 0) {
            throw new IllegalArgumentException("Wrong year!");
        }
        return tracks.stream()
                .filter(t -> t.year() == year)
                .max(Comparator.comparing(SpotifyTrack::loudness));
    }

}