package bg.sofia.uni.fmi.mjt.socialmedia.operation;

import java.time.LocalDateTime;

public class Comment extends Operation {
    private String text;

    public Comment(String contentId, String text, LocalDateTime dateOfActivity) {
        super(contentId, dateOfActivity);
        this.text = text;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ": Commented " + text + " on a content with id " + this.getContentId();
    }
}
