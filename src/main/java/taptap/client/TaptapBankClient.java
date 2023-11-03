package taptap.client;

import taptap.model.Recipient;
import taptap.model.Transaction;
import taptap.model.User;

public interface TaptapBankClient {

	public boolean addUser(User user);
	public boolean makeTransaction(User user, Transaction transaction);
	public boolean upgradeUser(User user);
}


