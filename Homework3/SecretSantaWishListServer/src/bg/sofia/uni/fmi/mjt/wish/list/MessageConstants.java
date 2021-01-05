package bg.sofia.uni.fmi.mjt.wish.list;

public class MessageConstants {
    static final String DISCONNECT_FROM_SERVER_MESSAGE =
            "[ Disconnected from server ]";
    static final String ALREADY_LOGGED_IN_MESSAGE =
            "[ Already logged in ]";
    static final String NOT_LOGGED_IN_MESSAGE =
            "[ You are not logged in ]";
    static final String UNKNOWN_COMMAND_MESSAGE =
            "[ Unknown command ]";
    static final String USERNAME_TAKEN_MESSAGE =
            "[ Username %s is already taken, select another one ]";
    static final String INVALID_USERNAME_MESSAGE =
            "[ Username %s is invalid, select a valid one ]";
    static final String USER_SUCCESSFUL_REGISTERED_MESSAGE =
            "[ Username %s successfully registered ]";
    static final String USER_SUCCESSFUL_LOGGED_IN_MESSAGE =
            "[ User %s successfully logged in ]";
    static final String INVALID_COMBINATION_MESSAGE =
            "[ Invalid username/password combination ]";
    static final String SUCCESSFUL_LOGGED_OUT_MESSAGE =
            "[ Successfully logged out ]";
    static final String NOT_REGISTERED_USER_MESSAGE =
            "[ Student with username %s is not registered ]";
    static final String SAME_GIFT_FOR_STUDENT_SUBMITTED_MESSAGE =
            "[ The same gift for student %s was already submitted ]";
    static final String GIFT_SUBMITTED_SUCCESSFULLY_MESSAGE =
            "[ Gift %s for student %s submitted successfully ]";
    static final String NO_STUDENTS_IN_WISH_LIST_MESSAGE =
            "[ There are no students present in the wish list ]";
    static final String INCOMPLETE_COMMAND_MESSAGE =
            "[ Incomplete command! Please enter full command ]";

    private MessageConstants() {
    }
}
