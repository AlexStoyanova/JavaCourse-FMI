package bg.sofia.uni.fmi.mjt.wish.list.user;

import java.util.Set;

public record UserData(String password, Set<String> wishes) {
    public void setWish(String wish) {
        wishes.add(wish);
    }

    public void removeWishes() {
        wishes.clear();
    }
}
