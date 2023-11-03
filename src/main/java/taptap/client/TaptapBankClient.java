package taptap.client;

import taptap.model.Recipient;
import taptap.model.Transaction;
import taptap.model.User;

import javax.validation.Valid;

public interface TaptapBankClient {

	public boolean addUser(@Valid User user);
	public boolean makeTransaction(User user, Transaction transaction);
	public boolean upgradeUser(User user);
}


