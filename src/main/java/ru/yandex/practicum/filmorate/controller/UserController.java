package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")

public class UserController {
    static final Logger log =
            LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @RequestMapping(path = "", method = RequestMethod.GET)
    public Collection<User> findAll() {
        return users.values();
    }


    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateUserBeforeUpdate(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Указан id несуществующего пользователя");
        }
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    public User create(@Valid @RequestBody User user) {
        try {
            user.setId(getNextId());
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Пользователь успешно создан {}", user);
            return user;
        } catch (Exception e) {
            log.debug("Ошибка создания пользователя", e.toString());
            throw e;
        }
    }

    @RequestMapping(path = "", method = RequestMethod.PUT)
    public User update(@Valid @RequestBody User user) {
        try {
            validateUserBeforeUpdate(user);
            users.put(user.getId(), user);
            log.info("Пользователь успешно обновлен {}", user);
            return user;
        } catch (Exception e) {
            log.debug("Ошибка обновления пользователя", e.toString());
            throw e;
        }
    }
}
