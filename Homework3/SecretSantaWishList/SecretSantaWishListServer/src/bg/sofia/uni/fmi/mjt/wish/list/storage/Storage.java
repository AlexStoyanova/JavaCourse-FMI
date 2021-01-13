package bg.sofia.uni.fmi.mjt.wish.list.storage;

import bg.sofia.uni.fmi.mjt.wish.list.user.UserData;

import java.nio.channels.SocketChannel;
import java.util.Map;

public interface Storage {
    String getUsernameByChannel(SocketChannel channel);

    UserData getUserDataByUsername(String username);

    boolean isRegisteredStudent(String username);

    void registerStudent(String username, String password);

    void addWishToStudent(String username, String wish);

    void addActiveUser(SocketChannel channel, String username);

    void removeActiveUser(SocketChannel channel);

    int getSizeOfRegisteredStudents();

    Map<String, UserData> getRegisteredStudents();

    void removeWishesFromStudent(String username);
}
