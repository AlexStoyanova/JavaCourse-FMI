package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.Duration;
import java.time.LocalDateTime;

public class Story extends Publication {

    private static final int validHours = 24;

    public Story(String username, String description, LocalDateTime publishedOn) {
        super(username, description, publishedOn);
    }

    @Override
    public boolean isExpired() {
        return Duration.between(getPublishedOn(), LocalDateTime.now()).toHours() > validHours;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ": Created a story with id " + getId();
    }
}
