package bg.sofia.uni.fmi.mjt.socialmedia.operation;

import bg.sofia.uni.fmi.mjt.socialmedia.activity.Activity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Operation implements Activity {
    private LocalDateTime dateOfActivity;
    private String contentId;

    public Operation(String contentId, LocalDateTime dateOfActivity) {
        this.contentId = contentId;
        this.dateOfActivity = dateOfActivity;
    }

    public String getContentId() {
        return contentId;
    }

    public LocalDateTime getDateOfActivity() {
        return dateOfActivity;
    }

    @Override
    public String getDescription() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        return dateOfActivity.format(formatter);
    }
}
