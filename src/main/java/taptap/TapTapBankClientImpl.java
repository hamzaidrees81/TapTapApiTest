package taptap;

import java.util.logging.Logger;

import taptap.util.Configs;
import taptap.util.HttpUtils;
import taptap.util.HttpUtils.HttpStatus;

public class TapTapBankClientImpl implements TaptapBankClient {

	private static final Logger logger = Logger.getLogger(TapTapBankClientImpl.class.getName());

	@Override
	public Response addUser(User user) {

		// check if user has all the required field
		if (user == null || user.firstName() == null || user.firstName().trim().isEmpty() || user.lastName() == null
				|| user.lastName().trim().isEmpty() || user.userAuth() == null || user.userAuth().email() == null
				|| user.userAuth().email().trim().isEmpty() || user.userAuth().password() == null
				|| user.userAuth().password().trim().isEmpty()) {
			return new Response(HttpStatus.BAD_REQUEST.getCode(), "");
		}

		// if all parameters are available, make a request
		Response response = makeAddUserRequest(user);

		return response;
	}

	private Response makeAddUserRequest(User user) {

		String xmlPayload = generateUserPayload(user);

		String contentType = "application/xml";

		String url = Configs.getInstance().getCreateUserApi();
		Integer responseCode = HttpUtils.makeRequest(url, contentType, xmlPayload);

		return createResponseFromResponseCode(responseCode);
	}


	private String generateUserPayload(User user) {
		return "<user>\n" + "    <firstName>" + user.firstName() + "</firstName>\n" + "    <lastName>" + user.lastName()
				+ "</lastName>\n" + "    <email>" + user.userAuth().email() + "</email>\n" + "    <password>"
				+ user.userAuth().password() + "</password>\n" + "</user>";
	}

	@Override
	public Response makeTransaction(UserCredentails user, Recipient recipient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response upgradeUser(UserCredentails userCredentails) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	public  Response createResponseFromResponseCode(Integer response) {

		// create response from status code
		if (response == null || response == HttpUtils.HttpStatus.BAD_REQUEST.getCode()) {
			return new Response(HttpUtils.HttpStatus.BAD_REQUEST.getCode(),
					HttpUtils.HttpStatus.BAD_REQUEST.getReasonPhrase());
		}

		if (response == HttpUtils.HttpStatus.OK.getCode()) {
			return new Response(response, HttpUtils.HttpStatus.OK.getReasonPhrase());
		}

		if (response == HttpUtils.HttpStatus.UNAUTHORIZED.getCode()) {
			return new Response(response, HttpUtils.HttpStatus.UNAUTHORIZED.getReasonPhrase());
		}

		if (response == HttpUtils.HttpStatus.NOT_FOUND.getCode()) {
			return new Response(response, HttpUtils.HttpStatus.NOT_FOUND.getReasonPhrase());
		}

		if (response == HttpUtils.HttpStatus.INTERNAL_SERVER_ERROR.getCode()) {
			return new Response(response, HttpUtils.HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		}

		if (response == HttpUtils.HttpStatus.NO_CONTENT.getCode()) {
			return new Response(response, HttpUtils.HttpStatus.NO_CONTENT.getReasonPhrase());
		}

		if (response == HttpUtils.HttpStatus.CREATED.getCode()) {
			return new Response(response, HttpUtils.HttpStatus.CREATED.getReasonPhrase());
		} else
			return new Response(HttpUtils.HttpStatus.BAD_REQUEST.getCode(),
					HttpUtils.HttpStatus.BAD_REQUEST.getReasonPhrase());
	}


}
