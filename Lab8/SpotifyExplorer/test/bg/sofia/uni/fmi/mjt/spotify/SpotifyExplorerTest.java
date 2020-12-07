package bg.sofia.uni.fmi.mjt.spotify;

import java.io.StringReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpotifyExplorerTest {

    private static final String FIRST_LINE = "id,artists,name,year,popularity,duration_ms,tempo,"
            + "loudness,valence,acousticness,danceability,energy,liveness,speechiness,explicit";
    private static final String BASIC_TRACK1 = "4BJqT0PrAfrxzMOxytFOIz,['Sergei Rachmaninoff'; 'James Levine'; "
            + "'Berliner Philharmoniker'],Piano Concerto No. 3 in D Minor Op. 30: III. Finale. Alla breve,"
            + "1921,4,831667,80.954,-20.096,0.0594,0.982,0.279,0.211,0.665,0.0366,0";
    private static final String BASIC_TRACK2 = "7xPhfUan2yNtyFG0cUWkt8,['Dennis Day'],"
            + "Clancy Lowered the Boom,1921,5,180533,60.936,-12.441,0.963,0.732,0.819,0.341,0.16,0.415,0";
    private static final String BASIC_TRACK3 = "0MJZ4hh60zwsYleWWxT5yW,['Zay Gatsby'],"
            + "Power Is Power,1921,0,205072,159.935,-7.298,0.493,0.0175,0.527,0.691,0.358,0.0326,1";

    private static final String BASIC_TRACK4_SAME_YEARS = "4v8CkyrDdgtgqkssZwaDe8,['Dennis Day'],"
            + "St. Patrick's Day Parade,1921,1,167027,125.023,-11.043,0.918,0.834,0.661,0.489,0.186,0.103,0";
    private static final String BASIC_TRACK5_DIFFERENT_YEARS = "4v8CkyrDdgtgqkssZwaDe8,['Dennis Day'],"
            + "St. Patrick's Day Parade,1923,1,167027,125.023,-11.043,0.918,0.834,0.661,0.489,0.186,0.103,0";

    private static final String BASIC_TRACK1_FROM_THE_80S = "28gKFaRNe013GtaKLtiHbH,"
            + "['Sister Sledge'],All American Girls,1981,41,283827,120.693,-7.037,0.943,"
            + "0.0268,0.747,0.75,0.395,0.0531,0";
    private static final String BASIC_TRACK2_FROM_THE_80S = "2vpRPyHWRg1kpfzhVpA0ad,"
            + "['José José'],Un Poco Más,1981,40,226000,79.838,-17.774,0.268,0.774,0.215,0.24,0.39,0.0351,0";
    private static final String BASIC_TRACK3_FROM_THE_80S = "4fvBHLNI8Ssmzll6uXdaOr,"
            + "['Los Jaivas'],Sube a Nacer Conmigo Hermano,1981,53,287387,109.924,-5.227,0.928,0.465,0.602,0.798,0.119,0.0465,0";

    private static final String BASIC_TRACK1_FROM_THE_90S = "5DkHYNiEqDnyKHW5r1Esj6,"
            + "['Mariah Carey'; 'Snoop Dogg'],Crybaby (feat. Snoop Dogg),1999,41,319107,79.08,"
            + "-5.183,0.626,0.216,0.586,0.578,0.106,0.0481,0";
    private static final String BASIC_TRACK2_FROM_THE_90S = "5gIwISiRFPLCBjMn8ykHfe,"
            + "['Misfits'],Fiend Club,1999,41,172368,141.102,-5.143,0.457,0.000193,0.455,0.919,0.678,0.0533,0";
    private static final String BASIC_TRACK3_FROM_THE_90S = "0xZC7eWCIIr3suXKTqlLAS,"
            + "['Sevendust'],Licking Crème,1999,39,197467,113.089,-3.802,0.577,0.000115,0.595,0.947,0.279,0.1,0";

    private static final String BASIC_DATA_INPUT = FIRST_LINE + System.lineSeparator()
            + BASIC_TRACK1 + System.lineSeparator()
            + BASIC_TRACK2 + System.lineSeparator()
            + BASIC_TRACK3;

    private static final String EMPTY_INPUT = "";

    @Test
    public void testSpotifyExplorerForReadingData() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            Collection<SpotifyTrack> tracks = spotifyExplorer.getAllSpotifyTracks();
            Collection<SpotifyTrack> testTracks = new LinkedList<>();
            testTracks.add(SpotifyTrack.of(BASIC_TRACK1));
            testTracks.add(SpotifyTrack.of(BASIC_TRACK2));
            testTracks.add(SpotifyTrack.of(BASIC_TRACK3));

            boolean hasAllTracks = testTracks.containsAll(tracks);
            boolean hasAllTestTracks = tracks.containsAll(testTracks);

            assertTrue("Tracks has all test tracks. ", hasAllTestTracks);
            assertTrue("Test tracks has all tracks. ", hasAllTracks);
        }
    }

    @Test
    public void testGetAllSpotifyTracksEmptyCollection() {
        try (StringReader dataReader = new StringReader(EMPTY_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            assertTrue("Empty collection!", spotifyExplorer.getAllSpotifyTracks().isEmpty());
        }
    }

    @Test
    public void testGetExplicitSpotifyTracksEmptyCollection() {
        try (StringReader dataReader = new StringReader(FIRST_LINE + System.lineSeparator() + BASIC_TRACK2)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            assertTrue("Empty collection.", spotifyExplorer.getExplicitSpotifyTracks().isEmpty());
        }
    }

    @Test
    public void testGetExplicitSpotifyTracksEmptyCollectionBecauseEmptyDataSet() {
        try (StringReader dataReader = new StringReader(EMPTY_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            assertTrue("Empty collection.", spotifyExplorer.getExplicitSpotifyTracks().isEmpty());
        }
    }

    @Test
    public void testGetExplicitSpotifyTracks() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            Collection<SpotifyTrack> tracks = spotifyExplorer.getExplicitSpotifyTracks();
            Collection<SpotifyTrack> testTracks = new LinkedList<>();
            testTracks.add(SpotifyTrack.of(BASIC_TRACK3));

            boolean hasAllTracks = testTracks.containsAll(tracks);
            boolean hasAllTestTracks = tracks.containsAll(testTracks);

            assertTrue("Tracks has all test tracks. ", hasAllTestTracks);
            assertTrue("Test tracks has all tracks. ", hasAllTracks);
        }
    }

    @Test
    public void testGroupSpotifyTracksByYearEmptyDataSet() {
        try (StringReader dataReader = new StringReader(EMPTY_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            assertTrue("Empty collection!", spotifyExplorer.groupSpotifyTracksByYear().isEmpty());
        }
    }

    @Test
    public void testGroupSpotifyTracksByYear() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            List<SpotifyTrack> spotifyTracks = new LinkedList<>();
            spotifyTracks.add(SpotifyTrack.of(BASIC_TRACK1));
            spotifyTracks.add(SpotifyTrack.of(BASIC_TRACK2));
            spotifyTracks.add(SpotifyTrack.of(BASIC_TRACK3));

            Map<Integer, Set<SpotifyTrack>> tracksByYear = spotifyExplorer.groupSpotifyTracksByYear();

            int year = 1921;
            boolean hasKey = tracksByYear.containsKey(year);
            boolean hasOnlyOneKey = Set.of(year).containsAll(tracksByYear.keySet());
            boolean hasValues = tracksByYear.get(year).containsAll(spotifyTracks);
            boolean hasTestTracksAllTracksFrom1921 = spotifyTracks.containsAll(tracksByYear.get(year));

            assertTrue("Grouped by year map contains right year.", hasKey);
            assertTrue("Grouped by year map contains the right year only.", hasOnlyOneKey);
            assertTrue("Grouped by year map contains all tracks in right year.", hasValues);
            assertTrue("Grouped by year tracks has all test tracks in right year.", hasTestTracksAllTracksFrom1921);
        }
    }

    @Test
    public void testGetArtistActiveYearsEmptyDataSet() {
        try (StringReader dataReader = new StringReader(EMPTY_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            int result = spotifyExplorer.getArtistActiveYears("Zay Gatsby");
            int expected = 0;
            assertEquals("Data set is empty, zero must be returned!", expected, result);
        }
    }

    @Test
    public void testGetArtistActiveYearsNoTracksFromTheArtist() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            int result = spotifyExplorer.getArtistActiveYears("Alex Stoyanova");
            int expected = 0;
            assertEquals("No tracks from this artist, zero must be returned!", expected, result);
        }
    }

    @Test
    public void testGetArtistActiveYearsWhenOldestAndNewestYearIsTheSame() {
        String currentDataInput = FIRST_LINE + System.lineSeparator()
                + BASIC_TRACK2 + System.lineSeparator()
                + BASIC_TRACK4_SAME_YEARS;

        try (StringReader dataReader = new StringReader(currentDataInput)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            int result = spotifyExplorer.getArtistActiveYears("Dennis Day");
            int expected = 1;
            assertEquals("Same years, 1 must be returned!", expected, result);
        }
    }

    @Test
    public void testGetArtistActiveYearsWhenOldestAndNewestYearIsDifferent() {
        String currentDataInput = FIRST_LINE + System.lineSeparator()
                + BASIC_TRACK2 + System.lineSeparator()
                + BASIC_TRACK5_DIFFERENT_YEARS;

        try (StringReader dataReader = new StringReader(currentDataInput)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            int result = spotifyExplorer.getArtistActiveYears("Dennis Day");
            int expected = 3;
            assertEquals("Different years, 3 must be returned!", expected, result);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTopNHighestValenceTracksFromThe80sIllegalArgument() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            int n = -1;
            spotifyExplorer.getTopNHighestValenceTracksFromThe80s(n);
        }
    }

    @Test
    public void testGetTopNHighestValenceTracksFromThe80sNExceedsTotalNumberOfTracks() {
        String currentDataInput = FIRST_LINE + System.lineSeparator()
                + BASIC_TRACK1_FROM_THE_80S + System.lineSeparator()
                + BASIC_TRACK2_FROM_THE_80S + System.lineSeparator()
                + BASIC_TRACK3_FROM_THE_80S;

        try (StringReader dataReader = new StringReader(currentDataInput)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            List<SpotifyTrack> spotifyTracks = new ArrayList<>();
            spotifyTracks.add(SpotifyTrack.of(BASIC_TRACK1_FROM_THE_80S));
            spotifyTracks.add(SpotifyTrack.of(BASIC_TRACK2_FROM_THE_80S));
            spotifyTracks.add(SpotifyTrack.of(BASIC_TRACK3_FROM_THE_80S));

            int n = 5;
            List<SpotifyTrack> returnedTracks = spotifyExplorer.getTopNHighestValenceTracksFromThe80s(n);

            boolean hasAllTracks = spotifyTracks.containsAll(returnedTracks);
            boolean hasAllTestTracks = returnedTracks.containsAll(spotifyTracks);

            assertTrue("All test tracks has all returned tracks.", hasAllTracks);
            assertTrue("All returned tracks has all test tracks.", hasAllTestTracks);
        }
    }

    @Test
    public void testGetTopNHighestValenceTracksFromThe80sNIsLessThanTotalNumberOfTracks() {
        String currentDataInput = FIRST_LINE + System.lineSeparator()
                + BASIC_TRACK1_FROM_THE_80S + System.lineSeparator()
                + BASIC_TRACK2_FROM_THE_80S + System.lineSeparator()
                + BASIC_TRACK3_FROM_THE_80S;
        try (StringReader dataReader = new StringReader(currentDataInput)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            List<SpotifyTrack> spotifyTracks = new ArrayList<>();
            spotifyTracks.add(SpotifyTrack.of(BASIC_TRACK1_FROM_THE_80S));
            spotifyTracks.add(SpotifyTrack.of(BASIC_TRACK3_FROM_THE_80S));

            int n = 2;
            List<SpotifyTrack> returnedTracks = spotifyExplorer.getTopNHighestValenceTracksFromThe80s(n);

            boolean hasAllTracks = spotifyTracks.containsAll(returnedTracks);
            boolean hasAllTestTracks = returnedTracks.containsAll(spotifyTracks);

            assertTrue("All test tracks has all returned tracks.", hasAllTracks);
            assertTrue("All returned tracks has all test tracks.", hasAllTestTracks);
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetMostPopularTrackFromThe90sNoTracksFromThe90s() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            spotifyExplorer.getMostPopularTrackFromThe90s();
        }
    }

    @Test
    public void testGetMostPopularTrackFromThe90sWhenOneIsMostPopular() {
        String currentDataInput = FIRST_LINE + System.lineSeparator()
                + BASIC_TRACK1_FROM_THE_90S + System.lineSeparator()
                + BASIC_TRACK3_FROM_THE_90S;

        try (StringReader dataReader = new StringReader(currentDataInput)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);
            SpotifyTrack spotifyTrack = spotifyExplorer.getMostPopularTrackFromThe90s();

            assertEquals("One track is most popular.", SpotifyTrack.of(BASIC_TRACK1_FROM_THE_90S), spotifyTrack);
        }

    }

    @Test
    public void testGetMostPopularTrackFromThe90sWhenMoreThanOnAreMostPopular() {
        String currentDataInput = FIRST_LINE + System.lineSeparator()
                + BASIC_TRACK1_FROM_THE_90S + System.lineSeparator()
                + BASIC_TRACK2_FROM_THE_90S + System.lineSeparator()
                + BASIC_TRACK3_FROM_THE_90S;

        try (StringReader dataReader = new StringReader(currentDataInput)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            SpotifyTrack spotifyTrack = spotifyExplorer.getMostPopularTrackFromThe90s();
            boolean isEquals1 = spotifyTrack.equals(SpotifyTrack.of(BASIC_TRACK1_FROM_THE_90S));
            boolean isEquals2 = spotifyTrack.equals(SpotifyTrack.of(BASIC_TRACK2_FROM_THE_90S));
            boolean isEqual = isEquals1 || isEquals2;

            assertTrue("More than one track are most popular, returned track is one of them.", isEqual);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberOfLongerTracksBeforeYearWhenMinutesAreNegativeNumber() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            int minutes = -20;
            int year = 1999;
            spotifyExplorer.getNumberOfLongerTracksBeforeYear(minutes, year);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberOfLongerTracksBeforeYearWhenYearsAreNegativeNumber() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            int minutes = 20;
            int year = -1999;
            spotifyExplorer.getNumberOfLongerTracksBeforeYear(minutes, year);
        }
    }

    @Test
    public void testGetNumberOfLongerTracksBeforeYearWhenOneTrackIsLonger() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            int minutes = 4;
            int year = 1922;
            int expected = 1;
            long count = spotifyExplorer.getNumberOfLongerTracksBeforeYear(minutes, year);
            assertEquals("One track is longer than 4 minutes before 1922.", expected, count);
        }
    }

    @Test
    public void testGetNumberOfLongerTracksBeforeYearWhenNoTrackIsLonger() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            int minutes = 20;
            int year = 1922;
            int expected = 0;
            long count = spotifyExplorer.getNumberOfLongerTracksBeforeYear(minutes, year);
            assertEquals("No track is longer than 4 minutes before 1922.", expected, count);
        }
    }

    @Test
    public void testGetNumberOfLongerTracksBeforeYearWhenMoreThanOneTrackIsLonger() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            int minutes = 3;
            int year = 1922;
            int expected = 3;
            long count = spotifyExplorer.getNumberOfLongerTracksBeforeYear(minutes, year);
            assertEquals("No track is longer than 4 minutes before 1922.", expected, count);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTheLoudestTrackInYearWhenArgumentIsNegativeNumber() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            int year = -1999;
            spotifyExplorer.getTheLoudestTrackInYear(year);
        }
    }

    @Test
    public void testGetTheLoudestTrackInYear() {
        try (StringReader dataReader = new StringReader(BASIC_DATA_INPUT)) {
            SpotifyExplorer spotifyExplorer = new SpotifyExplorer(dataReader);

            int year = 1921;
            Optional<SpotifyTrack> spotifyTrack = spotifyExplorer.getTheLoudestTrackInYear(year);
            assertEquals("", SpotifyTrack.of(BASIC_TRACK3), spotifyTrack.get());
        }
    }
}
