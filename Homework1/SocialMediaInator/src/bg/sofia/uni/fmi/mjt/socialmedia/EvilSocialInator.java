package bg.sofia.uni.fmi.mjt.socialmedia;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Post;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Publication;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Story;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.NoUsersException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.user.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EvilSocialInator implements SocialMediaInator {

    private HashMap<String, User> users;

    public EvilSocialInator() {
        users = new HashMap<>();
    }

    @Override
    public void register(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Argument is null!");
        }
        if (users.containsKey(username)) {
            throw new UsernameAlreadyExistsException("Username already exists in platform!");
        }
        users.put(username, new User(username));
    }

    @Override
    public String publishPost(String username, LocalDateTime publishedOn, String description) {
        if (username == null || publishedOn == null || description == null) {
            throw new IllegalArgumentException("Some of the arguments is null.");
        }
        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException("Username not found in platform!");
        }
        Publication newPost = new Post(username, description, publishedOn);
        users.get(username).addPost(newPost);
        addMentionsInUsers(newPost);
        return newPost.getId();
    }

    @Override
    public String publishStory(String username, LocalDateTime publishedOn, String description) {
        if (username == null || publishedOn == null || description == null) {
            throw new IllegalArgumentException("Some of the arguments is null.");
        }
        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException("Username not found in platform!");
        }
        Publication newStory = new Story(username, description, publishedOn);
        users.get(username).addStory(newStory);
        addMentionsInUsers(newStory);
        return newStory.getId();
    }

    @Override
    public void like(String username, String id) {
        if (username == null || id == null) {
            throw new IllegalArgumentException("Some of the arguments is null.");
        }
        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException("Username not found in platform!");
        }
        addOperationInUser(username, id, "", 'l');
    }

    @Override
    public void comment(String username, String text, String id) {
        if (username == null || text == null || id == null) {
            throw new IllegalArgumentException("Some of the arguments is null.");
        }
        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException("Username not found in platform!");
        }
        addOperationInUser(username, id, text, 'c');
    }

    @Override
    public Collection<Content> getNMostPopularContent(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument is a negative number.");
        }

        Collection<Publication> allPublications = new LinkedList<>();
        for (User user : users.values()) {
            allPublications.addAll(user.getPublications());
        }

        return sortNByPopularity(allPublications, n);
    }

    @Override
    public Collection<Content> getNMostRecentContent(String username, int n) {
        if (username == null || n < 0) {
            throw new IllegalArgumentException("Argument is null or/and number is negative.");
        }
        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException("Username not found in platform.");
        }

        return users.get(username).getNMostRecentPublications(n);
    }

    @Override
    public String getMostPopularUser() {
        if (users.isEmpty()) {
            throw new NoUsersException("No users in platform");
        }

        int mostPopular = 0;
        String nameOfUser = "";
        for (User user : users.values()) {
            if (user.getNumberOfMentions() >= mostPopular) {
                mostPopular = user.getNumberOfMentions();
                nameOfUser = user.getName();
            }
        }
        return nameOfUser;
    }

    @Override
    public Collection<Content> findContentByTag(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Argument is null.");
        }

        Collection<User> allUsers = users.values();
        Collection<Content> contentsWithTag = new LinkedList<>();

        for (User user : allUsers) {
            contentsWithTag.addAll(user.getContentsWithTag(tag));
        }

        return Collections.unmodifiableCollection(contentsWithTag);
    }

    @Override
    public List<String> getActivityLog(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Argument is null.");
        }
        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException("User not found in platform!");
        }
        return users.get(username).activityLog();
    }

    private void addOperationInUser(String username, String id, String text, char type) {
        Collection<User> allUsers = users.values();

        for (User user : allUsers) {
            if (user.isPublicationIn(id)) {
                if (type == 'l') {
                    user.addLikeOfPublication(id);
                    users.get(username).likePublication(id);
                } else if (type == 'c') {
                    user.addCommentOfPublication(id);
                    users.get(username).commentPublication(id, text);
                }
                return;
            }
        }
        throw new ContentNotFoundException("Content not found in publications of the user!");
    }

    private void addMentionsInUsers(Publication publication) {
        Collection<String> mentions = publication.getMentions();
        for (String mention : mentions) {
            if (users.containsKey(mention.substring(1))) {
                users.get(mention.substring(1)).increaseMentions();
            }
        }
    }

    private Collection<Content> sortNByPopularity(Collection<Publication> allPublications, int n) {
        Iterator<Publication> publicationIterator = allPublications.iterator();
        while (publicationIterator.hasNext()) {
            if (publicationIterator.next().isExpired()) {
                publicationIterator.remove();
            }
        }

        Collection<Content> mostNPopularContents = new LinkedList<>();
        List<Publication> sortedPublications = new LinkedList<>(allPublications);
        sortedPublications.sort(compareByPopularity);
        int index = 0;
        while (index < n && index < sortedPublications.size()) {
            mostNPopularContents.add(sortedPublications.get(index++));
        }
        return Collections.unmodifiableCollection(mostNPopularContents);
    }

    private static Comparator<Publication> compareByPopularity = new Comparator<>() {
        @Override
        public int compare(Publication publication1, Publication publication2) {
            return (publication2.getPopularity()) - (publication1.getPopularity());
        }
    };
}
