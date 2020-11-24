package bg.sofia.uni.fmi.mjt.socialmedia.content;

import bg.sofia.uni.fmi.mjt.socialmedia.activity.Activity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class Publication implements Content, Activity {

    private int numberOfLikes;
    private int numberOfComments;
    private static int idNumber = 0;
    private String id;
    private String description;
    private LocalDateTime publishedOn;

    public Publication(String username, String description, LocalDateTime publishedOn) {
        this.numberOfLikes = 0;
        this.numberOfComments = 0;
        this.description = description;
        this.publishedOn = publishedOn;
        id = username + "-" + idNumber;
        idNumber++;
    }

    @Override
    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    @Override
    public int getNumberOfComments() {
        return numberOfComments;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Collection<String> getTags() {
        return takeCollectionOfSubstrings('#');
    }

    @Override
    public Collection<String> getMentions() {
        return takeCollectionOfSubstrings('@');
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public int getPopularity() {
        return numberOfLikes + numberOfComments;
    }

    public String getDescription() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        return publishedOn.format(formatter);
    }

    public void like() {
        numberOfLikes++;
    }

    public void comment() {
        numberOfComments++;
    }

    public abstract boolean isExpired();

    private Collection<String> takeCollectionOfSubstrings(char delimiter) {
        Collection<String> substrings = new HashSet<>();
        List<String> wordsList = Arrays.asList(description.split(" "));
        int sizeList = wordsList.size();

        for (int i = 0; i < sizeList; i++) {
            if (wordsList.get(i).charAt(0) == delimiter) {
                substrings.add(wordsList.get(i));
            }
        }

        return substrings;
    }
}
