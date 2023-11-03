package taptap.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import taptap.exception.TapTapClientException;
import taptap.model.User;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class.getName());

    public static String generateTransactionId() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        // Take the first 10 characters of the UUID
        String transactionId = uuid.substring(0, 10);
        return transactionId;
    }

    public static String convertUserToXML(User user) {
        try {
            // Create an XmlMapper with specific configuration
            XmlMapper xmlMapper = new XmlMapper();

            // Configure the mapper to exclude the XML declaration
            xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false);

            return xmlMapper.writeValueAsString(user);
        } catch (Exception e) {
            throw new TapTapClientException("Error in converting user to xml.", e);
        }
    }

    public static String convertObjectToJson(Object object) {
        Gson gson = new Gson();
        // Convert the object to JSON
        return gson.toJson(object);
    }

    public static String getMd5FromString(String password) {
        MessageDigest md = null;
        String algorithm = "MD5";

        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new TapTapClientException("Invalid algorithm for hashing: "+algorithm, e);

        }
        byte[] messageDigest = md.digest(password.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        String hashtext = number.toString(16);
        return hashtext;
    }

    public static String prepareBasicAuthCreds(User user) {

        if (user.email() != null && user.password() != null) {
            // Combine username and password with a colon and encode in Base64
//            String credentials = user.email() + ":" + getMd5FromString(user.password());
            String credentials = user.email() + ":" + getMd5FromString(user.password());
            return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        } else
            throw new TapTapClientException("Unable to prepare basic auth. Please validate if credentails are provided.");
    }
}
