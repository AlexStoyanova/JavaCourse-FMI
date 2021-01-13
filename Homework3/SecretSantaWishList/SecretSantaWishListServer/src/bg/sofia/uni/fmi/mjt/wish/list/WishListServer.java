package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.wish.list.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.wish.list.storage.InMemoryStorage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.wish.list.command.constants.MessageConstants.DISCONNECT_FROM_SERVER_MESSAGE;

public class WishListServer implements AutoCloseable {
    private static final int BUFFER_SIZE = 1024;
    private static final String MESSAGE_PROBLEM_NETWORK_COMMUNICATION =
            "There is a problem with the network communication.";

    private Selector selector;
    private ByteBuffer buffer;
    private ServerSocketChannel serverSocketChannel;
    private CommandExecutor commandExecutor;
    private boolean runServer = true;

    public WishListServer(int port) {
        try {
            this.selector = Selector.open();
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.bind(new InetSocketAddress(port));
            this.commandExecutor = new CommandExecutor(new InMemoryStorage());
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
        if (selector.isOpen()) {
            selector.wakeup();
        }
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
                this.handleReadableKey(key);
            } else if (key.isAcceptable()) {
                this.handleAcceptKey(key);
            }
            keyIterator.remove();
        }
    }

    private void handleReadableKey(SelectionKey key) {
        SocketChannel currentChannel = (SocketChannel) key.channel();
        try {
            String messageFromClient = readInputFromClient(currentChannel);
            if (messageFromClient == null) {
                return;
            }

            writeOutputForClient(currentChannel, messageFromClient);
            if (messageFromClient.equals(DISCONNECT_FROM_SERVER_MESSAGE)) {
                currentChannel.close();
            }
        } catch (IOException e) {
            this.stop();
            e.printStackTrace();
        }
    }

    private String readInputFromClient(SocketChannel currentChannel) throws IOException {
        buffer.clear();
        int r = currentChannel.read(buffer);
        if (r <= 0) {
            currentChannel.close();
            return null;
        }
        buffer.flip();

        String message = StandardCharsets.UTF_8.decode(buffer).toString();
        return commandExecutor.execute(CommandCreator.newCommand(message), currentChannel);
    }

    private void writeOutputForClient(SocketChannel currentChannel, String outputMessage) throws IOException {
        buffer.clear();
        buffer.put((outputMessage + System.lineSeparator()).getBytes());
        buffer.flip();
        currentChannel.write(buffer);
    }

    private void handleAcceptKey(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }
}
