package bg.sofia.uni.fmi.mjt.socialmedia.operation;

import java.time.LocalDateTime;

public class Like extends Operation {
    public Like(String contentId, LocalDateTime dateOfActivity) {
        super(contentId, dateOfActivity);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ": Liked a content with id " + this.getContentId();
    }
}
