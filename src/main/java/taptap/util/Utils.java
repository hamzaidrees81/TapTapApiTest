package taptap.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.google.gson.Gson;
import taptap.exception.TapTapClientException;
import taptap.model.User;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class Utils {

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

    public String ObjectToJson(Object object) {
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
            // TODO Auto-generated catch block
            throw new TapTapClientException("Invalid algorithm for hashing: "+algorithm, e);

        }
        byte[] messageDigest = md.digest(password.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        String hashtext = number.toString(16);
        return hashtext;
    }

}
