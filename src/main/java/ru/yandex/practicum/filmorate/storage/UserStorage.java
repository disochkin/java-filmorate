package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Service
public interface UserStorage {
    User create(User user);

    Collection<User> findAll();

    User getUserById(Long userId);

    User update(User user);
}
