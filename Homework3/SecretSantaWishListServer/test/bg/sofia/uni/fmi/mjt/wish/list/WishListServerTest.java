package bg.sofia.uni.fmi.mjt.wish.list;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WishListServerTest {

    private static final int SERVER_PORT = 8888;
    private static Thread serverStarterThread;
    private static WishListServer wishListServer;

    @Before
    public void setUp() throws InterruptedException {
        serverStarterThread = new Thread(() -> {
            try (WishListServer wls = new WishListServer(SERVER_PORT)) {
                wishListServer = wls;
                wishListServer.start();
            } catch (Exception e) {
                System.out.println("An error has occurred");
                e.printStackTrace();
            }
        });
        serverStarterThread.start();
        Thread.sleep(200);
    }

    @After
    public void clean() throws InterruptedException {
        wishListServer.stop();
        serverStarterThread.interrupt();
        Thread.sleep(200);
    }

    @Test
    public void testRegisterCommandWithValidInput() {
        assertEquals("User must be successfully registered!",
                "[ Username Alex successfully registered ]",
                sendAndReceiveMessageFromServer("register Alex Abcd1234"));
    }

    @Test
    public void testRegisterCommandWithInvalidUsername() {
        assertEquals("User must not be successfully registered!",
                "[ Username Alex$x is invalid, select a valid one ]",
                sendAndReceiveMessageFromServer("register Alex$x Abcd1234"));
    }

    @Test
    public void testRegisterCommandWithTakenUsername() {
        assertEquals("Username is already taken!",
                "[ Username Alex is already taken, select another one ]",
                sendAndReceiveMessageFromServer("register Alex Abcd1234"
                        + System.lineSeparator()
                        + "register Alex Abcd"));
    }

    @Test
    public void testRegisterCommandWithIncompleteInput() {
        assertEquals("Incomplete command!",
                "[ Incomplete command! Please enter full command ]",
                sendAndReceiveMessageFromServer("register Alex"));
    }

    @Test
    public void testLoginCommandWithValidInput() {
        assertEquals("User must be successfully logged in!",
                "[ User Alex successfully logged in ]",
                sendAndReceiveMessageFromServer("register Alex Abcd1234"
                        + System.lineSeparator()
                        + "login Alex Abcd1234"));
    }

    @Test
    public void testLoginCommandWithInvalidUsername() {
        assertEquals("Invalid username or password was entered!",
                "[ Invalid username/password combination ]",
                sendAndReceiveMessageFromServer("register Alex 1234"
                        + System.lineSeparator()
                        + "login Alexx 1234"));
    }

    @Test
    public void testLoginCommandWithInvalidPassword() {
        assertEquals("Invalid username or password was entered!",
                "[ Invalid username/password combination ]",
                sendAndReceiveMessageFromServer("register Alex 1234"
                        + System.lineSeparator()
                        + "login Alex 123456"));
    }


    @Test
    public void testPostWishCommandWithValidInput() {
        assertEquals("Successfully post wish for existing user!",
                "[ Gift cat for student Alex submitted successfully ]",
                sendAndReceiveMessageFromServer("register Alex 123"
                        + System.lineSeparator()
                        + "login Alex 123"
                        + System.lineSeparator()
                        + "post-wish Alex cat"));
    }

    @Test
    public void testPostWishCommandWithSameGift() {
        assertEquals("The same gift was already submitted!",
                "[ The same gift for student Alex was already submitted ]",
                sendAndReceiveMessageFromServer("register Alex 123"
                        + System.lineSeparator()
                        + "login Alex 123"
                        + System.lineSeparator()
                        + "post-wish Alex cat"
                        + System.lineSeparator()
                        + "post-wish Alex cat"));
    }

    @Test
    public void testPostWishCommandForNotRegisteredStudent() {
        assertEquals("Post-wish for not registered user is not allowed!",
                "[ Student with username Sian is not registered ]",
                sendAndReceiveMessageFromServer("register Alex 123"
                        + System.lineSeparator()
                        + "login Alex 123"
                        + System.lineSeparator()
                        + "post-wish Sian dog"));
    }

    @Test
    public void testPostWishCommandWhenUserIsNotLoggedIn() {
        assertEquals("Post-wish is not allowed when user is not logged in!",
                "[ You are not logged in ]",
                sendAndReceiveMessageFromServer("register Alex 123"
                        + System.lineSeparator()
                        + "post-wish Alex cat"));
    }

    @Test
    public void testPostWishCommandWithIncompleteInput() {
        assertEquals("Incomplete command!",
                "[ Incomplete command! Please enter full command ]",
                sendAndReceiveMessageFromServer("register Alex 123"
                        + System.lineSeparator()
                        + "login Alex 123"
                        + System.lineSeparator()
                        + "post-wish Alex"));
    }

    @Test
    public void testGetWishCommandWithOneWish() {
        assertEquals("Successfully get-wish!",
                "[ Sian: [dog] ]",
                sendAndReceiveMessageFromServer("register Alex 123"
                        + System.lineSeparator()
                        + "register Sian 456"
                        + System.lineSeparator()
                        + "login Alex 123"
                        + System.lineSeparator()
                        + "post-wish Sian dog"
                        + System.lineSeparator()
                        + "get-wish"));
    }

    @Test
    public void testGetWishCommandWithMoreThanOneWish() {
        String result = sendAndReceiveMessageFromServer("register Alex 123"
                + System.lineSeparator()
                + "register Sian 456"
                + System.lineSeparator()
                + "login Alex 123"
                + System.lineSeparator()
                + "post-wish Sian dog"
                + System.lineSeparator()
                + "post-wish Sian cat"
                + System.lineSeparator()
                + "get-wish");

        assertTrue("Wish must contain cat!", result.contains("cat"));
        assertTrue("Wish must contain dog!", result.contains("dog"));
    }

    @Test
    public void testGetWishCommandWithNoStudentsWithWishes() {
        assertEquals("No student wishes in wish list!",
                "[ There are no students present in the wish list ]",
                sendAndReceiveMessageFromServer("register Alex 123"
                        + System.lineSeparator()
                        + "login Alex 123"
                        + System.lineSeparator()
                        + "get-wish"));
    }

    @Test
    public void testGetWishCommandWhenUserIsNotLoggedIn() {
        assertEquals("Get-wish is not allowed when user is not logged in!",
                "[ You are not logged in ]",
                sendAndReceiveMessageFromServer("get-wish"));
    }

    @Test
    public void testLogoutCommandWithSuccess() {
        assertEquals("User must be successfully logged out!",
                "[ Successfully logged out ]",
                sendAndReceiveMessageFromServer("register Alex 123"
                        + System.lineSeparator()
                        + "login Alex 123"
                        + System.lineSeparator()
                        + "logout"));
    }

    @Test
    public void testLogoutCommandWhenUserIsNotLoggedIn() {
        assertEquals("User can not be logged out before logged in!",
                "[ You are not logged in ]",
                sendAndReceiveMessageFromServer("logout"));
    }

    @Test
    public void testDisconnectCommand() {
        assertEquals("User must be disconnected from server!",
                "[ Disconnected from server ]",
                sendAndReceiveMessageFromServer("disconnect"));
    }

    @Test
    public void testUnknownCommand() {
        assertEquals("Unknown command must be entered!",
                "[ Unknown command ]",
                sendAndReceiveMessageFromServer("something"));
    }


    private synchronized String sendAndReceiveMessageFromServer(String message) {
        String response = "fail";
        try (Socket socket = new Socket(InetAddress.getLocalHost(), SERVER_PORT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader readerOfMessage = new BufferedReader(new StringReader(message))) {

            String line;
            while ((line = readerOfMessage.readLine()) != null) {
                writer.println(line);
                response = reader.readLine();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
