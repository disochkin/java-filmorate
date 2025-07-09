package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NoSuchElementException("Указан id несуществующего пользователя");
        }
    }

    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NoSuchElementException("Указан id несуществующего пользователя");
        }
        return users.get(userId);
    }

    public User update(User user) {
        validateUser(user);
        users.put(user.getId(), user);
        return user;
    }
}
