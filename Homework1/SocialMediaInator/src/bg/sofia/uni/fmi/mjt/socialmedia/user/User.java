package bg.sofia.uni.fmi.mjt.socialmedia.user;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Publication;
import bg.sofia.uni.fmi.mjt.socialmedia.operation.Comment;
import bg.sofia.uni.fmi.mjt.socialmedia.operation.Like;
import bg.sofia.uni.fmi.mjt.socialmedia.operation.Operation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class User {
    private String name;
    private HashMap<String, Publication> publications;
    private LinkedList<Operation> operations;
    private int numberOfMentions;

    public User(String name) {
        this.name = name;
        publications = new HashMap<>();
        operations = new LinkedList<>();
        numberOfMentions = 0;
    }

    public void addPost(Publication post) {
        publications.put(post.getId(), post);
    }

    public void addStory(Publication story) {
        publications.put(story.getId(), story);
    }

    public boolean isPublicationIn(String id) {
        return publications.containsKey(id);
    }

    public void addLikeOfPublication(String id) {
        publications.get(id).like();
    }

    public void likePublication(String id) {
        Operation newLike = new Like(id, LocalDateTime.now());
        operations.add(0, newLike);
    }

    public void addCommentOfPublication(String id) {
        publications.get(id).comment();
    }

    public void commentPublication(String id, String text) {
        Operation newComment = new Comment(id, text, LocalDateTime.now());
        operations.add(0, newComment);
    }

    public Collection<Content> getNMostRecentPublications(int n) {
        List<Publication> sortedPublications = sortedPublications();
        int index = 0;
        int sizeOfSortedPublications = sortedPublications.size();
        while (index < sizeOfSortedPublications && index < n) {
            if (sortedPublications.get(index).isExpired()) {
                sortedPublications.remove(index);
                sizeOfSortedPublications--;
            } else {
                index++;
            }
        }
        return Collections.unmodifiableCollection(sortedPublications.subList(0, index));
    }

    public Collection<Content> getContentsWithTag(String tag) {
        Collection<Content> contentsWithTag = new LinkedList<>();
        Collection<Publication> allPublications = publications.values();

        for (Publication publication : allPublications) {
            if (!publication.isExpired() && publication.getTags().contains(tag)) {
                contentsWithTag.add(publication);
            }
        }
        return contentsWithTag;
    }

    public List<String> activityLog() {
        if (publications.isEmpty() && operations.isEmpty()) {
            return new ArrayList<>();
        } else if (publications.isEmpty()) {
            return takeOperationsActivity();
        } else if (operations.isEmpty()) {
            return takePublicationsActivity();
        }
        return takeAllActivities();
    }

    public void increaseMentions() {
        numberOfMentions++;
    }

    public int getNumberOfMentions() {
        return numberOfMentions;
    }

    public String getName() {
        return name;
    }

    public Collection<Publication> getPublications() {
        return Collections.unmodifiableCollection(publications.values());
    }

    private List<String> takeAllActivities() {
        List<Publication> sortedPublications = sortedPublications();
        List<String> activities = new ArrayList<>();
        int indexToGet = 0;

        while (!sortedPublications.isEmpty() && indexToGet != operations.size()) {
            if (sortedPublications.get(0).getPublishedOn().isAfter(operations.get(indexToGet).getDateOfActivity())) {
                activities.add(sortedPublications.remove(0).getDescription());
            } else {
                activities.add(operations.get(indexToGet++).getDescription());
            }
        }

        return takeTheRestOfData(activities, sortedPublications, indexToGet);
    }

    private List<String> takeTheRestOfData(List<String> activities, List<Publication> sortedPublications, int index) {
        if (sortedPublications.isEmpty() && index == operations.size()) {
            return activities;
        } else if (sortedPublications.isEmpty()) {
            while (index < operations.size()) {
                activities.add(operations.get(index++).getDescription());
            }
        } else {
            while (!sortedPublications.isEmpty()) {
                activities.add(sortedPublications.remove(0).getDescription());
            }
        }
        return activities;
    }

    private List<Publication> sortedPublications() {
        List<Publication> sortedPublications = new ArrayList<>(publications.values());
        sortedPublications.sort(compareByDate);
        return sortedPublications;
    }

    private List<String> takeOperationsActivity() {
        List<String> operationDescription = new LinkedList<>();

        for (Operation operation : operations) {
            operationDescription.add(operation.getDescription());
        }

        return operationDescription;
    }

    private List<String> takePublicationsActivity() {
        List<String> publicationDescription = new LinkedList<>();

        for (Publication publication : sortedPublications()) {
            publicationDescription.add(publication.getDescription());
        }
        return publicationDescription;
    }

    private static Comparator<Publication> compareByDate = new Comparator<>() {
        @Override
        public int compare(Publication publication1, Publication publication2) {
            return publication2.getPublishedOn().compareTo(publication1.getPublishedOn());
        }
    };
}
