package taptap.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import taptap.exception.InvalidResponseException;
import taptap.exception.NotEnoughVolumeException;
import taptap.exception.TapTapClientException;
import taptap.exception.UserAlreadyExistsException;

import java.net.http.HttpResponse;

public class HandleClientResponse {

    final static String DEFAULT_RESPONSE_ERROR = "An error occurred but response has not mentioned the details.";
    private static final Logger logger = LoggerFactory.getLogger(HandleClientResponse.class.getName());

    public static boolean handleApiResponse(HttpResponse<String> response) {

        logger.info("Reading response...");

        int statusCode = response.statusCode();

        if (statusCode == 200) {
            logger.info("Request was successful.");
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

        //check if error can be made more meaningful if it comes from our list of expected errors
        throw throwRespectiveException(response.statusCode(),responseErrorMsg);

    }

    private static TapTapClientException throwRespectiveException(int statusCode, String responseErrorMsg) {

        //if response string matches our existing
        if(responseErrorMsg.contains("SQLITE_CONSTRAINT"))
        {
            UserAlreadyExistsException userAlreadyExistsException = new UserAlreadyExistsException("Error: Httpstatus: "+statusCode+" - error: User with this email already exists.");
            logger.error( userAlreadyExistsException.toString(), userAlreadyExistsException);
            throw userAlreadyExistsException;
        }

        if(responseErrorMsg.contains("Not enough volume "))
        {
            NotEnoughVolumeException notEnoughVolumeException = new NotEnoughVolumeException("Error: Httpstatus: "+statusCode+" - error: User's transaction volume is not enough to upgrade. It should be at least 50K.");
            logger.error( notEnoughVolumeException.toString(), notEnoughVolumeException);
            throw notEnoughVolumeException;
        }

        InvalidResponseException invalidResponseException = new InvalidResponseException("Error: Httpstatus: "+statusCode+" - error: "+responseErrorMsg);
        logger.error( invalidResponseException.toString(), invalidResponseException);
        throw invalidResponseException;

    }

    private static String extractResponseError(String responseBody) {

        logger.info("Parsing response to extract error");
        Document doc = Jsoup.parse(responseBody);
        Element preElement = doc.select("pre").first();

        if (preElement != null) {
            return preElement.text();
        }

        logger.info("Could not extract error from response, returning default error.");
        return DEFAULT_RESPONSE_ERROR;
    }
}
