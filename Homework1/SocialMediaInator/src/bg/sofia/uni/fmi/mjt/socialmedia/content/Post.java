package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.Duration;
import java.time.LocalDateTime;

public class Post extends Publication {

    private static final int validDays = 30;

    public Post(String username, String description, LocalDateTime publishedOn) {
        super(username, description, publishedOn);
    }

    @Override
    public boolean isExpired() {
        return Duration.between(getPublishedOn(), LocalDateTime.now()).toDays() > validDays;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ": Created a post with id " + getId();
    }
}
