package bg.sofia.uni.fmi.mjt.wish.list.command.constants;

public interface MessageConstants {
    String DISCONNECT_FROM_SERVER_MESSAGE =
            "[ Disconnected from server ]";
    String ALREADY_LOGGED_IN_MESSAGE =
            "[ Already logged in ]";
    String NOT_LOGGED_IN_MESSAGE =
            "[ You are not logged in ]";
    String UNKNOWN_COMMAND_MESSAGE =
            "[ Unknown command ]";
    String USERNAME_TAKEN_MESSAGE =
            "[ Username %s is already taken, select another one ]";
    String INVALID_USERNAME_MESSAGE =
            "[ Username %s is invalid, select a valid one ]";
    String USER_SUCCESSFUL_REGISTERED_MESSAGE =
            "[ Username %s successfully registered ]";
    String USER_SUCCESSFUL_LOGGED_IN_MESSAGE =
            "[ User %s successfully logged in ]";
    String INVALID_COMBINATION_MESSAGE =
            "[ Invalid username/password combination ]";
    String SUCCESSFUL_LOGGED_OUT_MESSAGE =
            "[ Successfully logged out ]";
    String NOT_REGISTERED_USER_MESSAGE =
            "[ Student with username %s is not registered ]";
    String SAME_GIFT_FOR_STUDENT_SUBMITTED_MESSAGE =
            "[ The same gift for student %s was already submitted ]";
    String GIFT_SUBMITTED_SUCCESSFULLY_MESSAGE =
            "[ Gift %s for student %s submitted successfully ]";
    String NO_STUDENTS_IN_WISH_LIST_MESSAGE =
            "[ There are no students present in the wish list ]";
    String INCOMPLETE_COMMAND_MESSAGE =
            "[ Incomplete command! Please enter full command ]";
}
