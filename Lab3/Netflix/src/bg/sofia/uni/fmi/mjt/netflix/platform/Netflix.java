package bg.sofia.uni.fmi.mjt.netflix.platform;

import bg.sofia.uni.fmi.mjt.netflix.account.Account;
import bg.sofia.uni.fmi.mjt.netflix.content.Streamable;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentUnavailableException;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.UserNotFoundException;

import java.time.LocalDateTime;

public class Netflix implements StreamingService {
    private Account[] accounts;
    private PairOfStreamableContentAndNumericInfo[] contentsWithViews;
    private static final int CHILDREN = 14;
    private static final int ADULT = 18;


    private boolean isInAccounts(Account user) {
        for (Account i : accounts) {
            if (i.equals(user)) {
                return true;
            }
        }
        return false;
    }

    private PairOfStreamableContentAndNumericInfo isInContents(String videoContentName) {
        for (int i = 0; i < contentsWithViews.length; ++i) {
            if (contentsWithViews[i].content().getTitle().equals(videoContentName)) {
                return new PairOfStreamableContentAndNumericInfo(contentsWithViews[i].content(), i);
            }
        }
        return null;
    }


    public Netflix(Account[] accounts, Streamable[] streamableContent) {
        this.accounts = accounts;
        contentsWithViews = new PairOfStreamableContentAndNumericInfo[streamableContent.length];

        for (int i = 0; i < streamableContent.length; ++i) {
            contentsWithViews[i] = new PairOfStreamableContentAndNumericInfo(streamableContent[i], 0);
        }
    }

    public void watch(Account user, String videoContentName) throws ContentUnavailableException {
        try {
            if (isInAccounts(user)) {
                long years = java.time.temporal.ChronoUnit.YEARS.between(user.birthdayDate(), LocalDateTime.now());
                PairOfStreamableContentAndNumericInfo contentAndIndex = isInContents(videoContentName);

                if (contentAndIndex == null) {
                    throw new ContentNotFoundException("Content is not found in videos");
                }

                if ((years < CHILDREN && (contentAndIndex.content().getRating() == PgRating.PG13
                        || contentAndIndex.content().getRating() == PgRating.NC17))
                        || (years < ADULT && contentAndIndex.content().getRating() == PgRating.NC17)) {
                    throw new ContentUnavailableException("Content is unavailable for that user because of their age");
                }

                int views = contentsWithViews[contentAndIndex.number()].number();

                contentsWithViews[contentAndIndex.number()] =
                        new PairOfStreamableContentAndNumericInfo(contentAndIndex.content(), views + 1);
            } else {
                throw new UserNotFoundException("User is not found in accounts!");
            }
        } catch (UserNotFoundException | ContentUnavailableException | ContentNotFoundException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public Streamable findByName(String videoContentName) {
        if (contentsWithViews == null) {
            return null;
        }

        for (PairOfStreamableContentAndNumericInfo pair : contentsWithViews) {
            if (pair.content().getTitle().equals(videoContentName)) {
                return pair.content();
            }
        }

        return null;
    }

    public Streamable mostViewed() {
        if (contentsWithViews == null) {
            return null;
        }

        int max = 0;
        int index = 0;
        int curr;

        for (int i = 0; i < contentsWithViews.length; ++i) {
            curr = contentsWithViews[i].number();
            if (curr > max) {
                max = curr;
                index = i;
            }
        }

        if (max == 0) {
            return null;
        }

        return contentsWithViews[index].content();
    }

    public int totalWatchedTimeByUsers() {
        if (contentsWithViews == null) {
            throw new NullPointerException();
        }

        int totalTime = 0;

        for (PairOfStreamableContentAndNumericInfo pair : contentsWithViews) {
            totalTime += (pair.content().getDuration() * pair.number());
        }

        return totalTime;
    }

    public static void main(String[] args) {

    }
}
