package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class WishListServer implements AutoCloseable {
    private static final int BUFFER_SIZE = 1024;
    private static final int COMMAND_INDEX = 0;
    private static final int STUDENT_NAME_INDEX = 1;
    private static final int WISH_START_INDEX = 2;

    private Selector selector;
    private ByteBuffer buffer;
    private ServerSocketChannel serverSocketChannel;
    private boolean runServer = true;

    private Map<String, Set<String>> studentWishes;

    public WishListServer(int port) {
        try {
            this.selector = Selector.open();
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            this.serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            this.studentWishes = new HashMap<>();
        } catch (IOException e) {
            System.out.println("There is a problem with the network communication");
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
        } catch (IOException e) {
            System.out.println("There is a problem with the network communication");
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

    private void read(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();

        try {
            buffer.clear();
            int r = sc.read(buffer);
            if (r <= 0) {
                return;
            }
            buffer.flip();

            String message = StandardCharsets.UTF_8.decode(buffer).toString();
            String result = executeCommand(message);
            buffer.clear();
            buffer.put((result + System.lineSeparator()).getBytes());
            buffer.flip();
            sc.write(buffer);
        } catch (IOException e) {
            this.stop();
            e.printStackTrace();
        }

    }

    private String executeCommand(String receivedMessage) {
        String[] commandParts = receivedMessage.split(" ");

        if (commandParts[COMMAND_INDEX].equalsIgnoreCase("post-wish")) {
            Set<String> wishes;
            String wish = takeWish(commandParts);

            if (studentWishes.containsKey(commandParts[STUDENT_NAME_INDEX])) {
                if (studentWishes.get(commandParts[STUDENT_NAME_INDEX]).contains(wish)) {
                    return String.format("[ The same gift for student %s was already submitted ]",
                            commandParts[STUDENT_NAME_INDEX]);
                }
                wishes = studentWishes.get(commandParts[STUDENT_NAME_INDEX]);
            } else {
                wishes = new HashSet<>();
            }

            wishes.add(wish);
            studentWishes.put(commandParts[STUDENT_NAME_INDEX], wishes);
            return "[ Gift "
                    + wish.trim()
                    + " for student "
                    + commandParts[STUDENT_NAME_INDEX]
                    + " submitted successfully ]";

        }
        if (receivedMessage.trim().equals("get-wish")) {
            if (studentWishes.isEmpty()) {
                return "[ There are no students present in the wish list ]";
            }
            Random random = new Random();
            Object[] keys = studentWishes.keySet().toArray();
            int indexOfRandomStudent = random.nextInt(studentWishes.size());
            String name = String.format("%s: ", keys[indexOfRandomStudent]);
            String wishes = String.join(", ", studentWishes.get(keys[indexOfRandomStudent]));
            studentWishes.remove(keys[indexOfRandomStudent]);
            return "[ " + name + "[" + wishes + "]" + " ]";
        }
        return "[ Unknown command ]";
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }


    private String takeWish(String[] messageParts) {
        StringBuilder wish = new StringBuilder();
        for (int i = WISH_START_INDEX; i < messageParts.length - 1; ++i) {
            wish.append(messageParts[i]).append(" ");
        }
        wish.append(messageParts[messageParts.length - 1]);
        return wish.toString().trim();
    }
}
