package bg.sofia.uni.fmi.mjt.wish.list;

import java.util.Set;

public record UserData(String password, Set<String> wishes) {
    public void setWishes(String wish) {
        wishes.add(wish);
    }

    public void removeWishes() {
        wishes.clear();
    }
}
