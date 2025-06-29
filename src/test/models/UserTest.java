package models;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    User user;
    @Test
    void userModelCommonTest() {
        User user = new User(1L, "usermail@mail.ru", "user-login", "user-name",
                LocalDate.of(1980, 1, 10) );
        assertEquals(1L, user.getId());
        assertEquals("usermail@mail.ru", user.getEmail());
        assertEquals("user-login", user.getLogin());
        assertEquals("user-name", user.getName());
    }


}