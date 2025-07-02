package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserModelTest {

    private Validator validator;
    private User user;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        user = new User();
        user.setName("John Dow");
        user.setLogin("userLogin");
        user.setBirthday(LocalDate.of(1990,12,30));
        user.setEmail("box@example.com");
    }

    @Test
    public void userEmailReqTest() {
        user.setEmail(null);
        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void userLoginTest() {
        user.setLogin("l o g i n");
        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void userBirthdayTest() {
        User user = new User();
        user.setBirthday(LocalDate.now().plusDays(1));
        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}