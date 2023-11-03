package taptap.client.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import taptap.client.TaptapBankClient;
import taptap.config.Config;
import taptap.exception.TapTapClientException;
import taptap.model.Transaction;
import taptap.model.User;
import taptap.util.HandleClientResponse;
import taptap.util.Utils;


import taptap.util.Validator;

import javax.validation.Valid;

import static taptap.model.Constants.*;

public class TapTapBankClientImpl implements TaptapBankClient {

    private static final Logger logger = LoggerFactory.getLogger(TapTapBankClientImpl.class.getName());
    private final Config config;
    private final HttpClient httpClient;



    public TapTapBankClientImpl(Config configs, HttpClient httpClient) {
        this.config = configs;
        this.httpClient = httpClient;
    }

    @Override
    public boolean addUser(@Valid User user) {

        Validator.validateUser(user);
        logger.info("Initiating add user workflow");
        boolean response = makeAddUserRequest(user);
        logger.info("User added successfully!");
        return response;
    }

    @Override
    public boolean makeTransaction(@Valid User user, @Valid Transaction transaction) {

        Validator.validateTransaction(transaction);
        logger.info("Initiating make transaction workflow");
        boolean response = makeTransactionRequest(user,transaction);
        logger.info("Transaction made successfully!");
        return response;
    }

    @Override
    public boolean upgradeUser(@Valid User user) {

        logger.info("Initiating user upgrade workflow");
        boolean response = makeUpgradeRequest(user);
        logger.info("User upgrade successfully!");
        return response;
    }

    private boolean makeUpgradeRequest(User user) {

        //prepare credentials from user
        String basicAuthCreds = Utils.prepareBasicAuthCreds(user);
        return makeRequest(config.getUpgradeUserApi(), CONTENT_TYPE_XML, "", basicAuthCreds, METHOD_POST);
    }

    private boolean makeAddUserRequest(User user) throws TapTapClientException {

        return makeRequest(config.getCreateUserApi(), CONTENT_TYPE_XML, Utils.convertUserToXML(user),null, METHOD_POST);
    }

    private boolean makeTransactionRequest(User user, Transaction transaction) {
        //prepare credentials from user
        String basicAuthCreds = Utils.prepareBasicAuthCreds(user);
        String apiEndpoint = config.getMakeTransactionApi().replace("{tid}",Utils.generateTransactionId());

        return makeRequest(apiEndpoint, CONTENT_TYPE_JSON, Utils.convertObjectToJson(transaction), basicAuthCreds, METHOD_PUT);
    }

    public boolean makeRequest(String url, String contentType, String payload, String credentials, String method) throws TapTapClientException {

        logger.debug(String.format("Making requests with following parameters: URL: %s ; content-type: %s ; payload: %s", url,contentType,payload));

        HttpRequest request = null;
        HttpResponse<String> response = null;

        try {

            request = prepareRequest(url, contentType, payload, credentials, method);

            logger.info( "Sending request.");

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logger.debug(String.format("Request completed successfully. Status : %s ,  Response body: %s",response.statusCode(),response.body()));

        } catch (Exception e) {
            TapTapClientException tapClientException = new TapTapClientException(String.format("Failed making request for %s", url), e);
            logger.error(String.format("Failed making request for %s", url), tapClientException);
            throw tapClientException;
        }


        return HandleClientResponse.handleApiResponse(response);
    }

    private HttpRequest prepareRequest(String url, String contentType, String payload, String credentials, String method) {

        String requestUrl = config.getHost() + ":" + config.getPort() + url;

        HttpRequest request;
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(requestUrl))
                .header("Content-Type", contentType); // Set the appropriate content type

        if(method.equals(METHOD_POST))
        {
            requestBuilder = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(payload));
        }
        else if(method.equals(METHOD_PUT))
        {
            requestBuilder = requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(payload));
        }

        if(credentials !=null&&!credentials.isEmpty())
            requestBuilder = requestBuilder.header("Authorization", "Basic " + credentials);


        request = requestBuilder.build();
        return request;
    }

}
