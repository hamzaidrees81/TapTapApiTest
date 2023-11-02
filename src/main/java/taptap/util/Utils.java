package taptap.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class Utils {

	public static String generateTransactionId() {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		// Take the first 10 characters of the UUID
		String transactionId = uuid.substring(0, 10);
		return transactionId;
	}
	
	public static String generateRandomUserPostfix() {
		String postfix = UUID.randomUUID().toString().replaceAll("-", "");
		// Take the first 10 characters of the UUID
		return postfix.substring(0, 3);
	}


	public static String getMd5FromString(String password)
	{
	        MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        byte[] messageDigest = md.digest(password.getBytes());
	        BigInteger number = new BigInteger(1, messageDigest);
	        String hashtext = number.toString(16);
	        return hashtext;
	}
	
}
