package bg.sofia.uni.fmi.mjt.wish.list.command;

import bg.sofia.uni.fmi.mjt.wish.list.user.UserData;
import bg.sofia.uni.fmi.mjt.wish.list.storage.Storage;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.ALREADY_LOGGED_IN_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.DISCONNECT_FROM_SERVER_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.GIFT_SUBMITTED_SUCCESSFULLY_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.INCOMPLETE_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.INVALID_COMBINATION_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.INVALID_USERNAME_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.NO_STUDENTS_IN_WISH_LIST_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.NOT_LOGGED_IN_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.NOT_REGISTERED_USER_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.SAME_GIFT_FOR_STUDENT_SUBMITTED_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.SUCCESSFUL_LOGGED_OUT_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.UNKNOWN_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.USER_SUCCESSFUL_LOGGED_IN_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.USER_SUCCESSFUL_REGISTERED_MESSAGE;
import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.USERNAME_TAKEN_MESSAGE;

public class CommandExecutor {

    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    private static final String LOGOUT = "logout";
    private static final String POST_WISH = "post-wish";
    private static final String GET_WISH = "get-wish";
    private static final String DISCONNECT = "disconnect";
    private static final String VALID_USERNAME_SYMBOLS = "\\.|-|_|[a-z]|[A-Z]|[0-9]";
    private static final int MAX_COMMAND_ARGUMENTS = 2;

    private final Storage storage;

    public CommandExecutor(Storage storage) {
        this.storage = storage;
    }

    public String execute(Command cmd, SocketChannel clientChannel) {
        String currentUser = storage.getUsernameByChannel(clientChannel);

        switch (cmd.command()) {
            case REGISTER -> {
                return register(cmd.arguments(), currentUser);
            }
            case LOGIN -> {
                return login(cmd.arguments(), clientChannel, currentUser);
            }
            case LOGOUT -> {
                return logout(clientChannel, currentUser);
            }
            case POST_WISH -> {
                return postWish(cmd.arguments(), currentUser);
            }
            case GET_WISH -> {
                return getWish(currentUser);
            }
            case DISCONNECT -> {
                return disconnect(clientChannel, currentUser);
            }
            default -> {
                return UNKNOWN_COMMAND_MESSAGE;
            }
        }
    }

    private String register(String[] arguments, String currentUser) {
        if (isCurrentUserLoggedIn(currentUser)) {
            return ALREADY_LOGGED_IN_MESSAGE;
        }
        if (!isValidNumberOfCommandArguments(arguments)) {
            return INCOMPLETE_COMMAND_MESSAGE;
        }

        String username = arguments[0];
        String password = arguments[1];

        if (storage.isRegisteredStudent(username)) {
            return String.format(USERNAME_TAKEN_MESSAGE, username);
        } else if (!isValidUsername(username)) {
            return String.format(INVALID_USERNAME_MESSAGE, username);
        } else {
            storage.registerStudent(username, password);
            return String.format(USER_SUCCESSFUL_REGISTERED_MESSAGE, username);
        }
    }

    private String login(String[] arguments, SocketChannel clientChannel, String currentUser) {
        if (isCurrentUserLoggedIn(currentUser)) {
            return ALREADY_LOGGED_IN_MESSAGE;
        }
        if (!isValidNumberOfCommandArguments(arguments)) {
            return INCOMPLETE_COMMAND_MESSAGE;
        }

        String username = arguments[0];
        String password = arguments[1];

        if (storage.isRegisteredStudent(username)
                && storage.getUserDataByUsername(username).password().equals(password)) {

            storage.addActiveUser(clientChannel, username);
            return String.format(USER_SUCCESSFUL_LOGGED_IN_MESSAGE, username);
        }
        return INVALID_COMBINATION_MESSAGE;
    }

    private String logout(SocketChannel clientChannel, String currentUser) {
        if (!isCurrentUserLoggedIn(currentUser)) {
            return NOT_LOGGED_IN_MESSAGE;
        }

        storage.removeActiveUser(clientChannel);
        return SUCCESSFUL_LOGGED_OUT_MESSAGE;
    }

    private String postWish(String[] arguments, String currentUser) {
        if (!isCurrentUserLoggedIn(currentUser)) {
            return NOT_LOGGED_IN_MESSAGE;
        }

        if (!isValidNumberOfCommandArguments(arguments)) {
            return INCOMPLETE_COMMAND_MESSAGE;
        }

        String username = arguments[0];
        String wish = arguments[1];

        if (!storage.isRegisteredStudent(username)) {
            return String.format(NOT_REGISTERED_USER_MESSAGE, username);
        }
        if (storage.getUserDataByUsername(username).wishes().contains(wish)) {
            return String.format(SAME_GIFT_FOR_STUDENT_SUBMITTED_MESSAGE, username);
        }
        storage.addWishToStudent(username, wish);
        return String.format(GIFT_SUBMITTED_SUCCESSFULLY_MESSAGE, wish, username);
    }

    private String getWish(String currentUser) {
        if (!isCurrentUserLoggedIn(currentUser)) {
            return NOT_LOGGED_IN_MESSAGE;
        }

        if (hasStudentsInWishList()) {
            Set<Map.Entry<String, UserData>> usersWithData = storage.getRegisteredStudents().entrySet();
            Map<String, Set<String>> usersWithWishes = usersWithData.stream()
                    .filter(e -> !e.getKey().equals(currentUser)
                            && !e.getValue().wishes().isEmpty())
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().wishes()));

            if (!usersWithWishes.isEmpty()) {
                Optional<Map.Entry<String, Set<String>>> studentWithWishes =
                        usersWithWishes.entrySet().stream().findAny();
                String wishes = String.join(", ", studentWithWishes.get().getValue());
                storage.removeWishesFromStudent(studentWithWishes.get().getKey());
                return "[ " + studentWithWishes.get().getKey() + ": " + "[" + wishes + "]" + " ]";
            }
        }
        return NO_STUDENTS_IN_WISH_LIST_MESSAGE;
    }

    private String disconnect(SocketChannel clientChannel, String currentUser) {
        if (isCurrentUserLoggedIn(currentUser)) {
            storage.removeActiveUser(clientChannel);
        }
        return DISCONNECT_FROM_SERVER_MESSAGE;
    }

    private boolean isValidUsername(String username) {
        return username.replaceAll(VALID_USERNAME_SYMBOLS, "").length() == 0;
    }

    private boolean isValidNumberOfCommandArguments(String[] arguments) {
        return arguments.length == MAX_COMMAND_ARGUMENTS;
    }

    private boolean isCurrentUserLoggedIn(String currentUser) {
        return currentUser != null;
    }

    private boolean hasStudentsInWishList() {
        return storage.getSizeOfRegisteredStudents() > 1;
    }
}
