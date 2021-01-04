package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class WishListClient {
    private static final String SERVER_HOST = "localhost";
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    private int port;

    public WishListClient(int port) {
        this.port = port;
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
                Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, port));

            while (true) {
                String message = scanner.nextLine();

                if ("disconnect".equalsIgnoreCase(message)) {
                    System.out.println("[ Disconnected from server ]");
                    break;
                }

                buffer.clear();
                buffer.put(message.getBytes());
                buffer.flip();
                socketChannel.write(buffer);

                buffer.clear();
                socketChannel.read(buffer);
                buffer.flip();

                byte[] byteArray = new byte[buffer.remaining()];
                buffer.get(byteArray);
                String reply = new String(byteArray, "UTF-8");
                System.out.println(reply);
            }

        } catch (IOException e) {
            System.out.println("There is a problem with the network communication");
            e.printStackTrace();
        }
        System.out.println("Client stopped");
    }
}
