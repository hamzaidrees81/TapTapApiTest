package taptap.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import taptap.exception.DataValidationException;
import taptap.model.Recipient;
import taptap.model.Transaction;
import taptap.model.User;
import taptap.test.Main;

public class Validator {

    private static final Logger logger = LoggerFactory.getLogger(Validator.class.getName());

    public static void validateUser(User user)
    {
        if(user.firstName()==null || user.firstName().isEmpty() || user.firstName().length()<2)
        {
            throw new DataValidationException("First name cannot be empty.");
        }
        if (user.lastName() == null || user.lastName().isEmpty() || user.lastName().length() < 2) {
            throw new DataValidationException("Last name cannot be empty or less than 2 characters.");
        }

        if (user.email() == null || user.email().isEmpty()) {
            throw new DataValidationException("Email cannot be empty.");
        }
        if (user.password() == null || user.password().isEmpty() || user.password().length() < 8
//                || !user.password().matches(".*[A-Za-z].*")|| !user.password().matches(".*\\d.*")
        ) {
            throw new DataValidationException("Password must be at least 8 characters long and contain at least one letter and one digit.");
        }
    }

    public static void validateRecipient(Recipient recipient) {
        if (recipient.firstname() == null || recipient.firstname().isEmpty()) {
            throw new DataValidationException("First name cannot be empty.");
        }

        if (recipient.lastname() == null || recipient.lastname().isEmpty()) {
            throw new DataValidationException("Last name cannot be empty.");
        }

        if (recipient.email() == null || recipient.email().isEmpty()) {
            throw new DataValidationException("Recipient Email cannot be empty.");
        }
    }

    public static void validateTransaction(Transaction transaction) {

        if (transaction.recipient() == null) {
            throw new DataValidationException("Recipient cannot be null.");
        }

        validateRecipient(transaction.recipient());

        if (transaction.amount() <= 0) {
            throw new DataValidationException("Amount must be greater than 0.");
        }
    }

}
