package root.authorithationservicestoryline.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.*;

public record UserUpdateDTO(

        @NotBlank(message = "Current username is required")
        String сurrentUsername,

        @NotBlank(message = "Current username is required")
        String username,

        @NotBlank(message = "name is required")
        String name,

        @Nonnull
        @Min(value = 1,message = "Age cannot be less than 1")
        @Max(value = 120,message = "Age cannot be more than 120")
        Integer age
) {
}
