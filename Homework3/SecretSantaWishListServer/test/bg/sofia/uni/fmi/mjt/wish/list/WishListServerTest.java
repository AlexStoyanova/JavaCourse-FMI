package bg.sofia.uni.fmi.mjt.wish.list;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WishListServerTest {

    private static final int SERVER_PORT = 8888;
    private static Thread serverStarterThread;
    private static WishListServer wishListServer;

    @Before
    public void setUp() {
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

    @After
    public void clean() {
        wishListServer.stop();
        serverStarterThread.interrupt();
    }

    @Test
    public void testRegisterCommandWithValidInput() {
        assertEquals("[ Username Alex successfully registered ]",
                sendAndReceiveOneMessageFromServer("register Alex Abcd1234"));
    }

    @Test
    public void testRegisterCommandWithInvalidUsername() {
        assertEquals("[ Username Alex$x is invalid, select a valid one ]",
                sendAndReceiveOneMessageFromServer("register Alex$x Abcd1234"));
    }

    @Test
    public void testRegisterCommandWithTakenUsername() {
        sendAndReceiveOneMessageFromServer("register Alex Abcd1234");
        assertEquals("[ Username Alex is already taken, select another one ]",
                sendAndReceiveOneMessageFromServer("register Alex Abcd"));
    }

    @Test
    public void testRegisterCommandWithIncompleteInput() {
        assertEquals("[ Incomplete command! Please enter full command ]",
                sendAndReceiveOneMessageFromServer("register Alex"));
    }

    @Test
    public void testLoginCommandWithValidInput() {
        sendAndReceiveOneMessageFromServer("register Alex Abcd1234");
        assertEquals("[ User Alex successfully logged in ]",
                sendAndReceiveOneMessageFromServer("login Alex Abcd1234"));
    }

    @Test
    public void testLoginCommandWithInvalidUsername() {
        sendAndReceiveOneMessageFromServer("register Alex 1234");
        assertEquals("[ Invalid username/password combination ]",
                sendAndReceiveOneMessageFromServer("login Alexx 1234"));
    }

    @Test
    public void testLoginCommandWithInvalidPassword() {
        sendAndReceiveOneMessageFromServer("register Alex 1234");
        assertEquals("[ Invalid username/password combination ]",
                sendAndReceiveOneMessageFromServer("login Alex 123456"));
    }

    @Test
    public void testPostWishCommandWithValidInput() {
        sendAndReceiveOneMessageFromServer("register Alex 123");
        assertEquals("[ Gift cat for student Alex submitted successfully ]",
                sendAndReceiveTwoMessagesFromServer("login Alex 123", "post-wish Alex cat"));
    }

    @Test
    public void testPostWishCommandWithSameGift() {
        sendAndReceiveOneMessageFromServer("register Alex 123");
        assertEquals("[ The same gift for student Alex was already submitted ]",
                sendAndReceiveThreeMessagesFromServer("login Alex 123",
                        "post-wish Alex cat", "post-wish Alex cat"));
    }

    @Test
    public void testPostWishCommandForNotRegisteredStudent() {
        sendAndReceiveOneMessageFromServer("register Alex 123");
        assertEquals("[ Student with username Sian is not registered ]",
                sendAndReceiveTwoMessagesFromServer("login Alex 123", "post-wish Sian dog"));
    }

    @Test
    public void testPostWishCommandWhenUserIsNotLoggedIn() {
        sendAndReceiveOneMessageFromServer("register Alex 123");
        assertEquals("[ You are not logged in ]",
                sendAndReceiveOneMessageFromServer("post-wish Alex cat"));
    }

    @Test
    public void testPostWishCommandWithIncompleteInput() {
        sendAndReceiveOneMessageFromServer("register Alex 123");
        assertEquals("[ Incomplete command! Please enter full command ]",
                sendAndReceiveTwoMessagesFromServer("login Alex 123", "post-wish Alex"));
    }

    @Test
    public void testGetWishCommandWithOneWish() {
        sendAndReceiveOneMessageFromServer("register Alex 123");
        sendAndReceiveOneMessageFromServer("register Sian 456");
        assertEquals("[ Sian: [dog] ]",
                sendAndReceiveThreeMessagesFromServer("login Alex 123",
                        "post-wish Sian dog", "get-wish"));
    }

    @Test
    public void testGetWishCommandWithMoreThanOneWish() {
        sendAndReceiveOneMessageFromServer("register Alex 123");
        sendAndReceiveOneMessageFromServer("register Sian 456");
        sendAndReceiveThreeMessagesFromServer("login Alex 123", "post-wish Sian dog", "logout");
        String result = sendAndReceiveThreeMessagesFromServer("login Alex 123",
                "post-wish Sian cat", "get-wish");
        assertTrue(result.contains("cat"));
        assertTrue(result.contains("dog"));
    }

    @Test
    public void testGetWishCommandWithNoStudentsWithWishes() {
        sendAndReceiveOneMessageFromServer("register Alex 123");
        assertEquals("[ There are no students present in the wish list ]",
                sendAndReceiveTwoMessagesFromServer("login Alex 123", "get-wish"));
    }

    @Test
    public void testGetWishCommandWhenUserIsNotLoggedIn() {
        assertEquals("[ You are not logged in ]",
                sendAndReceiveOneMessageFromServer("get-wish"));
    }

    @Test
    public void testLogoutCommandWithSuccess() {
        sendAndReceiveOneMessageFromServer("register Alex 123");
        assertEquals("[ Successfully logged out ]",
                sendAndReceiveTwoMessagesFromServer("login Alex 123", "logout"));
    }

    @Test
    public void testLogoutCommandWhenUserIsNotLoggedIn() {
        assertEquals("[ You are not logged in ]",
                sendAndReceiveOneMessageFromServer("logout"));
    }

    @Test
    public void testDisconnectCommand() {
        assertEquals("[ Disconnected from server ]",
                sendAndReceiveOneMessageFromServer("disconnect"));
    }

    @Test
    public void testUnknownCommand() {
        assertEquals("[ Unknown command ]",
                sendAndReceiveOneMessageFromServer("something"));
    }

    private String sendAndReceiveOneMessageFromServer(String message) {
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

    private String sendAndReceiveTwoMessagesFromServer(String firstMessage, String secondMessage) {
        String response = "fail";

        try (Socket socket = new Socket("localhost", SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream())) {

            out.println(firstMessage);
            out.flush();
            in.readLine();
            out.println(secondMessage);
            out.flush();
            response = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private String sendAndReceiveThreeMessagesFromServer(String firstMessage, String secondMessage, String thirdMessage) {
        String response = "fail";

        try (Socket socket = new Socket("localhost", SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream())) {

            out.println(firstMessage);
            out.flush();
            in.readLine();
            out.println(secondMessage);
            out.flush();
            in.readLine();
            out.println(thirdMessage);
            out.flush();
            response = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
