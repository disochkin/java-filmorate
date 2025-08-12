package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(JdbcUserRepository.class)
@DisplayName("JdbcUserRepositoryTest")
class JdbcUserRepositoryTest {
    private final JdbcUserRepository jdbcUserRepository;
    private final HashMap<Integer, User> userMap = new HashMap<>();

    static User getTestUser(Integer id, String email, String login, String name, LocalDate birthDay) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthDay);
        return user;
    }

    @BeforeEach
    public void initUsers() {
        String[][] userData = {{"1", "email_1@domain.ru", "testUserLogin1", "testUserName1", "2001-01-01"}, {"2", "email_2@domain.ru", "testUserLogin2", "testUserName2", "2002-02-02"}, {"3", "email_3@domain.ru", "testUserLogin3", "testUserName3", "2003-03-03"}};
        for (int i = 0; i < userData.length; i++) {
            userMap.put(i + 1, getTestUser(Integer.parseInt(userData[i][0]), userData[i][1], userData[i][2],
                    userData[i][3], LocalDate.parse(userData[i][4])));
        }
    }

    @Test
    void save() {
        String[] newUserData = {"5", "email_5@domain.ru", "testUserLogin5", "testUserName5", "2005-05-05"};
        User newUser = getTestUser(Integer.parseInt(newUserData[0]), newUserData[1], newUserData[2],
                newUserData[3], LocalDate.parse(newUserData[4]));
        User savedUser = jdbcUserRepository.save(newUser);
        Optional<User> userFromDbOpt = jdbcUserRepository.getUserById(savedUser.getId());
        assertThat(userFromDbOpt)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    void update() {
        String[] userDataToUpdate = {"4", "email_4@domain.ru", "testUserLogin4", "testUserName4", "2004-04-04"};
        User userToUpdate = userMap.get(3);
        userToUpdate.setEmail(userDataToUpdate[1]);
        userToUpdate.setLogin(userDataToUpdate[2]);
        userToUpdate.setName(userDataToUpdate[3]);
        userToUpdate.setBirthday(LocalDate.parse(userDataToUpdate[4]));
        jdbcUserRepository.update(userToUpdate);
        Optional<User> updatedUser = jdbcUserRepository.getUserById(userToUpdate.getId());
        assertThat(updatedUser)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(userToUpdate);
    }

    @Test
    void findAll() {
        Collection<User> actualUsers = jdbcUserRepository.findAll();
        assertThat(actualUsers).containsExactlyInAnyOrderElementsOf(userMap.values());
    }

    @Test
    void getUserByIds() {
        List<Integer> testUserId = new ArrayList<>();
        testUserId.add(1);
        testUserId.add(2);
        Collection<User> actualUsers = jdbcUserRepository.getUserByIds(testUserId);
        List<User> expectedUsers = testUserId.stream()
                .filter(userMap::containsKey)
                .map(userMap::get)
                .collect(Collectors.toList());
        assertThat(actualUsers).containsExactlyInAnyOrderElementsOf(expectedUsers);
    }

    @Test
    @DisplayName("Поиск пользователя по id")
    void getUserByIdTest() {
        int testUserId = 1;
        Optional<User> userOptional = jdbcUserRepository.getUserById(testUserId);
        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(userMap.get(testUserId));
    }

    @Test
    void addFriend_and_removeFriend() {
        Integer userId = 1;
        Integer friendId = 2;
        jdbcUserRepository.addFriend(userId, friendId);
        assertThat(jdbcUserRepository.getFriends(userId))
                .hasSize(1)
                .contains(friendId);

        jdbcUserRepository.removeFriend(userId, friendId);
        assertThat(jdbcUserRepository.getFriends(userId))
                .hasSize(0);
    }
}