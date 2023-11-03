package taptap.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import taptap.client.TaptapBankClient;
import taptap.client.impl.TapTapBankClientImpl;
import taptap.config.ConfigurationLoader;
import taptap.config.Config;
import taptap.exception.TapTapClientException;
import taptap.model.Recipient;
import taptap.model.Transaction;
import taptap.model.User;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.UUID;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class.getName());

	public static void main(String[] args) {

		Main main = new Main();
		main.test();
	}

	public void test() throws TapTapClientException
	{

		logger.info("Initiating tests");
		logger.debug("Initiating tests in debug");

		//load configurations
		String configFile = "config.yaml";

		Config clientConfigs = null;
		clientConfigs = ConfigurationLoader.loadConfiguration(configFile);

		//create http client
		HttpClient httpClient = createHttpClient();


		User user = createUniqueUser();
		Transaction transaction = createTransaction();

		//create tap tap client
		TaptapBankClient tapTapBankClient = new TapTapBankClientImpl(clientConfigs,httpClient);
		tapTapBankClient.addUser(user);
		tapTapBankClient.makeTransaction(user,transaction);
		tapTapBankClient.upgradeUser(user);
	}

	private Transaction createTransaction() {

		String postfix = generateRandomUserPostfix();
		String firstName = "John"+postfix;
		String lastName = "Smith"+postfix;
		String email = "john"+postfix+"@mail.com";
		String phone = "0123456789";
		String nickName = "Johny";
		String walletName = "";
		double amount = 500;

		Recipient recipient = new Recipient(firstName,lastName,email,phone,nickName,walletName);
		return  new Transaction(recipient,amount);
	}

	private static HttpClient createHttpClient() {
        return HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_2) // Set HTTP version
				.followRedirects(HttpClient.Redirect.NORMAL) // Handle redirects
				.connectTimeout(Duration.ofSeconds(10)) // Set connection timeout
				.build();
	}

	public User createUniqueUser()
	{
		String postfix = generateRandomUserPostfix();
		String firstName = "John"+postfix;
		String lastName = "Smith"+postfix;
		String email = "john"+postfix+"@mail.com";
		String password = "securePassword";
        return new User(firstName, lastName, email, password);
	}


	public   String generateRandomUserPostfix() {
		String postfix = UUID.randomUUID().toString().replaceAll("-", "");
		return postfix.substring(0, 3);
	}

	public Main()
	{
		// Load the logging configuration from the logging.properties file
		String logPropsPath = Config.class.getClassLoader().getResource("").getPath()+"logging.properties";
		System.setProperty("java.util.logging.config.file", logPropsPath);
	}

}
