package taptap.client.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import taptap.client.TaptapBankClient;
import taptap.config.TaptapHttpClientConfigs;
import taptap.exception.InvalidResponseException;
import taptap.exception.TapTapClientException;
import taptap.model.Recipient;
import taptap.model.User;
import taptap.util.Utils;

import javax.xml.bind.JAXBException;

import org.jsoup.Jsoup;

public class TapTapBankClientImpl implements TaptapBankClient {

    private static final Logger logger = Logger.getLogger(TapTapBankClientImpl.class.getName());
    private final TaptapHttpClientConfigs taptapHttpClientConfigs;
    private final HttpClient httpClient;

    final String CONTENT_TYPE_XML = "application/xml";
    final String CONTENT_TYPE_JSON = "application/xml";

    final static String DEFAULT_RESPONSE_ERROR = "Response does not contain an error.";

    public TapTapBankClientImpl(TaptapHttpClientConfigs configs, HttpClient httpClient) {
        this.taptapHttpClientConfigs = configs;
        this.httpClient = httpClient;
    }

    @Override
    public boolean addUser(User user) {

        logger.log(Level.INFO,"Initiating add user workflow");
        boolean response = makeAddUserRequest(user);
        logger.log(Level.INFO,"User added successfully!");
        return response;
    }

    @Override
    public boolean makeTransaction(User user, Recipient recipient) {
        logger.log(Level.INFO,"Initiating make transaction workflow");
        boolean response = makeTransactionRequest(user,recipient);
        logger.log(Level.INFO,"Transaction made successfully!");
        return response;
    }

    @Override
    public boolean upgradeUser(User user) {
        return false;
    }

    private boolean makeAddUserRequest(User user) throws TapTapClientException {

        return makeRequest(taptapHttpClientConfigs.getCreateUserApi(), CONTENT_TYPE_XML, Utils.convertUserToXML(user));
    }

    private boolean makeTransactionRequest(User user, Recipient recipient) {


        return makeRequest(taptapHttpClientConfigs.getCreateUserApi(), CONTENT_TYPE_JSON, Utils.convertUserToXML(user));
    }


    public boolean makeRequest(String url, String contentType, String payload) throws TapTapClientException {

        logger.log(Level.FINE,String.format("Making requests with following parameters: URL: %s ; content-type: %s ; payload: %s", url,contentType,payload));

        url = taptapHttpClientConfigs.getHost() + ":" + taptapHttpClientConfigs.getPort() + url;

        try {

            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(payload)) // Set
                    .header("Content-Type", contentType) // Set the appropriate content type
                    .build();

            logger.log(Level.INFO, "Sending request.");

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logger.log(Level.FINE, String.format("Request completed successfully. Status : %s ,  Response body: %s",response.statusCode(),response.body()));

            return handleApiResponse(response);

        } catch (Exception e) {
            TapTapClientException tapClientException = new TapTapClientException(String.format("Failed making request for %s", url), e);
            logger.log(Level.SEVERE, String.format("Failed making request for %s", url), tapClientException);
            throw tapClientException;
        }
    }

    private static boolean handleApiResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        // Process the response as needed
        logger.log(Level.FINE, String.format("Response Status code : %s ;  Response %s", statusCode, response.body()));

        if (statusCode == 200) {
            logger.log(Level.INFO, "Request was successful.");
            return true;
        } else if (statusCode >= 400 && statusCode < 500) {

            //extract response from body
            String responseErrorMsg = extractResponseError(response.body());

            InvalidResponseException invalidResponseException = new InvalidResponseException(responseErrorMsg);
            logger.log(Level.SEVERE, invalidResponseException.toString(), invalidResponseException);
            throw invalidResponseException;

        } else if (statusCode >= 500 && statusCode < 600) {

            //extract response from body
            String responseErrorMsg = extractResponseError(response.body());

            InvalidResponseException invalidResponseException = new InvalidResponseException(responseErrorMsg);
            logger.log(Level.SEVERE, invalidResponseException.toString(), invalidResponseException);
            throw invalidResponseException;

        }

        //if status  is not in range, throw exception
        InvalidResponseException invalidResponseException = new InvalidResponseException("Request returned an invalid status code " + statusCode);
        logger.log(Level.SEVERE, invalidResponseException.toString(), invalidResponseException);
        throw invalidResponseException;
    }

    private static String extractResponseError(String body) {

        logger.log(Level.FINE,"Parsing response to extract error");
        Document doc = Jsoup.parse(body);
        Element preElement = doc.select("pre").first();

        if (preElement != null) {
            return preElement.text();
        }

        logger.log(Level.INFO,"Could not extract error from response, returning default error.");
        return DEFAULT_RESPONSE_ERROR;
    }
}
