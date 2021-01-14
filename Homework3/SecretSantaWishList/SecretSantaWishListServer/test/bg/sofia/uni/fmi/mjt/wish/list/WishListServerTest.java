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
        String expectedResponse = "[ Username Alex successfully registered ]";
        String messageRequest = "register Alex Abcd1234";

        assertEquals("User must be successfully registered!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testRegisterCommandWithInvalidUsername() {
        String expectedResponse = "[ Username Alex$x is invalid, select a valid one ]";
        String messageRequest = "register Alex$x Abcd1234";

        assertEquals("User must not be successfully registered!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testRegisterCommandWithTakenUsername() {
        String expectedResponse = "[ Username Alex is already taken, select another one ]";
        String messageRequest = """
                register Alex Abcd1234
                register Alex Abcd"""
                .replaceAll("\n", System.lineSeparator());
        ;

        assertEquals("Username is already taken!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testRegisterCommandWithIncompleteInput() {
        String expectedResponse = "[ Incomplete command! Please enter full command ]";
        String messageRequest = "register Alex";

        assertEquals("Incomplete command!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testLoginCommandWithValidInput() {
        String expectedResponse = "[ User Alex successfully logged in ]";
        String messageRequest = """
                register Alex Abcd1234
                login Alex Abcd1234"""
                .replaceAll("\n", System.lineSeparator());

        assertEquals("User must be successfully logged in!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testLoginCommandWithInvalidUsername() {
        String expectedResponse = "[ Invalid username/password combination ]";
        String messageRequest = """
                register Alex 1234
                login Alexx 1234"""
                .replaceAll("\n", System.lineSeparator());

        assertEquals("Invalid username or password was entered!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testLoginCommandWithInvalidPassword() {
        String expectedResponse = "[ Invalid username/password combination ]";
        String messageRequest = """
                register Alex 1234
                login Alex 123456"""
                .replaceAll("\n", System.lineSeparator());

        assertEquals("Invalid username or password was entered!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }


    @Test
    public void testPostWishCommandWithValidInput() {
        String expectedResponse = "[ Gift cat for student Alex submitted successfully ]";
        String messageRequest = """
                register Alex 123
                login Alex 123
                post-wish Alex cat"""
                .replaceAll("\n", System.lineSeparator());

        assertEquals("Successfully post wish for existing user!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testPostWishCommandWithSameGift() {
        String expectedResponse = "[ The same gift for student Alex was already submitted ]";
        String messageRequest = """
                register Alex 123
                login Alex 123
                post-wish Alex cat
                post-wish Alex cat"""
                .replaceAll("\n", System.lineSeparator());


        assertEquals("The same gift was already submitted!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testPostWishCommandForNotRegisteredStudent() {
        String expectedResponse = "[ Student with username Sian is not registered ]";
        String messageRequest = """
                register Alex 123
                login Alex 123
                post-wish Sian dog"""
                .replaceAll("\n", System.lineSeparator());

        assertEquals("Post-wish for not registered user is not allowed!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testPostWishCommandWhenUserIsNotLoggedIn() {
        String expectedResponse = "[ You are not logged in ]";
        String messageRequest = """
                register Alex 123
                post-wish Alex dog"""
                .replaceAll("\n", System.lineSeparator());

        assertEquals("Post-wish is not allowed when user is not logged in!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testPostWishCommandWithIncompleteInput() {
        String expectedResponse = "[ Incomplete command! Please enter full command ]";
        String messageRequest = """
                register Alex 123
                login Alex 123
                post-wish Alex"""
                .replaceAll("\n", System.lineSeparator());

        assertEquals("Incomplete command!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testGetWishCommandWithOneWish() {
        String expectedResponse = "[ Sian: [dog] ]";
        String messageRequest = """
                register Alex 123
                register Sian 456
                login Alex 123
                post-wish Sian dog
                get-wish"""
                .replaceAll("\n", System.lineSeparator());

        assertEquals("Successfully get-wish!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testGetWishCommandWithMoreThanOneWish() {
        String messageRequest = """
                register Alex 123
                register Sian 456
                login Alex 123
                post-wish Sian dog
                post-wish Sian cat
                get-wish"""
                .replaceAll("\n", System.lineSeparator());
        String result = sendAndReceiveMessageFromServer(messageRequest);

        assertTrue("Wish must contain cat!", result.contains("cat"));
        assertTrue("Wish must contain dog!", result.contains("dog"));
    }

    @Test
    public void testGetWishCommandWithNoStudentsWithWishes() {
        String expectedResponse = "[ There are no students present in the wish list ]";
        String messageRequest = """
                register Alex 123
                login Alex 123
                get-wish"""
                .replaceAll("\n", System.lineSeparator());

        assertEquals("No student wishes in wish list!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testGetWishCommandWhenUserIsNotLoggedIn() {
        String expectedResponse = "[ You are not logged in ]";
        String messageRequest = "get-wish";

        assertEquals("Get-wish is not allowed when user is not logged in!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testLogoutCommandWithSuccess() {
        String expectedResponse = "[ Successfully logged out ]";
        String messageRequest = """
                register Alex 123
                login Alex 123
                logout"""
                .replaceAll("\n", System.lineSeparator());

        assertEquals("User must be successfully logged out!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testLogoutCommandWhenUserIsNotLoggedIn() {
        String expectedResponse = "[ You are not logged in ]";
        String messageRequest = "logout";

        assertEquals("User can not be logged out before logged in!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testDisconnectCommand() {
        String expectedResponse = "[ Disconnected from server ]";
        String messageRequest = "disconnect";

        assertEquals("User must be disconnected from server!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }

    @Test
    public void testUnknownCommand() {
        String expectedResponse = "[ Unknown command ]";
        String messageRequest = "something";

        assertEquals("Unknown command must be entered!",
                expectedResponse,
                sendAndReceiveMessageFromServer(messageRequest));
    }


    private String sendAndReceiveMessageFromServer(String message) {
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
