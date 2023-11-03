package taptap.test;

import taptap.client.impl.TapTapBankClientImpl;
import taptap.client.TaptapBankClient;
import taptap.config.ConfigurationLoader;
import taptap.config.TaptapHttpClientConfigs;
import taptap.exception.TapTapClientException;
import taptap.model.User;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	private static final Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {

		Main main = new Main();
		main.test();
	}

	public void test() throws TapTapClientException
	{

		logger.log(Level.INFO,"Initiating tests");

		//load configurations
		String configFile = "config.yaml";

		TaptapHttpClientConfigs clientConfigs = null;
		clientConfigs = ConfigurationLoader.loadConfiguration(configFile);

		//create http client
		HttpClient httpClient = createHttpClient();

		//create tap tap client
		TaptapBankClient tapTapBankClient = new TapTapBankClientImpl(clientConfigs,httpClient);

		User user = createUniqueUser();
		tapTapBankClient.addUser(user);
	}

	private static HttpClient createHttpClient() {
		HttpClient httpClient =  HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_2) // Set HTTP version
				.followRedirects(HttpClient.Redirect.NORMAL) // Handle redirects
				.connectTimeout(Duration.ofSeconds(10)) // Set connection timeout
				.build();
		return httpClient;
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
		// Take the first 10 characters of the UUID
		return postfix.substring(0, 3);
	}

	public Main()
	{
		// Load the logging configuration from the logging.properties file
		String logPropsPath = TaptapHttpClientConfigs.class.getClassLoader().getResource("").getPath()+"logging.properties";
		System.setProperty("java.util.logging.config.file", logPropsPath);
	}

}
