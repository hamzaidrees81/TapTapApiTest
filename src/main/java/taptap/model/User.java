package taptap.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@JacksonXmlRootElement(localName = "user")
public record User(
        @JacksonXmlProperty(localName = "firstName")
        @JsonProperty("first_name")
        @NotBlank(message = "First name cannot be blank")
        @Size(max = 50, message = "First name length must not exceed 50 characters")
        String firstName,

        @JacksonXmlProperty(localName = "lastName")
        @JsonProperty("last_name")
        @NotBlank(message = "Last name cannot be blank")
        @Size(max = 50, message = "Last name length must not exceed 50 characters")
        String lastName,

        @JacksonXmlProperty(localName = "email")
        @JsonProperty("email")
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        @JacksonXmlProperty(localName = "password")
        @JsonProperty("password")
        @NotBlank(message = "Password cannot be blank")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 8 characters long and contain at least one letter and one digit")
        String password
) {
}
