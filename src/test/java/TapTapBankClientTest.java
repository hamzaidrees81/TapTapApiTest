import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import taptap.client.TaptapBankClient;
import taptap.client.impl.TapTapBankClientImpl;
import taptap.config.Config;
import taptap.exception.DataValidationException;
import taptap.exception.UserAlreadyExistsException;
import taptap.model.User;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TapTapBankClientTest {

    @Test
    public void testAddUserWithEmptyLastName() {

        // Create a mock for TaptapHttpClientConfigs and HttpClient
        Config clientConfigs = Mockito.mock(Config.class);

        HttpClient httpClient = Mockito.mock(HttpClient.class);

        // Create a unique user with an empty last name
        User user = createUniqueUserWithEmptyLastName();

        // Create the TapTapBankClient with mocked dependencies
        TaptapBankClient tapTapBankClient = new TapTapBankClientImpl(clientConfigs, httpClient);

        // Use assertThrows to check for the exception
        assertThrows(DataValidationException.class, () -> tapTapBankClient.addUser(user));
    }



    @Test
    public void testValidCreateUserScenario() throws Exception {

        // Create a mock HttpClient
        HttpClient httpClient = Mockito.mock(HttpClient.class);

        // Create a mock HttpResponse with a 200 status
        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("Success");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        Config taptapClientConfigs = Mockito.mock(Config.class);
        when(taptapClientConfigs.getHost()).thenReturn("http://ec2-44-202-225-54.compute-1.amazonaws.com");
        when(taptapClientConfigs.getPort()).thenReturn("8080");
        when(taptapClientConfigs.getCreateUserApi()).thenReturn("/create");

        // Create an instance of the class under test
        TaptapBankClient taptapBankClient = new TapTapBankClientImpl(taptapClientConfigs,httpClient);

        User user = createUniqueUser();
        boolean result = taptapBankClient.addUser(user);
        // Verify the result
        assertTrue(result);
    }
    @Test
    public void testDuplicateClientScenario() throws Exception {
        // Create a mock HttpClient
        HttpClient httpClient = Mockito.mock(HttpClient.class);

        // Create a mock HttpResponse with a 200 status
        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(500);
        when(response.body()).thenReturn("<!DOCTYPE html> <html lang=\"en\"> <head> <meta charset=\"utf-8\"> <title>Error</title> </head> <body> <pre>SQLITE_CONSTRAINT: UNIQUE constraint failed: user.email</pre> </body> </html>");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        Config taptapClientConfigs = Mockito.mock(Config.class);
        when(taptapClientConfigs.getHost()).thenReturn("http://ec2-44-202-225-54.compute-1.amazonaws.com");
        when(taptapClientConfigs.getPort()).thenReturn("8080");
        when(taptapClientConfigs.getCreateUserApi()).thenReturn("/create");


        TaptapBankClient taptapBankClient = new TapTapBankClientImpl(taptapClientConfigs,httpClient);
        User user = createUniqueUser();
        assertThrows(UserAlreadyExistsException.class, () -> taptapBankClient.addUser(user));
    }



    private User createUniqueUser() {
        // Create a User with an empty last name
        return new User("John", "Smith", "john@example.com", "password123");
    }

    private User createUniqueUserWithEmptyLastName() {
        // Create a User with an empty last name
        return new User("John", "", "john@example.com", "password123");
    }

}
