package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")

public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    @RequestMapping("/")
    public Collection<User> findAll() {
        return users.values();
    }

    public boolean checkIfUserEmailAlreadyExists(String emailToFind) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(emailToFind));
    }

    public boolean checkIfUserLoginAlreadyExists(String loginToFind) {
        return users.values().stream()
                .anyMatch(user -> user.getLogin().equals(loginToFind));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PostMapping
    @RequestMapping("/create")
    public User create(@Valid @RequestBody User user) {
        if (checkIfUserEmailAlreadyExists(user.getEmail())) {
            throw new ValidationException("Этот имейл уже используется");
        }
        if (checkIfUserLoginAlreadyExists(user.getLogin())) {
            throw new ValidationException("Этот логин уже используется");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата рождения");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    @RequestMapping("/update")
    public ResponseEntity<String> update(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(user.getId())) {
            throw new ValidationException("Указан id несуществующего пользователя");
        }
        User exitsUser = users.get(user.getId());
        if (!exitsUser.getEmail().equals(user.getEmail())) {
            throw new ValidationException("Изменение email не допускается");
        }
        if (!exitsUser.getLogin().equals(user.getLogin())) {
            throw new ValidationException("Изменение login не допускается");
        }
        users.put(user.getId(), user);
        return new ResponseEntity<>("Данные пользователя обновлены " + user, HttpStatus.OK);
    }
}
