package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.ALREADY_LOGGED_IN_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.DISCONNECT_FROM_SERVER_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.GIFT_SUBMITTED_SUCCESSFULLY_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.INCOMPLETE_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.INVALID_COMBINATION_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.INVALID_USERNAME_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.NO_STUDENTS_IN_WISH_LIST_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.NOT_LOGGED_IN_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.NOT_REGISTERED_USER_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.SAME_GIFT_FOR_STUDENT_SUBMITTED_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.SUCCESSFUL_LOGGED_OUT_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.UNKNOWN_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.USER_SUCCESSFUL_LOGGED_IN_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.USER_SUCCESSFUL_REGISTERED_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.USERNAME_TAKEN_MESSAGE;

public class WishListServer implements AutoCloseable {
    private static final int BUFFER_SIZE = 1024;
    private static final int COMMAND_INDEX = 0;
    private static final int USERNAME_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;
    private static final int WISH_INDEX = 2;
    private static final int MAX_WORDS_FOR_COMMAND = 3;
    private static final String MESSAGE_PROBLEM_NETWORK_COMMUNICATION =
            "There is a problem with the network communication.";
    private static final String LINE_SEPARATORS = "\\r?\\n";
    private static final String WHITESPACES = "\\s+";
    private static final String VALID_USERNAME_SYMBOLS = "\\.|-|_|[a-z]|[A-Z]|[0-9]";

    private Selector selector;
    private ByteBuffer buffer;
    private ServerSocketChannel serverSocketChannel;
    private boolean runServer = true;

    private Map<String, UserData> registeredStudents;
    private Map<SocketChannel, String> usernameByChannel;

    public WishListServer(int port) {
        try {
            this.selector = Selector.open();
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.bind(new InetSocketAddress(port));
            this.registeredStudents = new ConcurrentHashMap<>();
            this.usernameByChannel = new ConcurrentHashMap<>();
        } catch (IOException e) {
            System.out.println(MESSAGE_PROBLEM_NETWORK_COMMUNICATION);
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (runServer) {
                int readyChannels = selector.select();
                if (readyChannels <= 0) {
                    continue;
                }
                handleReadyChannels();
            }
        } catch (IOException e) {
            System.out.println(MESSAGE_PROBLEM_NETWORK_COMMUNICATION);
            e.printStackTrace();
        }
    }

    public void stop() {
        runServer = false;
    }

    @Override
    public void close() throws Exception {
        serverSocketChannel.close();
        selector.close();
    }

    private void handleReadyChannels() throws IOException {
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (key.isReadable()) {
                this.read(key);
            } else if (key.isAcceptable()) {
                this.accept(key);
            }
            keyIterator.remove();
        }
    }

    private void read(SelectionKey key) {
        SocketChannel currentChannel = (SocketChannel) key.channel();

        try {
            buffer.clear();
            int r = currentChannel.read(buffer);
            if (r <= 0) {
                return;
            }
            buffer.flip();

            String message = StandardCharsets.UTF_8.decode(buffer).toString();
            String currentUsername = usernameByChannel.get(currentChannel);
            String result = executeCommand(currentChannel, message, currentUsername);
            buffer.clear();
            buffer.put((result + System.lineSeparator()).getBytes());
            buffer.flip();
            currentChannel.write(buffer);

            if (result.equals(DISCONNECT_FROM_SERVER_MESSAGE)) {
                currentChannel.close();
            }
        } catch (IOException e) {
            this.stop();
            e.printStackTrace();
        }
    }

    private String executeCommand(SocketChannel currentChannel, String receivedMessage, String username) {
        String[] commandParts = receivedMessage.replaceAll(LINE_SEPARATORS, "")
                .split(WHITESPACES, MAX_WORDS_FOR_COMMAND);

        switch (commandParts[COMMAND_INDEX]) {
            case "register" -> {
                if (username != null) {
                    return ALREADY_LOGGED_IN_MESSAGE;
                }
                if (commandParts.length < MAX_WORDS_FOR_COMMAND) {
                    return INCOMPLETE_COMMAND_MESSAGE;
                }
                return register(commandParts[USERNAME_INDEX], commandParts[PASSWORD_INDEX]);
            }
            case "login" -> {
                if (username != null) {
                    return ALREADY_LOGGED_IN_MESSAGE;
                }
                if (commandParts.length < MAX_WORDS_FOR_COMMAND) {
                    return INCOMPLETE_COMMAND_MESSAGE;
                }
                return login(commandParts[USERNAME_INDEX], commandParts[PASSWORD_INDEX], currentChannel);
            }
            case "logout" -> {
                if (username != null) {
                    return logout(currentChannel);
                }
                return NOT_LOGGED_IN_MESSAGE;
            }
            case "post-wish" -> {
                if (username != null) {
                    if (commandParts.length < MAX_WORDS_FOR_COMMAND) {
                        return INCOMPLETE_COMMAND_MESSAGE;
                    }
                    return postWish(commandParts[USERNAME_INDEX], commandParts[WISH_INDEX]);
                }
                return NOT_LOGGED_IN_MESSAGE;
            }
            case "get-wish" -> {
                if (username != null) {
                    return getWish(username);
                }
                return NOT_LOGGED_IN_MESSAGE;
            }
            case "disconnect" -> {
                if (username != null) {
                    usernameByChannel.remove(currentChannel);
                }
                return DISCONNECT_FROM_SERVER_MESSAGE;
            }
            default -> {
                return UNKNOWN_COMMAND_MESSAGE;
            }
        }
    }

    private String register(String username, String password) {
        if (registeredStudents.containsKey(username)) {
            return String.format(USERNAME_TAKEN_MESSAGE, username);
        } else if (!isValidUsername(username)) {
            return String.format(INVALID_USERNAME_MESSAGE, username);
        } else {
            registeredStudents.put(username, new UserData(password, new HashSet<>()));
            return String.format(USER_SUCCESSFUL_REGISTERED_MESSAGE, username);
        }
    }

    private String login(String username, String password, SocketChannel currentChannel) {
        if (registeredStudents.containsKey(username) && registeredStudents.get(username).password().equals(password)) {
            usernameByChannel.put(currentChannel, username);
            return String.format(USER_SUCCESSFUL_LOGGED_IN_MESSAGE, username);
        }
        return INVALID_COMBINATION_MESSAGE;
    }

    private String logout(SocketChannel currentChannel) {
        usernameByChannel.remove(currentChannel);
        return SUCCESSFUL_LOGGED_OUT_MESSAGE;
    }

    private String postWish(String username, String wish) {
        if (!registeredStudents.containsKey(username)) {
            return String.format(NOT_REGISTERED_USER_MESSAGE, username);
        }
        if (registeredStudents.get(username).wishes().contains(wish)) {
            return String.format(SAME_GIFT_FOR_STUDENT_SUBMITTED_MESSAGE, username);
        }
        registeredStudents.get(username).setWishes(wish);
        return String.format(GIFT_SUBMITTED_SUCCESSFULLY_MESSAGE, wish, username);
    }

    private String getWish(String username) {
        if (registeredStudents.size() > 1) {
            Set<Map.Entry<String, UserData>> usersWithData = registeredStudents.entrySet();
            Map<String, Set<String>> usersWithWishes = usersWithData.stream()
                    .filter(e -> !e.getKey().equals(username))
                    .filter(e -> !e.getValue().wishes().isEmpty())
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().wishes()));

            if (!usersWithWishes.isEmpty()) {
                Optional<Map.Entry<String, Set<String>>> studentWithWishes =
                        usersWithWishes.entrySet().stream().findAny();
                String wishes = String.join(", ", studentWithWishes.get().getValue());
                registeredStudents.get(studentWithWishes.get().getKey()).removeWishes();
                return "[ " + studentWithWishes.get().getKey() + ": " + "[" + wishes + "]" + " ]";
            }
        }
        return NO_STUDENTS_IN_WISH_LIST_MESSAGE;
    }

    private boolean isValidUsername(String username) {
        return username.replaceAll(VALID_USERNAME_SYMBOLS, "").length() == 0;
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args) {
        WishListServer server = new WishListServer(8888);
        server.start();
    }
}
