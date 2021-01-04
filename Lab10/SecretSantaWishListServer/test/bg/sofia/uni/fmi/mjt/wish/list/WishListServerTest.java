package bg.sofia.uni.fmi.mjt.wish.list;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class WishListServerTest {

    private static final int SERVER_PORT = 8888;
    private static Thread serverStarterThread;
    private static WishListServer wishListServer;

    @BeforeClass
    public static void setUp() {
        serverStarterThread = new Thread(() -> {
            try (WishListServer wls = new WishListServer(SERVER_PORT)) {
                wishListServer = wls;
                wishListServer.start();
            } catch (Exception e) {
                System.out.println("An error has occured");
                e.printStackTrace();
            }
        });
        serverStarterThread.start();
    }

    @AfterClass
    public static void clean() {
        wishListServer.stop();
        serverStarterThread.interrupt();
    }

    @Test
    public void testPostWishCommandWithCorrectDataWishOneWordWish() {
        assertEquals("[ Gift kolelo for student Zdravko submitted successfully ]",
                receiveMessageFromServer("post-wish Zdravko kolelo"));
        receiveMessageFromServer("get-wish");
    }

    @Test
    public void testPostWishCommandCorrectDataWithMoreThanOneWordWish() {
        assertEquals("[ Gift kolelo s chervena ramka for student Zdravko submitted successfully ]",
                receiveMessageFromServer("post-wish Zdravko kolelo s chervena ramka"));
        receiveMessageFromServer("get-wish");
    }

    @Test
    public void testPostWishWithTheSameGift() {
        receiveMessageFromServer("post-wish Alex cat");
        assertEquals("[ The same gift for student Alex was already submitted ]",
                receiveMessageFromServer("post-wish Alex cat"));
        receiveMessageFromServer("get-wish");
    }

    @Test
    public void testGetWishWithOneWish() {
        receiveMessageFromServer("post-wish Alex cat");
        assertEquals("[ Alex: [cat] ]", receiveMessageFromServer("get-wish"));
    }

    @Test
    public void testGetWishWithMoreThanOneWish() {
        receiveMessageFromServer("post-wish Alex cat");
        receiveMessageFromServer("post-wish Alex dog");
        assertEquals("[ Alex: [cat, dog] ]", receiveMessageFromServer("get-wish"));
    }

    @Test
    public void testGetWishWithoutStudentsInList() {
        assertEquals("[ There are no students present in the wish list ]", receiveMessageFromServer("get-wish"));
    }

    @Test
    public void testUnknownCommand() {
        assertEquals("[ Unknown command ]", receiveMessageFromServer("some-command"));
    }

    private String receiveMessageFromServer(String message) {
        String response = "fail";

        try (Socket socket = new Socket("localhost", SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream())) {

            out.println(message);
            out.flush();
            response = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
