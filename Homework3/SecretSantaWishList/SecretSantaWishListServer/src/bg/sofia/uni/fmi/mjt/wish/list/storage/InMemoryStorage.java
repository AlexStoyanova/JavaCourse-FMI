package bg.sofia.uni.fmi.mjt.wish.list.storage;

import bg.sofia.uni.fmi.mjt.wish.list.user.UserData;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class InMemoryStorage implements Storage {
    private final Map<String, UserData> registeredStudents;
    private final Map<SocketChannel, String> usernameByChannel;

    public InMemoryStorage() {
        registeredStudents = new HashMap<>();
        usernameByChannel = new HashMap<>();
    }

    public String getUsernameByChannel(SocketChannel channel) {
        return usernameByChannel.get(channel);
    }

    public UserData getUserDataByUsername(String username) {
        return registeredStudents.get(username);
    }

    public boolean isRegisteredStudent(String username) {
        return registeredStudents.containsKey(username);
    }

    public void registerStudent(String username, String password) {
        registeredStudents.put(username, new UserData(password, new HashSet<>()));
    }

    public void addWishToStudent(String username, String wish) {
        registeredStudents.get(username).setWish(wish);
    }

    public void addActiveUser(SocketChannel channel, String username) {
        usernameByChannel.put(channel, username);
    }

    public void removeActiveUser(SocketChannel channel) {
        usernameByChannel.remove(channel);
    }

    public int getSizeOfRegisteredStudents() {
        return registeredStudents.size();
    }

    public Map<String, UserData> getRegisteredStudents() {
        return registeredStudents;
    }

    public void removeWishesFromStudent(String username) {
        registeredStudents.get(username).removeWishes();
    }
}

