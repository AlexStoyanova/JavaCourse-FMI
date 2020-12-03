package bg.sofia.uni.fmi.mjt.tagger;

import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TaggerTest {

    private static final String cityWithCountry = "Aabenraa,Denmark" + System.lineSeparator() + "Aachen,Germany";

    @Test
    public void testTaggerReadCities() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Aabenraa");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        String taggedText = "<city country=\"Denmark\">Aabenraa</city>";

        assertEquals("Constructor read city Aabenraa from Denmark", taggedText, output.toString());
    }

    @Test
    public void testTagCitiesWhenThereIsNoCity() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Something write.");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        String taggedText = "Something write.";

        assertEquals("Tag cities with no cities and no tags.", taggedText, output.toString());
    }

    @Test
    public void testTagCitiesWhenThereIsOneCity() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Something write Aabenraa.");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        String taggedText = "Something write <city country=\"Denmark\">Aabenraa</city>.";

        assertEquals("Tag cities with one city and one tag.", taggedText, output.toString());
    }

    @Test
    public void testTagCitiesWhenThereIsRepeatedCities() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Something write Aabenraa bla bla Aabenraa.");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        String taggedText = "Something write <city country=\"Denmark\">Aabenraa</city> "
                + "bla bla <city country=\"Denmark\">Aabenraa</city>.";

        assertEquals("Tag cities with one city and one tag.", taggedText, output.toString());
    }

    @Test
    public void testTagCitiesWhenThereIsDifferentCities() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Something write Aabenraa bla bla Aachen.");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        String taggedText = "Something write <city country=\"Denmark\">Aabenraa</city> "
                + "bla bla <city country=\"Germany\">Aachen</city>.";

        assertEquals("Tag cities with one city and one tag.", taggedText, output.toString());
    }

    @Test
    public void testTagCitiesWithCaseInsensitive() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Something write AAbeNraa.");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        String taggedText = "Something write <city country=\"Denmark\">AAbeNraa</city>.";

        assertEquals("Tag cities with one city and one tag.", taggedText, output.toString());
    }

    @Test
    public void testTagCitiesWithCharactersToWord() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Something write AAbeNraa's .");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        String taggedText = "Something write <city country=\"Denmark\">AAbeNraa</city>'s .";

        assertEquals("Tag cities with one city and one tag.", taggedText, output.toString());
    }

    @Test
    public void testGetNMostTaggedCitiesEmptyCollection() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Something write.");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        assertTrue("No tags, empty collection must be returned!", tagger.getNMostTaggedCities(5).isEmpty());
    }

    @Test
    public void testGetNMostTaggedCitiesWhenNIsLessThanNumberOfMostTaggedCities() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Something write Aabenraa bla Aabenraa bla Aabenraa Aachen.");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        Collection<String> mostTaggedCities = new ArrayList<>();
        mostTaggedCities.add("Aabenraa");

        boolean hasReturnedCitiesAllUniqueCities = mostTaggedCities.containsAll(tagger.getNMostTaggedCities(1));
        boolean hasAllUniqueCitiesReturnCities = tagger.getNMostTaggedCities(1).containsAll(mostTaggedCities);

        assertTrue("Test collection contains all returned unique cities!", hasReturnedCitiesAllUniqueCities);
        assertTrue("Returned collection contains all test collection's elements!", hasAllUniqueCitiesReturnCities);
    }

    @Test
    public void testGetNMostTaggedCitiesWhenNIsMoreThanNumberOfMostTaggedCities() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Something write Aabenraa bla Aabenraa bla Aabenraa Aachen.");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        Collection<String> mostTaggedCities = new ArrayList<>();
        mostTaggedCities.add("Aabenraa");
        mostTaggedCities.add("Aachen");

        boolean hasReturnedCitiesAllUniqueCities = mostTaggedCities.containsAll(tagger.getNMostTaggedCities(5));
        boolean hasAllUniqueCitiesReturnCities = tagger.getNMostTaggedCities(5).containsAll(mostTaggedCities);

        assertTrue("Test collection contains all returned unique cities!", hasReturnedCitiesAllUniqueCities);
        assertTrue("Returned collection contains all test collection's elements!", hasAllUniqueCitiesReturnCities);
    }


    @Test
    public void testGetNMostTaggedCitiesFromLastInvocationOfTagCities() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text1 = new StringReader("Something write Aabenraa bla Aabenraa bla Aabenraa Aachen.");
        StringWriter output1 = new StringWriter();

        StringReader text2 = new StringReader("Something write Aachen bla Aachen bla Aachen.");
        StringWriter output2 = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text1, output1);
        tagger.tagCities(text2, output2);

        Collection<String> mostTaggedCities = new ArrayList<>();
        mostTaggedCities.add("Aachen");

        boolean hasReturnedCitiesAllUniqueCities = mostTaggedCities.containsAll(tagger.getNMostTaggedCities(1));
        boolean hasAllUniqueCitiesReturnCities = tagger.getNMostTaggedCities(1).containsAll(mostTaggedCities);

        assertTrue("Test collection contains all returned unique cities!", hasReturnedCitiesAllUniqueCities);
        assertTrue("Returned collection contains all test collection's elements!", hasAllUniqueCitiesReturnCities);
    }

    @Test
    public void testGetAllTaggedCitiesEmptyCollection() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Something write.");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        assertTrue("No tags, empty collection must be returned!", tagger.getAllTaggedCities().isEmpty());
    }

    @Test
    public void testGetAllTaggedCitiesWhenThereIsMoreTagsInOneCity() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Something write Aabenraa bla Aabenraa bla Aabenraa Aachen.");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        Collection<String> allTaggedCities = new ArrayList<>();
        allTaggedCities.add("Aabenraa");
        allTaggedCities.add("Aachen");

        boolean hasReturnedCitiesAllUniqueCities = allTaggedCities.containsAll(tagger.getAllTaggedCities());
        boolean hasAllUniqueCitiesReturnCities = tagger.getAllTaggedCities().containsAll(allTaggedCities);

        assertTrue("Test collection contains all returned unique cities!", hasReturnedCitiesAllUniqueCities);
        assertTrue("Returned collection contains all test collection's elements!", hasAllUniqueCitiesReturnCities);
    }


    @Test
    public void testGetAllTaggedCitiesFromLastInvocationOfTagCities() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text1 = new StringReader("Something write Aabenraa bla Aabenraa bla Aabenraa Aachen.");
        StringWriter output1 = new StringWriter();

        StringReader text2 = new StringReader("Something write Aachen bla Aachen bla Aachen.");
        StringWriter output2 = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text1, output1);
        tagger.tagCities(text2, output2);

        Collection<String> allTaggedCities = new ArrayList<>();
        allTaggedCities.add("Aachen");

        boolean hasReturnedCitiesAllUniqueCities = allTaggedCities.containsAll(tagger.getAllTaggedCities());
        boolean hasAllUniqueCitiesReturnCities = tagger.getAllTaggedCities().containsAll(allTaggedCities);

        assertTrue("Test collection contains all returned unique cities!", hasReturnedCitiesAllUniqueCities);
        assertTrue("Returned collection contains all test collection's elements!", hasAllUniqueCitiesReturnCities);
    }

    @Test
    public void testGetAllTagsCountWhenNoTaggedCities() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("Something write.");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        assertEquals("No tagged cities, count must be 0!", 0, tagger.getAllTagsCount());
    }

    @Test
    public void testGetAllTagsCountWhenOneCityIsTaggedMoreThanOneTime() {
        StringReader cities = new StringReader(cityWithCountry);
        StringReader text = new StringReader("\"Something write AaBenraa bla Aabenraa bla Aabenraa Aachen.");
        StringWriter output = new StringWriter();

        Tagger tagger = new Tagger(cities);
        tagger.tagCities(text, output);

        assertEquals("One city tagged 3 times and another 1 time, count must be 4!", 4, tagger.getAllTagsCount());
    }
}

