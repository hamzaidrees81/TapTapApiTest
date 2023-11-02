package taptap;

public interface TaptapBankClient {

	public Response addUser(User user);
	public Response makeTransaction(UserCredentails user, Recipient recipient);
	public Response upgradeUser(UserCredentails userCredentails);	
}


