package taptap.client;

import taptap.model.Recipient;
import taptap.model.User;

public interface TaptapBankClient {

	public boolean addUser(User user);
	public boolean makeTransaction(User user, Recipient recipient);
	public boolean upgradeUser(User user);
}


