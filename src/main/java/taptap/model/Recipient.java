package taptap.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record Recipient(
        @NotBlank(message = "First name cannot be blank")
        @Size(max = 50, message = "First name length must not exceed 50 characters")
        String firstname,

        @NotBlank(message = "Last name cannot be blank")
        @Size(max = 50, message = "Last name length must not exceed 50 characters")
        String lastname,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        @Pattern(regexp = "^\\d{0,15}$", message = "Invalid phone number format")
        String phone,

        @Size(max = 20, message = "Nickname length must not exceed 20 characters")
        String nickname,

        @Size(max = 50, message = "Wallet length must not exceed 50 characters")
        String wallet
) {}
