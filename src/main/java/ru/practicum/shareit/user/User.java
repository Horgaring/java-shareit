package ru.practicum.shareit.user;

import lombok.Data;
import ru.practicum.shareit.validation.Validators;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private Long id;
    private String name;
    private String email;

    public void validateUser() {
        Validators.validateNotBlank(this.name, "name");
        Validators.validateEmail(this.email);
    }

}
