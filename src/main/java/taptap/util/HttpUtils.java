package taptap.util;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpUtils {

	private static final Logger logger = Logger.getLogger(HttpUtils.class.getName());
	static HttpClient httpClient = null;

	public enum HttpStatus {
		OK(200, "OK"), CREATED(201, "Created"), NO_CONTENT(204, "No Content"), BAD_REQUEST(400, "Bad Request"),
		UNAUTHORIZED(401, "Unauthorized"), FORBIDDEN(403, "Forbidden"), NOT_FOUND(404, "Not Found"),
		INTERNAL_SERVER_ERROR(500, "Internal Server Error");

		private final int code;
		private final String reasonPhrase;

		HttpStatus(int code, String reasonPhrase) {
			this.code = code;
			this.reasonPhrase = reasonPhrase;
		}

		public int getCode() {
			return code;
		}

		public String getReasonPhrase() {
			return reasonPhrase;
		}

		@Override
		public String toString() {
			return code + " " + reasonPhrase;
		}
	}

	public static void setHttpClient(HttpClient httpClient) {
		HttpUtils.httpClient = httpClient;
	}

	public static Integer makeRequest(String url, String contentType, String payload) {

		if(httpClient == null)
			httpClient =  HttpClient.newHttpClient();
		
		url = Configs.getInstance().getHost() + ":" + Configs.getInstance().getPort() + url;

		Integer statusCode = null;

		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(payload)) // Set
				.header("Content-Type", contentType) // Set the appropriate content type
				.build();
		
		logger.log(Level.INFO,"Sending create user request.");

		
		
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			statusCode = response.statusCode();
			// Process the response as needed
			logger.log(Level.INFO,String.format("Status code is %s and Response is %s", statusCode, response.body()));

		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Failed making request for %s", url), e);
		}

		return statusCode;
	}

}
