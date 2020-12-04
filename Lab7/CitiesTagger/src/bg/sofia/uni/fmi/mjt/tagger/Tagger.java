package bg.sofia.uni.fmi.mjt.tagger;

import bg.sofia.uni.fmi.mjt.tagger.pair.CityWithNumberOfTags;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.Set;

import java.nio.file.Path;
import java.nio.file.Files;

public class Tagger {

    private Map<String, String> citiesWithCountries;
    private Map<String, Integer> citiesWithTags;

    public Tagger(Reader citiesReader) {
        citiesWithCountries = new HashMap<>();
        citiesWithTags = new HashMap<>();
        readCitiesFromFile(citiesReader);
    }

    public void tagCities(Reader text, Writer output) {
        if (!citiesWithTags.isEmpty()) {
            citiesWithTags = new HashMap<>();
        }
        processTextWithCities(text, output);
    }

    public Collection<String> getNMostTaggedCities(int n) {
        List<CityWithNumberOfTags> taggedCities = new ArrayList<>();
        Set<Map.Entry<String, Integer>> entries = citiesWithTags.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            CityWithNumberOfTags cityWithTags = new CityWithNumberOfTags(entry.getKey(), entry.getValue());
            taggedCities.add(cityWithTags);
        }

        taggedCities.sort(compareCitiesByNumberOfTags);
        if (n > taggedCities.size()) {
            n = taggedCities.size();
        }

        List<String> mostTaggedCities = new ArrayList<>();
        List<CityWithNumberOfTags> subListOfCities = taggedCities.subList(0, n);
        for (CityWithNumberOfTags pair : subListOfCities) {
            mostTaggedCities.add(pair.city());
        }

        return mostTaggedCities;
    }

    public Collection<String> getAllTaggedCities() {
        return citiesWithTags.keySet();
    }

    public long getAllTagsCount() {
        long counts = 0;
        Collection<Integer> values = citiesWithTags.values();
        for (Integer numberOfTags : values) {
            counts += numberOfTags;
        }
        return counts;
    }

    private void readCitiesFromFile(Reader citiesReader) {
        try (BufferedReader citiesBufferedReader = new BufferedReader(citiesReader)) {
            String line;
            String[] pair;
            while ((line = citiesBufferedReader.readLine()) != null) {
                pair = line.split(",");
                citiesWithCountries.put(pair[0], pair[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processTextWithCities(Reader text, Writer output) {
        try (Scanner textBufferedReader = new Scanner(new BufferedReader(text));
                BufferedWriter textBufferedWriter = new BufferedWriter(output)) {
            Pattern pat = Pattern.compile(".*\\R|.+\\z");
            String line;
            while ((line = textBufferedReader.findWithinHorizon(pat, 0)) != null) {
                textBufferedWriter.append(changeLine(line));
            }
            textBufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String changeLine(String oldLine) {
        int size = oldLine.length();

        StringBuilder word = new StringBuilder();
        StringBuilder changedLine = new StringBuilder();

        for (int i = 0; i < size; ++i) {
            if (oldLine.charAt(i) == ' ') {
                addModifiedCity(changedLine, word);
                word.delete(0, word.length());
                changedLine.append(' ');
            } else if (oldLine.charAt(i) == '\t') {
                addModifiedCity(changedLine, word);
                word.delete(0, word.length());
                changedLine.append('\t');
            } else {
                word.append(oldLine.charAt(i));
            }
        }
        addModifiedCity(changedLine, word);

        return String.valueOf(changedLine);
    }

    private void addModifiedCity(StringBuilder changedLine, StringBuilder word) {
        StringBuilder modifiedWord;
        String country;
        String cityWithTag;
        String finalWord;
        String caseInsensitiveCity;
        int index1;
        int index2;

        modifiedWord = removeCharactersButNotLetters(String.valueOf(word));
        caseInsensitiveCity = String.valueOf(changeToCaseInsensitive(modifiedWord));
        if (citiesWithCountries.containsKey(caseInsensitiveCity)) {
            country = citiesWithCountries.get(caseInsensitiveCity);
            cityWithTag = "<city country=\"" + country + "\">" + modifiedWord + "</city>";
            index1 = word.indexOf(String.valueOf(modifiedWord));
            index2 = index1 + modifiedWord.length();
            finalWord = word.substring(0, index1) + cityWithTag + word.substring(index2);
            changedLine.append(finalWord);
            increaseTagsInCity(caseInsensitiveCity);
        } else {
            changedLine.append(word);
        }
    }

    private void increaseTagsInCity(String city) {
        if (citiesWithTags.containsKey(city)) {
            Integer counts = citiesWithTags.get(city);
            citiesWithTags.put(city, counts + 1);
        } else {
            citiesWithTags.put(city, 1);
        }
    }

    private StringBuilder removeCharactersButNotLetters(String word) {
        int size = word.length();
        StringBuilder filteredWord = new StringBuilder();
        int j = 0;
        for (int i = 0; i < size; ++i) {
            if (isLetter(word.charAt(i))) {
                filteredWord.insert(j++, word.charAt(i));
            } else if (!filteredWord.isEmpty()) {
                break;
            }
        }
        return filteredWord;
    }

    private boolean isLetter(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }

    private boolean isABigLetter(char letter) {
        return (letter >= 'A' && letter <= 'Z');
    }

    private char changeLetterToUpper(char letter) {
        return (char) (letter - ('a' - 'A'));
    }

    private char changeLetterToLower(char letter) {
        return (char) (letter + ('a' - 'A'));
    }

    private StringBuilder changeToCaseInsensitive(StringBuilder city) {
        StringBuilder newCaseInsensitive = new StringBuilder("");

        if (String.valueOf(city).equals("")) {
            return newCaseInsensitive;
        }

        int length = city.length();
        if (isLetter(city.charAt(0)) && !isABigLetter(city.charAt(0))) {
            newCaseInsensitive.append(changeLetterToUpper(city.charAt(0)));
        } else {
            newCaseInsensitive.append(city.charAt(0));
        }

        for (int i = 1; i < length; ++i) {
            if (isLetter(city.charAt(i)) && isABigLetter(city.charAt(i))) {
                newCaseInsensitive.append(changeLetterToLower(city.charAt(i)));
            } else {
                newCaseInsensitive.append(city.charAt(i));
            }
        }

        return newCaseInsensitive;
    }

    private Comparator<CityWithNumberOfTags> compareCitiesByNumberOfTags = new Comparator<>() {
        @Override
        public int compare(CityWithNumberOfTags o1, CityWithNumberOfTags o2) {
            return o2.numberOfTags() - o1.numberOfTags();
        }
    };

    public static void main(String[] args) {
        Path path1 = Path.of("world-cities.txt");
        Path path2 = Path.of("essay.txt");
        Path path3 = Path.of("output.txt");
        try (BufferedReader br1 = Files.newBufferedReader(path1);
             BufferedReader br2 = Files.newBufferedReader(path2);
             BufferedWriter br3 = Files.newBufferedWriter(path3)) {
            Tagger tagger = new Tagger(br1);

            tagger.tagCities(br2, br3);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
