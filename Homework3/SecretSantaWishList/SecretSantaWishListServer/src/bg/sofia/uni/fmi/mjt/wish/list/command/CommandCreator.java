package bg.sofia.uni.fmi.mjt.wish.list.command;

import java.util.Arrays;

public class CommandCreator {
    private static final String LINE_SEPARATORS = "\\r?\\n";
    private static final String WHITESPACES = "\\s+";
    private static final int MAX_WORDS_FOR_COMMAND = 3;

    public static Command newCommand(String clientInput) {
        String[] tokens = clientInput.replaceAll(LINE_SEPARATORS, "")
                .split(WHITESPACES, MAX_WORDS_FOR_COMMAND);

        String[] arguments = Arrays.stream(tokens, 1, tokens.length)
                .toArray(String[]::new);

        return new Command(tokens[0], arguments);
    }
}
