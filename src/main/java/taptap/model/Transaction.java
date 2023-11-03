package taptap.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record Transaction(
        @NotNull(message = "Recipient cannot be null")
        Recipient recipient,

        @Positive(message = "Amount must be greater than 0")
        double amount
) {}
