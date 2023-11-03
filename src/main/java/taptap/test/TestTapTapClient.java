package taptap.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import taptap.client.TaptapBankClient;
import taptap.client.impl.TapTapBankClientImpl;
import taptap.config.ConfigurationLoader;
import taptap.config.TapTapClientConfig;
import taptap.exception.TapTapClientException;
import taptap.model.Recipient;
import taptap.model.Transaction;
import taptap.model.User;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.UUID;

public class TestTapTapClient {

    private static final Logger logger = LoggerFactory.getLogger(TestTapTapClient.class.getName());

    String configFile = "config.yaml";

    TapTapClientConfig clientConfigs;
    HttpClient httpClient;
    User user;
    Transaction transaction;

    public TestTapTapClient() {
        clientConfigs = ConfigurationLoader.loadConfiguration(configFile);
        httpClient = createHttpClient();
        user = createUniqueUser();
        transaction = createTransaction();
    }


    public static void main(String[] args) {

        TestTapTapClient testTapTapClient = new TestTapTapClient();
        testTapTapClient.testTapTapClient();
    }

    public void testTapTapClient() throws TapTapClientException {

        logger.info("Initiating tests");

        //create tap tap client
        TaptapBankClient tapTapBankClient = new TapTapBankClientImpl(clientConfigs, httpClient);

        logger.info("Testing adding user...");
        tapTapBankClient.addUser(user);

        logger.info("Testing making transaction...");
        tapTapBankClient.makeTransaction(user, transaction);

        logger.info("Testing user upgrade...");
        tapTapBankClient.upgradeUser(user);
    }

    private Transaction createTransaction() {

        String postfix = generateRandomUserPostfix();
        String firstName = "John" + postfix;
        String lastName = "Smith" + postfix;
        String email = "john" + postfix + "@mail.com";
        String phone = "0123456789";
        String nickName = "Johny";
        String walletName = "";
        double amount = 600;

        Recipient recipient = new Recipient(firstName, lastName, email, phone, nickName, walletName);
        return new Transaction(recipient, amount);
    }

    private static HttpClient createHttpClient() {
        return HttpClient.newBuilder().version(HttpClient.Version.HTTP_2) // Set HTTP version
                .followRedirects(HttpClient.Redirect.NORMAL) // Handle redirects
                .connectTimeout(Duration.ofSeconds(10)) // Set connection timeout
                .build();
    }

    public User createUniqueUser() {
        String postfix = generateRandomUserPostfix();
        String firstName = "John" + postfix;
        String lastName = "Smith" + postfix;
        String email = "john" + postfix + "@mail.com";
        String password = "securePassword";
        return new User(firstName, lastName, email, password);
    }


    public String generateRandomUserPostfix() {
        String postfix = UUID.randomUUID().toString().replaceAll("-", "");
        return postfix.substring(0, 3);
    }
}
