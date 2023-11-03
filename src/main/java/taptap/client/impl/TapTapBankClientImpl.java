package taptap.client.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import taptap.client.TaptapBankClient;
import taptap.config.TaptapHttpClientConfigs;
import taptap.exception.InvalidResponseException;
import taptap.exception.TapTapClientException;
import taptap.model.Recipient;
import taptap.model.Transaction;
import taptap.model.User;
import taptap.util.Utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.bind.JAXBException;

import org.jsoup.Jsoup;

public class TapTapBankClientImpl implements TaptapBankClient {

    private static final Logger logger = Logger.getLogger(TapTapBankClientImpl.class.getName());
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private final TaptapHttpClientConfigs taptapHttpClientConfigs;
    private final HttpClient httpClient;

    final String CONTENT_TYPE_XML = "application/xml";
    final String CONTENT_TYPE_JSON = "application/json";

    final static String DEFAULT_RESPONSE_ERROR = "An error occurred but response has not mentioned the details.";

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
    public boolean makeTransaction(User user, Transaction transaction) {
        logger.log(Level.INFO,"Initiating make transaction workflow");
        boolean response = makeTransactionRequest(user,transaction);
        logger.log(Level.INFO,"Transaction made successfully!");
        return response;
    }

    @Override
    public boolean upgradeUser(User user) {
        return false;
    }

    private boolean makeAddUserRequest(User user) throws TapTapClientException {

        return makeRequest(taptapHttpClientConfigs.getCreateUserApi(), CONTENT_TYPE_XML, Utils.convertUserToXML(user),null, METHOD_POST);
    }

    private boolean makeTransactionRequest(User user, Transaction transaction) {
        //prepare credentials from user
        String basicAuthCreds = Utils.prepareBasicAuthCreds(user);
        String apiEndpoint = taptapHttpClientConfigs.getMakeTransactionApi().replace("{tid}",Utils.generateTransactionId());

        return makeRequest(apiEndpoint, CONTENT_TYPE_JSON, Utils.convertObjectToJson(transaction), basicAuthCreds, METHOD_PUT);
    }

    public boolean makeRequest(String url, String contentType, String payload, String credentials, String method) throws TapTapClientException {

        logger.log(Level.FINE,String.format("Making requests with following parameters: URL: %s ; content-type: %s ; payload: %s", url,contentType,payload));

        url = taptapHttpClientConfigs.getHost() + ":" + taptapHttpClientConfigs.getPort() + url;

        HttpRequest request = null;
        HttpResponse<String> response = null;

        try {

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(url))
                    .header("Content-Type", contentType); // Set the appropriate content type

            if(method.equals(METHOD_POST))
            {
                requestBuilder = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(payload));
            }
            else if(method.equals(METHOD_PUT))
            {
                requestBuilder = requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(payload));
            }

            if(credentials!=null&&!credentials.isEmpty())
                requestBuilder = requestBuilder.header("Authorization", "Basic " + credentials);


            request = requestBuilder.build();

            logger.log(Level.INFO, "Sending request.");

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logger.log(Level.FINE, String.format("Request completed successfully. Status : %s ,  Response body: %s",response.statusCode(),response.body()));

        } catch (Exception e) {
            TapTapClientException tapClientException = new TapTapClientException(String.format("Failed making request for %s", url), e);
            logger.log(Level.SEVERE, String.format("Failed making request for %s", url), tapClientException);
            throw tapClientException;
        }


        return handleApiResponse(response);
    }

    private static boolean handleApiResponse(HttpResponse<String> response) {

        logger.log(Level.INFO,"Reading response...");

        int statusCode = response.statusCode();
        //TODO REMOVE ME
        logger.info("PRINT RESPONSE BODY: "+response.body());

        if (statusCode == 200) {
            logger.log(Level.INFO, "Request was successful.");
            return true;
        }

        String responseErrorMsg = "";

         if (statusCode >= 400 && statusCode < 500) {
            if(statusCode == 404)
                responseErrorMsg = "The requested endpoint was not found.";
            else if(statusCode == 401)
                responseErrorMsg = "User credentails are invalid.";
            else
            //extract response from body
             responseErrorMsg = extractResponseError(response.body());

        } else if (statusCode >= 500 && statusCode < 600) {

             if(statusCode==503)
                 responseErrorMsg = "Service is unavailable. Please try again later.";
            else
             //extract response from body
                responseErrorMsg = extractResponseError(response.body());
        }
         else
             responseErrorMsg = "Failed. Request returned an invalid status code ";

        InvalidResponseException invalidResponseException = new InvalidResponseException("Error: Httpstatus: "+statusCode+" - error: "+responseErrorMsg);
        logger.log(Level.SEVERE, invalidResponseException.toString(), invalidResponseException);
        throw invalidResponseException;
    }

    private static String extractResponseError(String responseBody) {

        logger.log(Level.FINE,"Parsing response to extract error");
        Document doc = Jsoup.parse(responseBody);
        Element preElement = doc.select("pre").first();

        if (preElement != null) {
            return preElement.text();
        }

        logger.log(Level.INFO,"Could not extract error from response, returning default error.");
        return DEFAULT_RESPONSE_ERROR;
    }
}
