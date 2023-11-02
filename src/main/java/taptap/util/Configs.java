package taptap.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;

@Getter
public class Configs {

	String host;
	String port;
	String createUserApi;
	String makeTransactionApi;
	String upgradeUserApi;

	private static Configs config;

	public static Configs getInstance() {
		if (config == null) {
			config = new Configs();
			config.loadConfig();
		}

		return config;
	}

	private static final Logger logger = Logger.getLogger(Configs.class.getName());

	public void loadConfig() {

		Properties properties = new Properties();

		logger.log(Level.INFO, "Reading properties...");

		try {

			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream inputStream = classLoader.getResourceAsStream("config.properties");

			if (inputStream == null) {
				System.err.println("Property file not found!");
				return;
			}

			properties.load(inputStream);
			inputStream.close();

			// Access properties
			host = properties.getProperty("host");
			port = properties.getProperty("port");
			createUserApi = properties.getProperty("create_url_api");
			makeTransactionApi = properties.getProperty("make_transaction_api");
			upgradeUserApi = properties.getProperty("upgrade_user_api");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error in reading   properties: " + e);
		}
	}
}
