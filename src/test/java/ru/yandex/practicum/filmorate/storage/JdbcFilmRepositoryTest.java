package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(JdbcUserRepository.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcUserRepositoryTest")
class JdbcFilmRepositoryTest {
    private final JdbcFilmRepository jdbcFilmRepository;
    private HashMap<Integer, Film> filmMap = new HashMap<>();

    static User getTestUser(Integer id, String email, String login, String name, LocalDate birthDay) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthDay);
        return user;
    }

    @Test
    void findAll() {
    }

    @Test
    void getPopularFilms() {
    }

    @Test
    void getByIds() {
    }

    @Test
    void getFilmById() {
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void getLikes() {
    }

    @Test
    void addLike() {
    }

    @Test
    void removeLike() {
    }
}