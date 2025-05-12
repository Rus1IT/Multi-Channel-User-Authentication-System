package root.authorithationservicestoryline.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.*;

public record UserRegistrationDTO(

        @NotBlank(message = "username is required")
        String username,

        @NotBlank(message = "name is required")
        String name,

        @Nonnull
        @Min(value = 1,message = "Age cannot be less than 1")
        @Max(value = 120,message = "Age cannot be more than 120")
        Integer age,

        @NotBlank(message = "password is required")
        String password,

        @NotBlank(message = "registration is required (email,phone_number,github or google account)")
        @Nonnull
        String registration_account,

        @NotBlank(message = "registration_type is required (email,phone_number,google,github")
        @Nonnull
        String registration_type,

        @NotBlank(message = "role is required (ADMIN,USER")
        @Nonnull
        String role
) {
}
