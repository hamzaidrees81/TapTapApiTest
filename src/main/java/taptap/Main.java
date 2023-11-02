package taptap;

import taptap.util.Utils;

public class Main {

	public static void main(String[] args) {

		Main main = new Main();
		main.test();
	}
	
	public void test()
	{
		TaptapBankClient client = new TapTapBankClientImpl();

		User user = createUniqueUser();
		client.addUser(user);
	}

	public User createUniqueUser()
	{
		String postfix = Utils.generateRandomUserPostfix();
		String firstName = "John"+postfix;
		String lastName = "Smith"+postfix;
		String email = "john"+postfix+"@mail.com";
		String password = "securePassword";
		
		UserCredentails credentails = new UserCredentails(email, password, Utils.getMd5FromString(password));
		User user = new User(credentails, firstName, lastName);
		return user;
	}
}
